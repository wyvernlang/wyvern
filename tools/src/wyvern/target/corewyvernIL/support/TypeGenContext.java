package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.type.ValueType;

public class TypeGenContext extends GenContext {

    String typeName;
    Path objName;

    public TypeGenContext(String typeName, Path objName, GenContext genContext) {
        super(genContext);
        this.typeName = typeName;
        this.objName = objName;
    }

    public TypeGenContext(String typeName, String newName, GenContext genContext) {
        super(genContext);
        this.typeName = typeName;
        this.objName = new Variable(newName);
    }

    @Override
    public Path getContainerForTypeAbbrev(String typeName) {
        if(this.typeName.equals(typeName)) return objName;
        else return getNext().getContainerForTypeAbbrev(typeName);
    }

    @Override
    public String toString() {
        return "GenContext[" + endToString();
    }

    @Override
    public String endToString() {
        return typeName + " : " + objName  + ", " + getNext().endToString();
    }

    @Override
    public ValueType lookupType(String varName) {
        return getNext().lookupType(varName);
    }

    @Override
    public CallableExprGenerator getCallableExprRec(String varName, GenContext origCtx) {
        return getNext().getCallableExprRec(varName, origCtx);
    }
}
