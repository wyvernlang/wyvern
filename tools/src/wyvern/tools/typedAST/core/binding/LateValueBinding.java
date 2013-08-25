package wyvern.tools.typedAST.core.binding;


import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;

import java.util.concurrent.atomic.AtomicReference;

public class LateValueBinding extends ValueBinding {
	private AtomicReference<Value> valueRef;

	public LateValueBinding(String name, AtomicReference<Value> valueRef, Type valType) {
		super(name, valType);
		this.valueRef = valueRef;
	}

	@Override
	public TypedAST getUse() {
		return valueRef.get();
	}

	@Override
	public Value getValue(Environment env) {
		return valueRef.get();
	}

	@Override
	public void setValue(Value newValue) {
		//assert value == null;
		//assert newValue != null;
		valueRef.set(newValue);
	}
}
