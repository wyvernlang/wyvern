package wyvern.target.corewyvernIL.expression;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.decl.DefDeclaration;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DeclTypeWithResult;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.Type;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.errors.ToolError;

public class MethodCall extends Expression {

	private Expression objectExpr;
	private String methodName;
	private List<Expression> args;
	
	public MethodCall(Expression objectExpr, String methodName,
			List<Expression> args, HasLocation location) {
		super(location != null ? location.getLocation():null);
		this.objectExpr = objectExpr;
		this.methodName = methodName;
		this.args = args;
		// sanity check
		if (args.size() > 0 && args.get(0) == null)
			throw new NullPointerException("invariant: no null args");
	}
	
	@Override
	public void doPrettyPrint(Appendable dest, String indent) throws IOException {
		objectExpr.doPrettyPrint(dest,indent);
		dest.append('.').append(methodName).append('(');
		boolean first = true;
		for (Expression arg : args) {
			if (first)
				first = false;
			else
				dest.append(", ");
			arg.doPrettyPrint(dest, indent);
		}
		dest.append(')');
	}
	
	public Expression getObjectExpr() {
		return objectExpr;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public List<Expression> getArgs() {
		return args;
	}
	
	@Override
	public ValueType typeCheck(TypeContext ctx) {
		ValueType ot = objectExpr.typeCheck(ctx);
		StructuralType st = ot.getStructuralType(ctx);
		DeclType dt = st.findDecl(methodName, ctx);
		if (dt == null)
			ToolError.reportError(ErrorMessage.NO_SUCH_METHOD, this, methodName);
		if (!(dt instanceof DefDeclType))
			ToolError.reportError(ErrorMessage.NOT_A_METHOD, this, methodName);
		DefDeclType ddt = (DefDeclType)dt;
		View v = View.from(objectExpr, ctx);
		// check argument compatibility
		for (int i = 0; i < args.size(); ++i) {
			Expression e = args.get(i);
			Type argType = ddt.getFormalArgs().get(i).getType().adapt(v);
			ValueType actualType = e.typeCheck(ctx); 
			if (!actualType.isSubtypeOf(argType, ctx)) {
				ToolError.reportError(ErrorMessage.ACTUAL_FORMAL_TYPE_MISMATCH, this, actualType.toString(), argType.toString());
            }
		}
		// compute result type
		this.setExprType(ddt.getResultType(v));
		return getExprType();
	}

	@Override
	public <T> T acceptVisitor(ASTVisitor <T> emitILVisitor,
			Environment env, OIREnvironment oirenv) {
		return emitILVisitor.visit(env, oirenv, this);
	}

	@Override
	public Value interpret(EvalContext ctx) {
		Invokable receiver = (Invokable)objectExpr.interpret(ctx);
		List<Value> argValues = new ArrayList<Value>(args.size());
		for (int i = 0; i < args.size(); ++i) {
			Expression e = args.get(i);
			argValues.add(e.interpret(ctx));
		}
		return receiver.invoke(methodName, argValues, ctx);		
	}
}
