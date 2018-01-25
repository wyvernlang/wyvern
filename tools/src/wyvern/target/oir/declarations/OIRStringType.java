package wyvern.target.oir.declarations;

import wyvern.target.oir.ASTVisitor;
import wyvern.target.oir.OIREnvironment;

public class OIRStringType extends OIRType {

    private static OIRStringType type = new OIRStringType();
    private static String stringRep = "string";

    protected OIRStringType() {
        super(new OIREnvironment(null));
    }

    public static OIRStringType getStringType() {
        return type;
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> visitor, S state) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String toString() {
        return OIRStringType.stringRep;
    }

    @Override
    public String getName() {
        return toString();
    }
}
