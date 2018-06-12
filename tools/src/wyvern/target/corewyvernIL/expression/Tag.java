package wyvern.target.corewyvernIL.expression;

import wyvern.target.corewyvernIL.decl.TypeDeclaration;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.TagType;

public class Tag {
    private ObjectValue object;
    private String memberName;
    
    public Tag(ObjectValue v, String memberName) {
        object = v;
        this.memberName = memberName;
    }
    
    boolean isSubTag(Tag t, EvalContext ctx) {
        if (object == t.object && memberName.equals(t.memberName)) {
            return true;
        }
        Tag parent = getParent(ctx);
        return (parent == null) ? false : parent.isSubTag(t, ctx);
    }
    
    Tag getParent(EvalContext ctx) {
        TypeDeclaration td = (TypeDeclaration) object.findDecl(memberName, true);
        if (!(td.getSourceType() instanceof TagType)) {
            return null;
        }
        NominalType nt = ((TagType) td.getSourceType()).getParentType(null);
        return nt == null ? null : nt.getTag(ctx);
    }
}
