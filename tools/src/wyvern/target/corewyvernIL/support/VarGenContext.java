package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.type.ValueType;

public class VarGenContext extends GenContext {
	private String var;
	private Expression expr;
	private GenContext genContext;
	private ValueType type;
	
	public VarGenContext(String var, Expression expr, ValueType type, GenContext genContext) {
		this.var = var;
		this.expr = expr;
		this.genContext = genContext;
	}

	@Override
	public Expression lookupExp(String varName) {
		if (varName.equals(var))
			return expr;
		else
			return genContext.lookupExp(varName);
	}

	@Override
	public ValueType lookup(String varName) {
		if (varName.equals(var))
			return type;
		else
			return genContext.lookup(varName);
	}
}
