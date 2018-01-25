package wyvern.target.oir.declarations;

import wyvern.target.oir.ASTVisitor;
import wyvern.target.oir.OIREnvironment;

public class OIRBooleanType extends OIRType {
    private static OIRBooleanType type = new OIRBooleanType();
    private static String stringRep = "bool";

    protected OIRBooleanType() {
        super(new OIREnvironment(null));
    }

    public static OIRBooleanType getBooleanType() {
        return type;
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> visitor, S state) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String toString() {
        return OIRBooleanType.stringRep;
    }

    @Override
    public String getName() {
        return toString();
    }
}
