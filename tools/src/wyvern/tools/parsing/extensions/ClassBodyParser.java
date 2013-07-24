package wyvern.tools.parsing.extensions;

import java.util.LinkedList;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.*;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.rawAST.IntLiteral;
import wyvern.tools.rawAST.Line;
import wyvern.tools.rawAST.LineSequence;
import wyvern.tools.rawAST.Parenthesis;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.rawAST.RawASTVisitor;
import wyvern.tools.rawAST.StringLiteral;
import wyvern.tools.rawAST.Symbol;
import wyvern.tools.rawAST.Unit;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.expressions.TupleObject;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.interfaces.EnvironmentExtender;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.Pair;
import static wyvern.tools.errors.ToolError.reportError;

public class ClassBodyParser implements RawASTVisitor<Environment, Pair<Environment, ContParser>> {
	private ClassBodyParser() { }
	private static ClassBodyParser instance = new ClassBodyParser();
	public static ClassBodyParser getInstance() { return instance; }

	@Override
	public Pair<Environment, ContParser> visit(LineSequence node, Environment env) {
		if (node.children.size() == 0) {
			ToolError.reportError(ErrorMessage.UNEXPECTED_EMPTY_BLOCK, node);
		}

		final LinkedList<ContParser> contParsers = new LinkedList<>();
		final LinkedList<Pair<Pair<TypedAST, DeclParser>, Pair<ExpressionSequence, Environment>>> unparsed = new LinkedList<>();
		Environment newEnv = Environment.getEmptyEnvironment();

		for (RawAST line : node.children) {
			if (!(line instanceof ExpressionSequence))
				ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, node);

			Pair<ExpressionSequence, Environment> ctx = new Pair<>((ExpressionSequence) line, env);
			Pair<TypedAST, LineParser> fetched = getParser(ctx);

			if (!(fetched.second instanceof DeclParser))
				throw new RuntimeException();

			if (!(fetched.second instanceof TypeExtensionParser)) {
				unparsed.add(new Pair<>(new Pair<>(fetched.first, (DeclParser)fetched.second),ctx));
				continue;
			}

			Pair<Environment,ContParser> partiallyParsed =
					parseLineInt(new Pair<>((ExpressionSequence)line,env));
			newEnv = newEnv.extend(partiallyParsed.first);
			contParsers.add(partiallyParsed.second);
		}

		final Environment finalNewEnv = newEnv;
		return new Pair<Environment, ContParser>(newEnv, new ClassBodyContParser(finalNewEnv, contParsers, unparsed));
	}

	private Pair<TypedAST, LineParser> getParser(Pair<ExpressionSequence,Environment> ctx) {
		ExpressionSequence node = ctx.first;
		Environment env = ctx.second;
		// TODO: should not be necessary, but a useful sanity check
		// FIXME: gets stuck on atomics like {$L $L} which were sometimes leftover by lexer from comments.
		if (node.children.size() == 0) {
			ToolError.reportError(ErrorMessage.UNEXPECTED_EMPTY_BLOCK, node);
		}
		TypedAST first = node.getFirst().accept(BodyParser.getInstance(), env);
		ctx.first = ctx.first.getRest();

		return new Pair<>(first, first.getLineParser());
	}

	private Pair<Environment,ContParser> parseLineInt(Pair<ExpressionSequence,Environment> ctx) {
		ExpressionSequence node = ctx.first;
		Environment env = ctx.second;
		// TODO: should not be necessary, but a useful sanity check
		// FIXME: gets stuck on atomics like {$L $L} which were sometimes leftover by lexer from comments.
		if (node.children.size() == 0) {
			ToolError.reportError(ErrorMessage.UNEXPECTED_EMPTY_BLOCK, node);
		}

		TypedAST first = node.getFirst().accept(BodyParser.getInstance(), env);
		LineParser parser = first.getLineParser();

		if (!(parser instanceof DeclParser))
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, node);
		DeclParser dp = (DeclParser)parser;

		ExpressionSequence rest = node.getRest();
		ctx.first = rest;

		if (parser == null)
			ToolError.reportError(ErrorMessage.UNEXPECTED_EMPTY_BLOCK, node);

		// if first is a special form, get the expression continuation parser and use it to parse the rest
		return dp.parseDeferred(first, ctx);
	}


	@Override
	public Pair<Environment, ContParser> visit(IntLiteral node, Environment arg) {
		reportError(ErrorMessage.UNEXPECTED_INPUT, node);
		return null;
	}

	@Override
	public Pair<Environment, ContParser> visit(StringLiteral node, Environment arg) {
		reportError(ErrorMessage.UNEXPECTED_INPUT, node);
		return null;
	}

	@Override
	public Pair<Environment, ContParser> visit(Symbol node, Environment arg) {
		reportError(ErrorMessage.UNEXPECTED_INPUT, node);
		return null;
	}

	@Override
	public Pair<Environment, ContParser> visit(Unit node, Environment arg) {
		reportError(ErrorMessage.UNEXPECTED_INPUT, node);
		return null;
	}

	@Override
	public Pair<Environment, ContParser> visit(Line node, Environment arg) {
		reportError(ErrorMessage.UNEXPECTED_INPUT, node);
		return null;
	}

	@Override
	public Pair<Environment, ContParser> visit(Parenthesis node, Environment arg) {
		reportError(ErrorMessage.UNEXPECTED_INPUT, node);
		return null;
	}

	public static class ClassBodyContParser implements RecordTypeParser {
		private final Environment finalNewEnv;
		private final LinkedList<ContParser> contParsers;
		private final LinkedList<Pair<Pair<TypedAST, DeclParser>, Pair<ExpressionSequence, Environment>>> unparsed;
		public Environment internalEnv;

		public ClassBodyContParser(Environment finalNewEnv, LinkedList<ContParser> contParsers, LinkedList<Pair<Pair<TypedAST, DeclParser>, Pair<ExpressionSequence, Environment>>> unparsed) {
			this.finalNewEnv = finalNewEnv;
			this.contParsers = contParsers;
			this.unparsed = unparsed;
			internalEnv = finalNewEnv;
		}

		public Environment getInternalEnv() {
			return internalEnv;
		}

		@Override
		public void parseTypes(EnvironmentResolver r) {
			for (ContParser parser : contParsers)
				if (parser instanceof RecordTypeParser)
					((RecordTypeParser)parser).parseTypes(r);
		}

		@Override
		public void parseInner(EnvironmentResolver r) {
			for (Pair<Pair<TypedAST, DeclParser>, Pair<ExpressionSequence, Environment>> toParse : unparsed) {
				Pair<Environment, ContParser> ret = toParse.first.second.parseDeferred(toParse.first.first, toParse.second);
				internalEnv = internalEnv.extend(ret.first);
				contParsers.add(ret.second);
			}
			for (ContParser parser : contParsers)
				if (parser instanceof RecordTypeParser)
					((RecordTypeParser)parser).parseInner(r);
		}

		@Override
		public TypedAST parse(EnvironmentResolver env) {
			LinkedList<TypedAST> seqBody = new LinkedList<TypedAST>();
			boolean isExtender = true;
			for (ContParser cp : contParsers) {
				if (cp instanceof RecordTypeParser) {
					((RecordTypeParser) cp).parseTypes(env);
					((RecordTypeParser) cp).parseInner(env);
				}
				TypedAST parsed = cp.parse(env);
				seqBody.add(parsed);
				if (!(parsed instanceof EnvironmentExtender))
					isExtender = false;
			}

			if (seqBody.size() == 0)
				return seqBody.getFirst();
			else if (!isExtender)
				return new Sequence(seqBody);
			else
				return new DeclSequence(seqBody);
		}
	}
}
