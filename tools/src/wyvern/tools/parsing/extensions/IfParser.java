package wyvern.tools.parsing.extensions;

import java.util.ArrayList;
import java.util.LinkedList;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.BodyParser;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.LineSequenceParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.rawAST.LineSequence;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.typedAST.abs.CachingTypedAST;
import wyvern.tools.typedAST.core.Keyword;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.binding.KeywordNameBinding;
import wyvern.tools.typedAST.core.expressions.IfExpr;
import wyvern.tools.typedAST.core.values.BooleanConstant;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Bool;
import wyvern.tools.util.Pair;
import wyvern.tools.util.TreeWriter;

public class IfParser implements LineParser {
	private IfParser() { }
	private static IfParser instance = new IfParser();
	public static IfParser getInstance() { return instance; }
	
	private class IfClause extends CachingTypedAST implements IfExpr.IfClause {
		private TypedAST clause;
		private TypedAST body;
		private boolean isThen = false;
		public IfClause(TypedAST clause, TypedAST body, boolean isThen) {
			this.clause = clause;
			this.body = body;
			this.isThen = isThen;
		}
		@Override
		public Value evaluate(Environment env) {
			return body.evaluate(env);
		}
		@Override
		public LineParser getLineParser() {
			return null;
		}
		@Override
		public LineSequenceParser getLineSequenceParser() {
			return null;
		}
		@Override
		public void writeArgsToTree(TreeWriter writer) {
		}
		@Override
		public FileLocation getLocation() {
			return body.getLocation();
		}
		@Override
		public boolean satisfied(Environment env) {
			TypedAST returned = clause.evaluate(env);
			if (!(returned instanceof BooleanConstant))
				ToolError.reportError(ErrorMessage.TYPE_CANNOT_BE_APPLIED, this);
			return ((BooleanConstant)returned).getValue();
		}
		@Override
		protected Type doTypecheck(Environment env) {
			if (!(clause.typecheck(env) instanceof Bool)) {
				ToolError.reportError(ErrorMessage.TYPE_CANNOT_BE_APPLIED, this);
			}
			return body.typecheck(env);
		}
		public boolean getIsThen() { return isThen; }
	}
	private class ThenParser implements LineParser {
		
		private TypedAST clause;
		private Environment env;

		public ThenParser(TypedAST clause, Environment env) {
			this.clause = clause;
			this.env = env;
		}

		@Override
		public TypedAST parse(TypedAST first,
				Pair<ExpressionSequence, Environment> ctx) {
			TypedAST body = ParseUtils.extractLines(ctx).accept(BodyParser.getInstance(), ctx.second);
			return new IfClause(clause,body,true);
		}
	}
	private class ElseParser implements LineParser {
		private Environment env;

		public ElseParser(Environment env) {
			this.env = env;
		}

		@Override
		public TypedAST parse(TypedAST first,
				Pair<ExpressionSequence, Environment> ctx) {
			TypedAST clause = new BooleanConstant(true);
			if (ParseUtils.checkFirst("if",ctx)) {
				ParseUtils.parseSymbol("if", ctx);
				clause =  ParseUtils.parseCond(ctx);
			}
			TypedAST body = ParseUtils.extractLines(ctx).accept(BodyParser.getInstance(), ctx.second);
			return new IfClause(clause,body,false);
		}
	}
	
	@Override
	public TypedAST parse(TypedAST first,
			Pair<ExpressionSequence, Environment> ctx) {
		TypedAST thenClause = ParseUtils.parseCond(ctx);
		
		Environment bodyEnv = Environment.getEmptyEnvironment();
		bodyEnv = bodyEnv.extend(new KeywordNameBinding("then", new Keyword(new ThenParser(thenClause,ctx.second))));
		bodyEnv = bodyEnv.extend(new KeywordNameBinding("else", new Keyword(new ElseParser(ctx.second))));
		
		TypedAST body = ParseUtils.extractLines(ctx).accept(BodyParser.getInstance(), bodyEnv);
		LinkedList<IfExpr.IfClause> result = new LinkedList<IfExpr.IfClause>();
		if (body instanceof IfClause) {
			if (!((IfClause)body).getIsThen())
				 ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, body);
			result.add((IfExpr.IfClause) body);
		} else if (body instanceof Sequence) {
			boolean isFirst = true;
			for (TypedAST elem : (Sequence)body) {
				if (!(elem instanceof IfClause))
					ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, elem);
				if (isFirst) {
					if (!((IfClause)elem).getIsThen())
						ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, elem);
					isFirst = false;
				}
				result.add((IfClause)elem);
			}
		}
		return new IfExpr(result, body.getLocation());
	}

}
