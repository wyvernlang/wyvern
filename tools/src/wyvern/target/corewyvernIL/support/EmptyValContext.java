package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.type.ValueType;

public class EmptyValContext extends EvalContext {
	public Value lookup(String varName) {
		throw new RuntimeException("Variable " + varName + " not found");
	}
}
