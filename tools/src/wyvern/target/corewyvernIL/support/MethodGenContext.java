package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.BindingSite;
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;

public class MethodGenContext extends GenContext {
    private BindingSite objectSite;
    private String methodName;
    private FileLocation loc;

    public MethodGenContext(String methodName, BindingSite objectSite, GenContext genContext, FileLocation loc) {
        super(genContext);
        this.objectSite = objectSite;
        this.methodName = methodName;
    }

    @Override
    public boolean isPresent(String varName, boolean isValue) {
        if (isValue && this.methodName.equals(varName)) {
            return true;
        } else {
            return super.isPresent(varName, isValue);
        }
    }

    @Override
    public String toString() {
        return "GenContext[" + endToString();
    }

    @Override
    public String endToString() {
        return methodName + " = " + objectSite + '.' + methodName +  ", " + getNext().endToString();
    }

    @Override
    public ValueType lookupTypeOf(String varName) {
        return getNext().lookupTypeOf(varName);
    }

    @Override
    public ValueType lookupTypeOf(Variable v) {
        return getNext().lookupTypeOf(v);
    }

    @Override
    public Path getContainerForTypeAbbrev(String typeName) {
        return getNext().getContainerForTypeAbbrev(typeName);
    }

    @Override
    public CallableExprGenerator getCallableExprRec(String varName, GenContext origCtx) {
        if (this.methodName.equals(varName)) {
            return new InvocationExprGenerator(new Variable(objectSite), varName, origCtx, loc);
        } else {
            return getNext().getCallableExprRec(varName, origCtx);
        }
    }

}
