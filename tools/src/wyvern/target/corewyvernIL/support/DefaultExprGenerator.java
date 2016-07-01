package wyvern.target.corewyvernIL.support;

import java.util.List;

import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.HasLocation;

public class DefaultExprGenerator implements CallableExprGenerator {

	private Expression expr;
	
	public DefaultExprGenerator(Expression e) {
		expr = e;
	}
	
	@Override
	public Expression genExpr() {
		return expr;
	}

	@Override
	public Expression genExprWithArgs(List<Expression> args, HasLocation loc) {
		Expression e = genExpr();
		return new MethodCall(e, Util.APPLY_NAME, args, loc);
	}

	@Override
	public DefDeclType getDeclType(TypeContext ctx) {
		Expression e = genExpr();
		ValueType vt = e.typeCheck(ctx);
		return (DefDeclType)vt.findDecl(Util.APPLY_NAME, ctx).adapt(View.from(expr, ctx));
	}

}
