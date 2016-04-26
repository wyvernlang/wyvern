package wyvern.target.corewyvernIL.expression;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.expression.AbstractValue;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIREnvironment;
import wyvern.tools.errors.FileLocation;

public class FFIImport extends AbstractValue {
  String path;
  ValueType ffiType;

  public FFIImport(ValueType ffi, String path) {
    super(new NominalType("system", "Dyn"), FileLocation.UNKNOWN);
    this.path = path;
    this.ffiType = ffi;
  }

  @Override
  public <T> T acceptVisitor(ASTVisitor<T> emitILVisitor,
                             Environment env,
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
  public Set<String> getFreeVariables() {
    return new HashSet<>();
  }

  @Override
  public ValueType getType() {
    return this.getExprType();
  }
}
