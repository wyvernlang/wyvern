package wyvern.tools.typedAST.core.binding;

import wyvern.tools.types.Type;

public abstract class AbstractBinding implements Binding {
    private String name;
    private Type type;

    public AbstractBinding(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "{" + name + " : " + type + "}";
    }
}
