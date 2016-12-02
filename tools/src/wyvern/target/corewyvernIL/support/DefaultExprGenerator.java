package wyvern.target.corewyvernIL.support;

import java.util.List;

import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.HasLocation;

public class DefaultExprGenerator implements CallableExprGenerator {

	private IExpr expr;
	
	public DefaultExprGenerator(IExpr iExpr) {
		expr = iExpr;
	}
	
	@Override
	public IExpr genExpr() {
		return expr;
	}

	@Override
	public IExpr genExprWithArgs(List<? extends IExpr> args, HasLocation loc) {
		IExpr e = genExpr();
		return new MethodCall(e, Util.APPLY_NAME, args, loc);
	}

	@Override
	public DefDeclType getDeclType(TypeContext ctx) {
		IExpr e = genExpr();
		ValueType vt = e.typeCheck(ctx);
		return (DefDeclType) vt.findDecl(Util.APPLY_NAME, ctx);
		// return (DefDeclType)vt.findDecl(Util.APPLY_NAME, ctx).adapt(View.from(expr, ctx));
	}

}
