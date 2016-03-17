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
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.HasLocation;

public class InvocationExprGenerator implements CallableExprGenerator {

	private final Expression receiver;
	private final DeclType declType;
	
	public InvocationExprGenerator(Expression receiver, String operationName, GenContext ctx) {
		this.receiver = receiver;
		ValueType rt = receiver.typeCheck(ctx);
		declType = rt.findDecl(operationName, ctx);
		if (declType == null)
			throw new RuntimeException("typechecking error: operation " + operationName + " not found");
	}
	
	@Override
	public Expression genExpr() {
		if (declType instanceof ValDeclType || declType instanceof VarDeclType) {
			return new FieldGet(receiver, declType.getName());
		} else {
			throw new RuntimeException("eta-expansion of a method reference not implemented");
		}
	}

	@Override
	public Expression genExprWithArgs(List<Expression> args, HasLocation loc) {
		if (declType instanceof ValDeclType || declType instanceof VarDeclType) {
			Expression e = genExpr();
			return new MethodCall(e, "apply", args, loc);
		} else {
			return new MethodCall(receiver, declType.getName(), args, loc);			
		}
	}

	@Override
	public List<FormalArg> getExpectedArgTypes(TypeContext ctx) {
		if (declType instanceof ValDeclType || declType instanceof VarDeclType) {
			Expression e = genExpr();
			ValueType vt = e.typeCheck(ctx);
			DefDeclType dt = (DefDeclType)vt.findDecl("apply", ctx);
			return dt.getFormalArgs();
		} else {
			DefDeclType dt = (DefDeclType) declType;
			return dt.getFormalArgs();
		}
	}
}
