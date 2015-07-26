package wyvern.target.corewyvernIL.expression;

import java.util.List;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T acceptVisitor(ASTVisitor <T> emitILVisitor,
			Environment env, OIREnvironment oirenv) {
		return emitILVisitor.visit(env, oirenv, this);
	}

	@Override
	public Value interpret(EvalContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}
}
