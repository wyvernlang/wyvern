package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.type.ValueType;

public class EmptyGenContext extends GenContext {
    private InterpreterState state;

    protected EmptyGenContext() {
        super(null);
    }

    public EmptyGenContext(InterpreterState state) {
        super(null);
        this.state = state;
    }

    @Override
    public ValueType lookupTypeOf(String varName) {
        throw new RuntimeException("Variable " + varName + " not found");
    }

    @Override
    public ValueType lookupTypeOf(Variable v) {
        throw new RuntimeException("Variable " + v.getName() + " not found");
    }

    @Override
    protected String endToString() {
        return "]";
    }

    @Override
    public Path getContainerForTypeAbbrev(String typeName) {
        return null;
    }

    @Override
    public CallableExprGenerator getCallableExprRec(String varName, GenContext origCtx) {
        throw new RuntimeException("Variable " + varName + " not found");
    }

    @Override
    public InterpreterState getInterpreterState() {
        return state;
    }
}
