package wyvern.tools.parsing.extensions;

import java.util.LinkedList;
import java.util.Optional;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.*;
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
import wyvern.tools.util.CompilationContext;
import wyvern.tools.util.TreeWriter;

public class IfParser implements LineParser {
	private IfParser() { }
	private static IfParser instance = new IfParser();
	public static IfParser getInstance() { return instance; }
	
	private class IfClause extends IfExpr.IfClause {
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
		protected Type doTypecheck(Environment env, Optional<Type> expected) {
			if (!(clause.typecheck(env, Optional.empty()) instanceof Bool)) {
				ToolError.reportError(ErrorMessage.TYPE_CANNOT_BE_APPLIED, this);
			}
			return body.typecheck(env, Optional.empty());
		}
		public boolean getIsThen() { return isThen; }
		
		@Override
		public TypedAST getClause() {
			return clause;
		}
		@Override
		public TypedAST getBody() {
			return body;
		}

		@Override
		protected TypedAST createInstance(TypedAST clause, TypedAST body) {
			return new IfClause(clause, body, isThen);
		}
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
							  CompilationContext ctx) {
			TypedAST body = ParseUtils.extractLines(ctx).accept(new BodyParser(ctx), env);
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
							  CompilationContext ctx) {
			TypedAST clause = new BooleanConstant(true);
            CompilationContext ictx = ctx.copyTokens(ctx.getEnv().getExternalEnv());
			if (ParseUtils.checkFirst("if",ictx)) {
				ParseUtils.parseSymbol("if", ictx);
				clause = ParseUtils.parseCond(ictx);
			}
			TypedAST body = ParseUtils.extractLines(ictx).accept(new BodyParser(ctx), env);
            ctx.setTokens(ictx.getTokens());
			return new IfClause(clause,body,false);
		}
	}
	
	@Override
	public TypedAST parse(TypedAST first,
						  CompilationContext ctx) {
		TypedAST thenClause = ParseUtils.parseCond(ctx);
		
		Environment bodyEnv = Environment.getEmptyEnvironment();
		bodyEnv = bodyEnv.extend(new KeywordNameBinding("then", new Keyword(new ThenParser(thenClause, ctx.getEnv()))));
		bodyEnv = bodyEnv.extend(new KeywordNameBinding("else", new Keyword(new ElseParser(ctx.getEnv()))));
		
		TypedAST body = ParseUtils.extractLines(ctx).accept(new BodyParser(ctx), ctx.getEnv().setInternalEnv(bodyEnv));
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
		return new IfExpr(result, thenClause.getLocation());
	}

}
