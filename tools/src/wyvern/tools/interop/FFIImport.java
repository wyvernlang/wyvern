package wyvern.tools.interop;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import wyvern.stdlib.support.backend.BytecodeOuterClass;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.effects.EffectAccumulator;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;

public class FFIImport extends Expression {
    private String path;
    private ValueType ffiType;

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

    @Override
    public BytecodeOuterClass.Expression emitBytecode() {
        NominalType javaPlatform = new NominalType("system", "java");
        NominalType javascriptPlatform = new NominalType("system", "javascript");
        BytecodeOuterClass.Expression unit = BytecodeOuterClass.Expression.newBuilder().setNewExpression(
                BytecodeOuterClass.Expression.NewExpression.newBuilder().setType(
                        BytecodeOuterClass.Type.newBuilder().setSimpleType(BytecodeOuterClass.Type.SimpleType.Top)
                ).setSelfName("unitSelf")
        ).build();
        if (getFFIType().equals(javaPlatform)) {
            // @HACK: stub out java imports for prelude
            return unit;
        } else if (getFFIType().equals(javascriptPlatform)) {
            return BytecodeOuterClass.Expression.newBuilder().setVariable("FFI_" + getPath()).build();
        } else {
            throw new UnsupportedOperationException("Unsupported ffiType for bytecode FFIImport");
        }
    }

    public String getPath() {
        return this.path;
    }

    public ValueType getFFIType() {
        return this.ffiType;
    }

    @Override
    public ValueType typeCheck(TypeContext ctx, EffectAccumulator effectAccumulator) {
        return this.getType();
    }

    @Override
    public Value interpret(EvalContext ctx) {
        if (this.ffiType.equals(new NominalType("system", "java"))) {
            try {
                FObject obj = wyvern.tools.interop.Default.importer().find(path, this);
                return new JavaValue(obj, this.getType());
            } catch (ReflectiveOperationException e1) {
                throw new RuntimeException(e1);
            }
        } else if (this.ffiType.equals(new NominalType("system", "javascript"))) {
            // @HACK - trying to run TSLs on .wyv containing javascript import
            return new JavaValue(null, this.getType());
        } else if (this.ffiType.equals(new NominalType("system", "python"))) {
            // @HACK - trying to run TSLs on .wyv containing python import
            // This should have a more general solution when we create a staged prelude
            return new JavaValue(null, this.getType());
        } else {
            throw new RuntimeException("Cannot interpret FFI import of type" + this.ffiType.toString());
        }
    }

    @Override
    public Set<String> getFreeVariables() {
        return new HashSet<>();
    }

}
