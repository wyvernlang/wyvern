package wyvern.target.corewyvernIL.expression;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.JavaValue;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIREnvironment;
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
  public <T, E> T acceptVisitor(ASTVisitor<T, E> emitILVisitor,
                                E env,
                                OIREnvironment oirenv) {
    return emitILVisitor.visit(env, oirenv, this);
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
    if (this.ffiType.equals(new NominalType("system", "Java"))) {
      try {
        FObject obj = wyvern.tools.interop.Default.importer().find(path);
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
