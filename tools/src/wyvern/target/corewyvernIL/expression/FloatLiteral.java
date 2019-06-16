package wyvern.target.corewyvernIL.expression;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import wyvern.stdlib.support.backend.BytecodeOuterClass;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.effects.EffectAccumulator;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;

public class FloatLiteral extends Literal implements Invokable  {
  private final Double value;
  @Override
  public int hashCode() {
    return ((Double) value).hashCode();
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
      if (value != other.value) {
          return false;
      }
      return true;
  }
    public FloatLiteral(double value) {
      this(value, FileLocation.UNKNOWN);
    }
    public FloatLiteral(Double value) {
      this(value, FileLocation.UNKNOWN);
    }
    public FloatLiteral(double value, FileLocation loc) {
      this((Double) value, loc);
    }
    public FloatLiteral(Double value, FileLocation loc) {
      super(Util.floatType(), loc);
      this.value = value;
    }
    @Override
    public void doPrettyPrint(Appendable dest, String indent) throws IOException {
        dest.append(value.toString());
    }

    @Override
    public BytecodeOuterClass.Expression emitBytecode() {
        BytecodeOuterClass.Expression.Literal.Builder literal = BytecodeOuterClass.Expression.Literal.newBuilder().setFloatLiteral(value);
        return BytecodeOuterClass.Expression.newBuilder().setLiteral(literal).build();
    }

    public ValueType typeCheck(TypeContext env, EffectAccumulator effectAccumulator)  {
      return Util.floatType();
    }
    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> emitFLVisitor, S state)  {
      return emitFLVisitor.visit(state, this);
    }
    public Double getFullValue() {
      return value;
    }
    @Override
    public Value invoke(String methodName, List<Value> args, FileLocation loc)  {
      switch (methodName) {
      case "+": return new FloatLiteral(this.value + ((FloatLiteral) args.get(0)).getFullValue());
      case "-": return new FloatLiteral(this.value - ((FloatLiteral) args.get(0)).getFullValue());
      case "*": return new FloatLiteral(this.value * ((FloatLiteral) args.get(0)).getFullValue());
      case "/": return new FloatLiteral(this.value / ((FloatLiteral) args.get(0)).getFullValue());
      case "negate": return new FloatLiteral(this.value * -1);
      case "floor": return new IntegerLiteral((int) Math.floor(this.value));
      case "<": return new BooleanLiteral(this.value.compareTo(((FloatLiteral) args.get(0)).getFullValue()) < 0);
      case ">": return new BooleanLiteral(this.value.compareTo(((FloatLiteral) args.get(0)).getFullValue()) > 0);
      case "==": return new BooleanLiteral(this.value.compareTo(((FloatLiteral) args.get(0)).getFullValue()) == 0);
      case "<=": return new BooleanLiteral(this.value.compareTo(((FloatLiteral) args.get(0)).getFullValue()) <= 0);
      case ">=": return new BooleanLiteral(this.value.compareTo(((FloatLiteral) args.get(0)).getFullValue()) >= 0);
      case "!=": return new BooleanLiteral(this.value.compareTo(((FloatLiteral) args.get(0)).getFullValue()) != 0);
      
      default: throw new RuntimeException("runtime error: float operation " + methodName + "not supported by the runtime");
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
