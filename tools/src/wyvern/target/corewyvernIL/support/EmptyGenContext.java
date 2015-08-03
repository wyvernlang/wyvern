package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.type.ValueType;

public class EmptyGenContext extends GenContext {

	@Override
	public Expression lookupExp(String varName) {
		throw new RuntimeException("Variable " + varName + " not found");
	}

	@Override
	public ValueType lookup(String varName) {
		throw new RuntimeException("Variable " + varName + " not found");
	}

}
