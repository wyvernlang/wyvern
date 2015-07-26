package wyvern.target.corewyvernIL.expression;

import wyvern.target.corewyvernIL.ASTNode;
import wyvern.target.corewyvernIL.EmitOIR;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.ValueType;

public abstract class Expression extends ASTNode implements EmitOIR {
	
	private ValueType exprType;
	public abstract ValueType typeCheck(TypeContext ctx);
	// TODO: cheating, but I'll fix it later!
	public abstract Value interpret(EvalContext ctx);

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
