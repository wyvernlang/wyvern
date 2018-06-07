package wyvern.target.oir.declarations;

import wyvern.target.oir.ASTVisitor;
import wyvern.target.oir.OIREnvironment;

public class OIRFloatType extends OIRInterface  {
  private static OIRFloatType type = new OIRFloatType();
  private static String stringRep = "float";

  protected OIRFloatType() {
      super(new OIREnvironment(null), "float", "this", null);
  }

  public static OIRFloatType getFloatType() {
      return type;
  }

  @Override
  public <S, T> T acceptVisitor(ASTVisitor<S, T> visitor, S state) {
      return visitor.visit(state, this);
  }

  @Override
  public String toString() {
      return OIRFloatType.stringRep;
  }

  @Override
  public String getName() {
      return toString();
  }
}
