package wyvern.tools.interop;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import wyvern.stdlib.Globals;
import wyvern.stdlib.support.backend.BytecodeOuterClass;
import wyvern.target.corewyvernIL.VarBinding;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.effects.EffectAccumulator;
import wyvern.target.corewyvernIL.expression.AbstractValue;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.Tag;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.InterpreterState;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.DynamicType;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.core.declarations.ImportDeclaration;
import wyvern.tools.util.Pair;

public class FFI extends AbstractValue {
    private ValueType type;
    private String importName;

    public FFI(String importName, ValueType type, FileLocation loc) {
        super(type, loc);
        this.type = type;
        this.importName = importName;
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> emitILVisitor, S state) {
        return emitILVisitor.visit(state, this);
    }

    @Override
    public ValueType typeCheck(TypeContext ctx, EffectAccumulator effectAccumulator) {
        return type;
    }

    @Override
    public Set<String> getFreeVariables() {
        return new HashSet<>();
    }

    @Override
    public ValueType getType() {
        return type;
    }

    public String getImportName() {
        return this.importName;
    }

    @Override
    public BytecodeOuterClass.Expression emitBytecode() {
        return BytecodeOuterClass.Expression.newBuilder().setNewExpression(
                BytecodeOuterClass.Expression.NewExpression.newBuilder().setType(type.emitBytecodeType()).setSelfName("unused").build()
        ).build();
    }

    public Tag getTag(EvalContext ctx) {
        NominalType nt = (NominalType) this.getType();
        return nt.getTag(ctx);
    }

    public static Pair<VarBinding, GenContext> importURI(URI uri, GenContext ctx, HasLocation errorLocation) {
        final String scheme = uri.getScheme();
        if (scheme.equals("java")) {
            return doJavaImport(uri, ctx, errorLocation);
        } else if (scheme.equals("python")) {
            return doPythonImport(uri, ctx);
        } else if (scheme.equals("javascript")) {
            return doJavaScriptImport(uri, ctx, errorLocation);
        } else {
            // TODO: support non-Java imports too - probably separated out into various FFI subclasses
            System.err.println("importURI called with uri=" + uri + ", ctx=" + ctx);
            throw new RuntimeException("only Java imports should get to this code path; others not implemented yet");
        }
    }

    public static Pair<VarBinding, GenContext> doJavaImport(URI uri, GenContext ctx, HasLocation errorLocation) {
        String importName = uri.getSchemeSpecificPart();
        if (importName.contains(".")) {
            importName = importName.substring(importName.lastIndexOf(".") + 1);
        }
        String importPath = uri.getRawSchemeSpecificPart();
        FObject obj = null;
        try {
            obj = wyvern.tools.interop.Default.importer().find(importPath, errorLocation);
        } catch (ReflectiveOperationException e1) {
            ToolError.reportError(ErrorMessage.IMPORT_NOT_FOUND, errorLocation, uri.toString());
        }

        ctx = GenUtil.ensureJavaTypesPresent(ctx);
        ctx = ImportDeclaration.extendWithImportCtx(obj, ctx);

        assert (uri.toString().substring(0, 5).equals("java:"));
        boolean safe = Globals.checkSafeJavaImport(uri.toString().substring(5));
        ValueType type = GenUtil.javaClassToWyvernType(obj.getJavaClass(), ctx, safe);

        Expression importExp = new FFIImport(new NominalType("system", "java"), importPath, type);
        ctx = ctx.extend(importName, new Variable(importName), type);
        return new Pair<VarBinding, GenContext>(new VarBinding(importName, type, importExp), ctx);
    }

    public static Pair<VarBinding, GenContext> doPythonImport(URI uri, GenContext ctx) {
        String importName = uri.getSchemeSpecificPart();
        ValueType type = new DynamicType();
        Expression importExp = new FFIImport(new NominalType("system", "python"), importName, type);
        return new Pair<VarBinding, GenContext>(new VarBinding(importName, type, importExp), ctx.extend(importName, new Variable(importName), type));
    }

    public static Pair<VarBinding, GenContext> doJavaScriptImport(URI uri, GenContext ctx, HasLocation errorLocation) {
        String importPath = uri.getSchemeSpecificPart();
        String[] split = importPath.split("\\.");
        String importName = split[split.length - 1];
        ValueType type = new DynamicType();
        Expression importExp = new FFIImport(new NominalType("system", "javascript"), importName, type);
        BytecodeOuterClass.Bytecode.Import i = BytecodeOuterClass.Bytecode.Import.newBuilder()
                // TODO
                .setIsTypeImport(false)
                .setIsMetadataImport(false)
                .setPlatform("javascript")
                .setPath(importPath)
                .setName(importName)
                .setType(type.emitBytecodeType()).build();
        InterpreterState.getLocalThreadInterpreter().getJavascriptFFIImports().add(i);
        return new Pair<VarBinding, GenContext>(new VarBinding(importName, type, importExp), ctx.extend(importName, new Variable(importName), type));
    }

}
