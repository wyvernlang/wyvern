package wyvern.target.corewyvernIL.expression;

import wyvern.target.corewyvernIL.ASTNode;
import wyvern.target.corewyvernIL.EmitOIR;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.types.Environment;

public abstract class Expression extends ASTNode implements EmitOIR {
	
	private ValueType exprType;
	public abstract ValueType typeCheck(Environment env);

	protected Expression (ValueType exprType)
	{
		this.exprType = exprType;
	}
	
	protected Expression ()
	{
		this.exprType = exprType;
	}
	
	public ValueType getExprType() {
		return exprType;
	
	}
	protected void setExprType(ValueType exprType) {
		this.exprType = exprType;
	}
}
