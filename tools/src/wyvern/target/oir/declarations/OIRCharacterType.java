package wyvern.target.oir.declarations;

import wyvern.target.oir.ASTVisitor;
import wyvern.target.oir.OIREnvironment;

public class OIRCharacterType extends OIRType {

    private static OIRCharacterType type = new OIRCharacterType();
    private static String stringRep = "char";

    protected OIRCharacterType() {
        super(new OIREnvironment(null));
    }

    public static OIRCharacterType getCharacterType() {
        return type;
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> visitor, S state) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String toString() {
        return OIRCharacterType.stringRep;
    }

    @Override
    public String getName() {
        return toString();
    }
}
