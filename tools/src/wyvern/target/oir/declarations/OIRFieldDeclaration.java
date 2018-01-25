package wyvern.target.oir.declarations;

import wyvern.target.oir.ASTVisitor;

public class OIRFieldDeclaration extends OIRMemberDeclaration {
    private String name;
    private OIRType type;
    private boolean isFinal = false;

    public OIRFieldDeclaration(String name, OIRType type) {
        super();
        this.name = name;
        this.type = type;
    }

    public OIRFieldDeclaration(String name, OIRType type, boolean isFinal) {
        super();
        this.name = name;
        this.type = type;
        this.isFinal = isFinal;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public OIRType getType() {
        return type;
    }
    public void setType(OIRType type) {
        this.type = type;
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }

    public boolean isFinal() {
        return isFinal;
    }
}
