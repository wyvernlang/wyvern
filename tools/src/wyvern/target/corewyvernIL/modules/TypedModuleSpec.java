package wyvern.target.corewyvernIL.modules;

import wyvern.target.corewyvernIL.type.ValueType;

public class TypedModuleSpec extends ModuleSpec {
	private final ValueType type;
	private final String definedTypeName;

	public TypedModuleSpec(String qualifiedName, ValueType type, String typeName) {
		super(qualifiedName);
		this.type = type;
		this.definedTypeName = typeName;
	}

	public ValueType getType() {
		return type;
	}

	/**
	 * @return the name of the type defined by this module, if any; otherwise null
	 */
    public String getDefinedTypeName() {
        return definedTypeName;
    }
}
