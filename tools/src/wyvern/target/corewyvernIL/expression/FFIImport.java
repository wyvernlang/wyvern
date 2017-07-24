package wyvern.target.corewyvernIL.expression;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.JavaValue;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.interop.FObject;

public class FFIImport extends Expression {
  String path;
  ValueType ffiType;

  public FFIImport(ValueType ffi, String path, ValueType type) {
    super(type, FileLocation.UNKNOWN);
    this.path = path;
    this.ffiType = ffi;
  }

  @Override
  public <S, T> T acceptVisitor(ASTVisitor<S, T> emitILVisitor,
                                S state) {
    return emitILVisitor.visit(state, this);
  }

  @Override
  public void doPrettyPrint(Appendable dest, String indent) throws IOException {
    dest.append("FFI(");
    this.ffiType.doPrettyPrint(dest, indent);
    dest.append(", ");
    dest.append(this.path);
    dest.append(")");
  }

  public String getPath() {
    return this.path;
  }

  public ValueType getFFIType() {
    return this.ffiType;
  }

  @Override
  public ValueType typeCheck(TypeContext ctx) {
    return this.getExprType();
  }

  @Override
  public Value interpret(EvalContext ctx) {
    if (this.ffiType.equals(new NominalType("system", "java"))) {
      try {
        FObject obj = wyvern.tools.interop.Default.importer().find(path, this);
        return new JavaValue(obj, this.getExprType());
      } catch (ReflectiveOperationException e1) {
        throw new RuntimeException(e1);
      }
    } else {
      throw new RuntimeException("Cannot interpret FFI import of type" +
                                 this.ffiType.toString());
    }
  }

  @Override
  public Set<String> getFreeVariables() {
    return new HashSet<>();
  }

}
