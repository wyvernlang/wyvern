package wyvern.target.corewyvernIL.expression;

import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;
import wyvern.tools.types.Environment;

public class Cast extends Expression{

	private Expression toCastExpr;

	
	public Cast(Expression toCastExpr, ValueType exprType) {
		super(exprType);
		this.toCastExpr = toCastExpr;
	}

	public Expression getToCastExpr() {
		return toCastExpr;
	}

	public void setToCastExpr(Expression toCastExpr) {
		this.toCastExpr = toCastExpr;
	}

	@Override
	public ValueType typeCheck(Environment env) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T acceptVisitor(ASTVisitor <T> emitILVisitor,
			wyvern.target.corewyvernIL.Environment env, OIREnvironment oirenv) {
		return emitILVisitor.visit(env, oirenv, this);
	}
}
