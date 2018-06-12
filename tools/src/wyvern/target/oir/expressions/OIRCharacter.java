package wyvern.target.oir.expressions;

import wyvern.target.oir.ASTVisitor;
import wyvern.target.oir.OIREnvironment;
import wyvern.target.oir.declarations.OIRCharacterType;
import wyvern.target.oir.declarations.OIRType;

public class OIRCharacter extends OIRLiteral implements OIRValue {
    private char value;

    public char getValue() {
        return value;
    }

    public void setValue(char value) {
        this.value = value;
    }

    public OIRCharacter(char value) {
        super();
        this.value = value;
    }

    @Override
    public OIRType typeCheck(OIREnvironment oirEnv) {
        setExprType(OIRCharacterType.getCharacterType());
        return getExprType();
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }
}
