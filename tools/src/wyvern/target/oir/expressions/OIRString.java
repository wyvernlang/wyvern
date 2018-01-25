package wyvern.target.oir.expressions;

import wyvern.target.oir.ASTVisitor;
import wyvern.target.oir.OIREnvironment;
import wyvern.target.oir.declarations.OIRStringType;
import wyvern.target.oir.declarations.OIRType;

public class OIRString extends OIRLiteral implements OIRValue {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public OIRString(String value) {
        super();
        this.value = value;
    }

    @Override
    public OIRType typeCheck(OIREnvironment oirEnv) {
        setExprType(OIRStringType.getStringType());
        return getExprType();
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }
}
