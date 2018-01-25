package wyvern.target.oir.declarations;

import wyvern.target.oir.ASTVisitor;
import wyvern.target.oir.OIREnvironment;

public class OIRRationalType extends OIRType {
    private static OIRRationalType type = new OIRRationalType();
    private static String stringRep = "rational";

    protected OIRRationalType() {
        super(new OIREnvironment(null));
    }

    public static OIRRationalType getRationalType() {
        return type;
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> visitor, S state) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String toString() {
        return OIRRationalType.stringRep;
    }

    @Override
    public String getName() {
        return toString();
    }
}
