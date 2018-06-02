package wyvern.target.corewyvernIL.expression;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.math.BigDecimal;

import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.effects.EffectAccumulator;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;

public class FloatLiteral extends Literal implements Invokable  {
  private final BigDecimal value;
  @Override
  public int hashCode() {
      return value.hashCode();
  }
  @Override
  public boolean equals(Object obj) {
      if (this == obj) {
          return true;
      }
      if (obj == null) {
          return false;
      }
      if (getClass() != obj.getClass()) {
          return false;
      }
      FloatLiteral other = (FloatLiteral) obj;
      if (!value.equals(other.value)) {
          return false;
      }
      return true;
  }
    public FloatLiteral(double value) {
      this(value, FileLocation.UNKNOWN);
    }
    public FloatLiteral(BigDecimal value) {
      this(value, FileLocation.UNKNOWN);
    }
    public FloatLiteral(double value, FileLocation loc) {
      this(BigDecimal.valueOf(value), loc);
    }
    public FloatLiteral(BigDecimal value, FileLocation loc) {
      super(Util.floatType(), loc);
      this.value = value;
    }
    @Override
    public void doPrettyPrint(Appendable dest, String indent) throws IOException {
        dest.append(value.toString());
    }
    public ValueType typeCheck(TypeContext env, EffectAccumulator effectAccumulator)  {
      return Util.floatType();
    }
    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> emitFLVisitor, S state)  {
      return emitFLVisitor.visit(state, this);
    }
    public BigDecimal getFullValue() {
      return value;
    }
    @Override
    public Value invoke(String methodName, List<Value> args)  {
      switch (methodName) {
      case "+": return new FloatLiteral(this.value.add(((FloatLiteral) args.get(0)).getFullValue()));
      case "-": return new FloatLiteral(this.value.subtract(((FloatLiteral) args.get(0)).getFullValue()));
      case "*": return new FloatLiteral(this.value.multiply(((FloatLiteral) args.get(0)).getFullValue()));
      case "/": return new FloatLiteral(this.value.divide(((FloatLiteral) args.get(0)).getFullValue()));
      case "negate": return new FloatLiteral(this.value.negate());
      case "<": return new BooleanLiteral(this.value.compareTo(((FloatLiteral) args.get(0)).getFullValue()) < 0);
      case ">": return new BooleanLiteral(this.value.compareTo(((FloatLiteral) args.get(0)).getFullValue()) > 0);
      case "==": return new BooleanLiteral(this.value.compareTo(((FloatLiteral) args.get(0)).getFullValue()) == 0);
      default: throw new RuntimeException("runtime error: integer operation " + methodName + "not supported by the runtime");
      }
    }
    @Override
    public Value getField(String fieldName) {
      throw new RuntimeException("no fields");
    }
    @Override
    public Set<String> getFreeVariables() {
      return new HashSet<>();
    }
}
