package wyvern.tools.typedAST.core.binding;

import wyvern.tools.types.Type;

public class NameBindingImpl extends AbstractBinding implements NameBinding {
    public NameBindingImpl(String name, Type type) {
        super(name, type);
    }

    @Override
    public String toString() {
        return "{" + getName() + " : " + getType() + "}";
    }

}