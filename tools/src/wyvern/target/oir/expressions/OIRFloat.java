package wyvern.target.oir.expressions;

import java.math.BigDecimal;

import wyvern.target.oir.ASTVisitor;
import wyvern.target.oir.OIREnvironment;
import wyvern.target.oir.declarations.OIRFloatType;
import wyvern.target.oir.declarations.OIRType;

public class OIRFloat extends OIRLiteral implements OIRValue  {
  private BigDecimal value;

  public OIRFloat(BigDecimal bigDecimal) {
    super();
    this.value = bigDecimal;
  }

  public BigDecimal getValue() {
    return value;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }

  public <S, T> T acceptVisitor(ASTVisitor<S, T> visitor, S state)  {
    return visitor.visit(state, this);
  }

  public OIRType typeCheck(OIREnvironment oirEnv) {
    setExprType(OIRFloatType.getFloatType());
    return getExprType();
  }

}
