package wyvern.target.oir.declarations;

import wyvern.target.oir.ASTVisitor;
import wyvern.target.oir.OIRAST;

public class OIRForward extends OIRAST {
    private OIRType type;
    private String field;

    public OIRForward(OIRType type, String field) {
        super();
        this.type = type;
        this.field = field;
    }
    public OIRType getType() {
        return type;
    }
    public void setType(OIRType type) {
        this.type = type;
    }
    public String getField() {
        return field;
    }
    public void setField(String field) {
        this.field = field;
    }
    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> visitor, S state) {
        // TODO Auto-generated method stub
        return null;
    }
}
