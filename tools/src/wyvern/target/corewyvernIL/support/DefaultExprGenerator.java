package wyvern.target.corewyvernIL.support;

import java.util.List;

import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.HasLocation;

public class DefaultExprGenerator implements CallableExprGenerator {
    private IExpr expr;

    public DefaultExprGenerator(IExpr iExpr) {
        expr = iExpr;
    }

    @Override
    public IExpr genExpr(FileLocation loc) {
        return expr.locationHint(loc);
    }

    @Override
    public IExpr genExprWithArgs(List<? extends IExpr> args, HasLocation loc, boolean isTailCall, TypeContext ctx) {
        IExpr e = genExpr(loc.getLocation());
        return new MethodCall(e, Util.APPLY_NAME, args, loc, isTailCall);
    }

    @Override
    public DefDeclType getDeclType(TypeContext ctx) {
        IExpr e = genExpr(null);
        ValueType vt = e.typeCheck(ctx, null);
        return (DefDeclType) vt.findDecl(Util.APPLY_NAME, ctx);
        // return (DefDeclType)vt.findDecl(Util.APPLY_NAME, ctx).adapt(View.from(expr, ctx));
    }
}
