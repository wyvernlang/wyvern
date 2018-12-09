package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.BindingSite;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.type.ValueType;

public class VarBindingContext extends TypeContext {
    private TypeContext previous;
    private BindingSite binding;
    private ValueType type;

    public VarBindingContext(BindingSite binding, ValueType type, TypeContext typeContext) {
        this.binding = binding;
        this.type = type;
        previous = typeContext;
    }

    @Override
    public boolean isPresent(String varName, boolean isValue) {
        if (isValue && this.binding.getName().equals(varName)) {
            return true;
        } else {
            return super.isPresent(varName, isValue);
        }
    }

    @Override
    public ValueType lookupTypeOf(String varName) {
        if (varName.equals(this.binding.getName())) {
            return type;
        } else {
            return previous.lookupTypeOf(varName);
        }
    }
    
    @Override
    public ValueType lookupTypeOf(Variable v) {
        if (v.getSite() != null) {
            if (v.getSite() == binding) {
                return type;
            } else {
                return getNext().lookupTypeOf(v);
            }
        } else {
            if (v.getName().equals(binding.getName())) {
                v.siteFound(binding);
                return type;
            } else {
                return getNext().lookupTypeOf(v);
            }
        }
    }

    @Override
    protected TypeContext getNext() {
        return previous;
    }

    @Override
    public String toString() {
        return "VarBindingContext[" + endToString();
    }

    @Override
    protected String endToString() {
        return binding.toString() + " : " + type + ", " + getNext().endToString();
    }
}
