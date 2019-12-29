package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.BindingSite;
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.type.ValueType;

/** Used to look up the paths associated with a type abbreviation or an effect abbreviation */
public class TypeOrEffectGenContext extends GenContext {
    private String typeName;
    private Path objName;

    @Override
    public String desugarType(Path var, String member) {
        if (var.equals(objName) && member.equals(typeName)) {
            return typeName;
        }
        return super.desugarType(var, member);
    }

    public TypeOrEffectGenContext(String typeName, Path objName, GenContext genContext) {
        super(genContext);
        Util.check(typeName != null, "null type");
        this.typeName = typeName;
        this.objName = objName;
    }

    public TypeOrEffectGenContext(String typeName, BindingSite site, GenContext genContext) {
        super(genContext);
        Util.check(typeName != null, "null type");
        this.typeName = typeName;
        this.objName = new Variable(site);
    }

    @Override
    public Path getContainerForTypeAbbrev(String typeName) {
        if (this.typeName.equals(typeName)) {
            return objName;
        } else {
            return getNext().getContainerForTypeAbbrev(typeName);
        }
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
    public ValueType lookupTypeOf(String varName) {
        return getNext().lookupTypeOf(varName);
    }

    @Override
    public ValueType lookupTypeOf(Variable v) {
        return getNext().lookupTypeOf(v);
    }

    @Override
    public CallableExprGenerator getCallableExprRec(String varName, GenContext origCtx) {
        return getNext().getCallableExprRec(varName, origCtx);
    }

    @Override
    public boolean isPresent(String varName, boolean isValue) {
        if (!isValue && this.typeName.equals(varName)) {
            return true;
        } else {
            return super.isPresent(varName, isValue);
        }
    }

}
