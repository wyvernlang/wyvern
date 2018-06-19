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

    /** Returns the signature of this module.
     * In the case of a module def this signature will have a single method, apply().
     * In the case the module defines a type, this signature will have a single type definition within it that is the type,
     * and that type definition will have the name given by getDefinedTypeName().
     */
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
