package wyvern.target.oir.declarations;

import wyvern.target.oir.ASTVisitor;
import wyvern.target.oir.OIREnvironment;

public class OIRIntegerType extends OIRInterface {
    private static OIRIntegerType type = new OIRIntegerType();
    private static String stringRep = "int";

    protected OIRIntegerType() {
        super(new OIREnvironment(null), "int", "this", null);
    }

    public static OIRIntegerType getIntegerType() {
        return type;
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }

    @Override
    public String toString() {
        return OIRIntegerType.stringRep;
    }

    @Override
    public String getName() {
        return toString();
    }
}
