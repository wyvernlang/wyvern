package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.BindingSite;
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.type.ValueType;

public abstract class TypeContext {
    public TypeContext extend(BindingSite binding, ValueType type) {
        return new VarBindingContext(binding, type, this);
    }

    public boolean isAssumedSubtype(ValueType t1, ValueType t2) {
        if (getNext() == null) {
            return false;
        } else {
            return getNext().isAssumedSubtype(t1, t2);
        }
    }

    /*
     * Returns the type of this variable in the context
     */
    public abstract ValueType lookupTypeOf(String varName);
    public abstract ValueType lookupTypeOf(Variable v);

    public boolean isPresent(String varName, boolean isValue) {
        if (getNext() == null) {
            return false;
        } else {
            return getNext().isPresent(varName, isValue);
        }
    }

    public static TypeContext empty() {
        return theEmpty;
    }

    public String desugarType(Path var, String member) {
        if (getNext() == null) {
            return null;
        } else {
            return getNext().desugarType(var, member);
        }
    }

    private static final TypeContext theEmpty = new EmptyTypeContext();

    protected abstract TypeContext getNext();
    protected abstract String endToString();
}
