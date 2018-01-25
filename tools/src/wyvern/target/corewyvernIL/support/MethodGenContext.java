package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;

public class MethodGenContext extends GenContext {
    private String objectName;
    private String methodName;
    private FileLocation loc;

    public MethodGenContext(String methodName, String objectName, GenContext genContext, FileLocation loc) {
        super(genContext);
        this.objectName = objectName;
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
        return methodName + " = " + objectName + '.' + methodName +  ", " + getNext().endToString();
    }

    @Override
    public ValueType lookupTypeOf(String varName) {
        return getNext().lookupTypeOf(varName);
    }

    @Override
    public Path getContainerForTypeAbbrev(String typeName) {
        return getNext().getContainerForTypeAbbrev(typeName);
    }

    @Override
    public CallableExprGenerator getCallableExprRec(String varName, GenContext origCtx) {
        if (this.methodName.equals(varName)) {
            return new InvocationExprGenerator(new Variable(objectName), varName, origCtx, loc);
        } else {
            return getNext().getCallableExprRec(varName, origCtx);
        }
    }

}
