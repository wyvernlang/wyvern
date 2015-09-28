package wyvern.target.corewyvernIL.expression;

import wyvern.target.corewyvernIL.ASTNode;
import wyvern.target.corewyvernIL.EmitOIR;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.ValueType;

public abstract class Expression extends ASTNode implements EmitOIR,IExpr {
	
	private ValueType exprType;
	@Override
	public abstract ValueType typeCheck(TypeContext ctx);
	public abstract Value interpret(EvalContext ctx);

	protected Expression (ValueType exprType)
	{
		this.exprType = exprType;
	}
	
	protected Expression ()
	{
		// if this constructor is used, exprType must be set later!
	}
	
	public ValueType getExprType() {
		return exprType;
	
	}
	protected void setExprType(ValueType exprType) {
		this.exprType = exprType;
	}

}
