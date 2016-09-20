package wyvern.target.corewyvernIL.support;

import java.util.List;

import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.decltype.ConcreteTypeMember;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.decltype.ValDeclType;
import wyvern.target.corewyvernIL.decltype.VarDeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.FieldGet;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.errors.ToolError;

public class InvocationExprGenerator implements CallableExprGenerator {

	private final IExpr receiver;
	private final DeclType declType;
	private final FileLocation location;
	
	public InvocationExprGenerator(IExpr iExpr, String operationName, GenContext ctx, FileLocation loc) {
		this.receiver = iExpr;
		ValueType rt = iExpr.typeCheck(ctx);
		DeclType dt = rt.findDecl(operationName, ctx);
		location = loc;
		if (dt == null)
			ToolError.reportError(ErrorMessage.NO_SUCH_METHOD, loc, operationName);
		declType = dt.adapt(View.from(iExpr, ctx));
	}
	
	@Override
	public Expression genExpr() {
		if (declType instanceof ValDeclType || declType instanceof VarDeclType) {
			return new FieldGet(receiver, declType.getName(), location);
		} else {
			throw new RuntimeException("eta-expansion of a method reference not implemented");
		}
	}

	@Override
	public IExpr genExprWithArgs(List<? extends IExpr> args, HasLocation loc) {
		if (declType instanceof ValDeclType || declType instanceof VarDeclType) {
			IExpr e = genExpr();
			return new MethodCall(e, Util.APPLY_NAME, args, loc);
		} else {
			return new MethodCall(receiver, declType.getName(), args, loc);			
		}
	}

	@Override
	public DefDeclType getDeclType(TypeContext ctx) {
		if (declType instanceof ValDeclType || declType instanceof VarDeclType) {
			Expression e = genExpr();
			ValueType vt = e.typeCheck(ctx);
			return (DefDeclType)vt.findDecl(Util.APPLY_NAME, ctx).adapt(View.from(receiver, ctx));
		} else if (declType instanceof DefDeclType) {
			return (DefDeclType) declType;
		} else {
			ToolError.reportError(ErrorMessage.NOT_A_METHOD, location, declType.getName());
			throw new RuntimeException("can't get here");
		}
	}
}
