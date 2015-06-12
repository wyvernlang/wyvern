package wyvern.target.corewyvernIL.expression;

import wyvern.target.corewyvernIL.astvisitor.EmitILVisitor;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.types.Environment;

public class Cast extends Expression{

	private Expression toCastExpr;

	
	public Cast(Expression toCastExpr) {
		super();
		this.toCastExpr = toCastExpr;
	}

	public Expression getToCastExpr() {
		return toCastExpr;
	}

	public void setToCastExpr(Expression toCastExpr) {
		this.toCastExpr = toCastExpr;
	}

	@Override
	public java.lang.String acceptEmitILVisitor(EmitILVisitor emitILVisitor,
			wyvern.target.corewyvernIL.Environment env) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ValueType typeCheck(Environment env) {
		// TODO Auto-generated method stub
		return null;
	}
}
