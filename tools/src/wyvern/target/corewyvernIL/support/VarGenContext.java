package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.type.ValueType;

public class VarGenContext extends GenContext {
	private String var;
	private Expression expr;
	private ValueType type;
	
	public VarGenContext(String var, Expression expr, ValueType type, GenContext genContext) {
		super(genContext);
		this.var = var;
		this.expr = expr;
		this.type = type;
	}
	
	@Override
	public boolean isPresent(String varName) {
		if (this.var.equals(varName))
			return true;
		else
			return super.isPresent(varName);
	}

@Override
	public String toString() {
		return "GenContext[" + endToString();
	}
	
	@Override
	public String endToString() {
		return var + " : " + type + " = " + expr + ", " + getNext().endToString();
	}
	
	@Override
	public ValueType lookupType(String varName) {
		if (varName.equals(var))
			return type;
		else
			return getNext().lookupType(varName);
	}
	
	@Override
	public String getContainerForTypeAbbrev(String typeName) {
		return getNext().getContainerForTypeAbbrev(typeName);
	}

	@Override
	public CallableExprGenerator getCallableExprRec(String varName, GenContext origCtx) {
		if (varName.equals(var))
			return new DefaultExprGenerator(expr);
		else {
			return getNext().getCallableExprRec(varName, origCtx);
		}
	}	
}
