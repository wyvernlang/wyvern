package wyvern.tools.parsing;

import java.util.HashSet;
import java.util.LinkedList;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;
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
import wyvern.tools.typedAST.interfaces.EnvironmentExtender;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.util.CompilationContext;
import wyvern.tools.util.Pair;
import static wyvern.tools.errors.ToolError.reportError;

public class DeclarationParser implements RawASTVisitor<Environment, Pair<Environment, ContParser>> {
	private DeclarationParser() { }
	private static DeclarationParser instance = new DeclarationParser();
	public static DeclarationParser getInstance() { return instance; }

	@Override
	public Pair<Environment, ContParser> visit(LineSequence node, Environment env) {
		if (node.children.size() == 0) {
			ToolError.reportError(ErrorMessage.UNEXPECTED_EMPTY_BLOCK, node);
		}
		
		final LinkedList<ContParser> contParsers = new LinkedList<ContParser>();
		Environment newEnv = Environment.getEmptyEnvironment();
		
		for (RawAST line : node.children) {
			if (!(line instanceof ExpressionSequence))
				ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, node);
			
			Pair<Environment,ContParser> partiallyParsed = 
					parseLineInt(new CompilationContext((ExpressionSequence)line,env));
			newEnv = newEnv.extend(partiallyParsed.first);
			contParsers.add(partiallyParsed.second);
		}

		return new Pair<Environment, ContParser>(newEnv, new RecordTypeParser.RecordTypeParserBase() {
			private HashSet<Integer> parseInnerCalled = new HashSet<>();
			@Override
			public void doParseTypes(EnvironmentResolver r) {
				for (ContParser parser : contParsers)
					if (parser instanceof RecordTypeParser)
						((RecordTypeParser) parser).parseTypes(r);
			}

			@Override
            public void doParseInner(EnvironmentResolver r) {
				for (ContParser parser : contParsers)
					if (parser instanceof RecordTypeParser)
						((RecordTypeParser)parser).parseInner(r);
            }

            @Override
			public TypedAST parse(EnvironmentResolver env) {
				LinkedList<TypedAST> seqBody = new LinkedList<TypedAST>();
				boolean isExtender = true;
				for (ContParser cp : contParsers) {
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
		});
	}
	
	private Pair<Environment,ContParser> parseLineInt(CompilationContext ctx) {
		ExpressionSequence node = ctx.getTokens();
		Environment env = ctx.getEnv();
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
		ctx.setTokens(rest);
		
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

}
