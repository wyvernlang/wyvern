package wyvern.target.corewyvernIL.modules;

import wyvern.target.corewyvernIL.type.ValueType;

public class TypedModuleSpec extends ModuleSpec {
	private final ValueType type;

	public TypedModuleSpec(String qualifiedName, ValueType type) {
		super(qualifiedName);
		this.type = type;
	}

	public ValueType getType() {
		return type;
	}
}
