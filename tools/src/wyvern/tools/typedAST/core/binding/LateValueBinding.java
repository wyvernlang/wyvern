package wyvern.tools.typedAST.core.binding;


import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.Reference;

import java.util.concurrent.atomic.AtomicReference;

public class LateValueBinding extends ValueBinding {
	private Reference<Value> valueRef;

	public LateValueBinding(String name, final AtomicReference<Value> valueRef, final Type valType) {
		super(name, valType);
		this.valueRef = new Reference<Value>() {
			public Value get() {
				return valueRef.get();
			}

			public void set(Value value) {
				valueRef.set(value);
			}
		};
	}

	public LateValueBinding(String name, Reference<Value> valueRef, Type type) {
		super(name, type);
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
