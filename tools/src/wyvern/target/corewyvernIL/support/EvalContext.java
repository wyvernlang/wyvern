package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.BindingSite;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.type.ValueType;

public abstract class EvalContext extends TypeContext {
    public EvalContext extend(BindingSite site, Value v) {
        return new VarEvalContext(site, v, this);
    }

    @Override
    public ValueType lookupTypeOf(String varName) {
        return lookupValue(varName).getType();
    }

    @Override
    public ValueType lookupTypeOf(Variable v) {
        // TODO: fix me, use Variable
        return lookupValue(v.getName()).getType();
    }

    
    @Override
    public String toString() {
        return "EvalContext[" + endToString();
    }

    public abstract Value lookupValue(String varName);

    public static EvalContext empty() {
        return theEmpty;
    }

    private static final EvalContext theEmpty = new EmptyValContext();

    public abstract String endToString();
}
