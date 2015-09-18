package wyvern.target.corewyvernIL.expression;

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

public class MethodCall extends Expression {

	private Expression objectExpr;
	private String methodName;
	private List<Expression> args;
	
	public MethodCall(Expression objectExpr, String methodName,
			List<Expression> args) {
		super();
		this.objectExpr = objectExpr;
		this.methodName = methodName;
		this.args = args;
	}
	
	@Override
	public String toString() {
		return objectExpr.toString() + "." + methodName + "(" + args + ")";
	}

	public Expression getObjectExpr() {
		return objectExpr;
	}
	
	public void setObjectExpr(Expression objectExpr) {
		this.objectExpr = objectExpr;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	public List<Expression> getArgs() {
		return args;
	}
	
	public void setArgs(List<Expression> args) {
		this.args = args;
	}

	@Override
	public ValueType typeCheck(TypeContext env) {
		ValueType ot = objectExpr.typeCheck(env);
		StructuralType st = ot.getStructuralType();
		DeclType dt = st.findDecl(methodName);
		if (dt == null)
			throw new RuntimeException("method not found");
		if (!(dt instanceof DefDeclType))
			throw new RuntimeException("invoking a non-method");
		DefDeclType ddt = (DefDeclType)dt;
		View v = View.from(objectExpr, env);
		// check argument compatibility
		for (int i = 0; i < args.size(); ++i) {
			Expression e = args.get(i);
			Type argType = ddt.getFormalArgs().get(i).getType().adapt(v);
			if (!e.typeCheck(env).isSubtypeOf(argType, env))
				throw new RuntimeException("argument type doesn't match");
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
		ObjectValue receiver = (ObjectValue)objectExpr.interpret(ctx);
		DefDeclaration dd = (DefDeclaration) receiver.findDecl(methodName);
		EvalContext methodCtx = ctx;
		for (int i = 0; i < args.size(); ++i) {
			Expression e = args.get(i);
			methodCtx = methodCtx.extend(dd.getFormalArgs().get(i).getName(), e.interpret(ctx));
		}
		return dd.getBody().interpret(methodCtx);
	}
}
