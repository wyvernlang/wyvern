package wyvern.target.corewyvernIL.expression;

import wyvern.target.corewyvernIL.ASTNode;
import wyvern.target.corewyvernIL.emitIL;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.types.Environment;

public abstract class Expression extends ASTNode implements emitIL {
	
	private ValueType exprType;
	public abstract ValueType typeCheck(Environment env);

	public ValueType getExprType() {
		return exprType;
	
	}
	protected void setExprType(ValueType exprType) {
		this.exprType = exprType;
	}
}
