package wyvern.tools.interop;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import wyvern.stdlib.Globals;
import wyvern.stdlib.support.Effect;
import wyvern.stdlib.support.backend.BytecodeOuterClass;
import wyvern.target.corewyvernIL.VarBinding;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.effects.EffectAccumulator;
import wyvern.target.corewyvernIL.expression.AbstractValue;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.Tag;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.modules.Module;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.InterpreterState;
import wyvern.target.corewyvernIL.support.ModuleResolver;
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

    public static Pair<Pair<VarBinding, GenContext>, List<TypedModuleSpec>> importURI(URI uri, GenContext ctx, HasLocation errorLocation) {
        final String scheme = uri.getScheme();
        List<TypedModuleSpec> noDependencies = Collections.emptyList();
        if (scheme.equals("java")) {
            return doJavaImport(uri, ctx, errorLocation);
        } else if (scheme.equals("python")) {
            return new Pair<>(doPythonImport(uri, ctx), noDependencies);
        } else if (scheme.equals("javascript")) {
            return new Pair<>(doJavaScriptImport(uri, ctx, errorLocation), noDependencies);
        } else {
            // TODO: support non-Java imports too - probably separated out into various FFI subclasses
            System.err.println("importURI called with uri=" + uri + ", ctx=" + ctx);
            throw new RuntimeException("only Java imports should get to this code path; others not implemented yet");
        }
    }

    public static Pair<Pair<VarBinding, GenContext>, List<TypedModuleSpec>> doJavaImport(URI uri, GenContext ctx, HasLocation errorLocation) {
        String importName = uri.getSchemeSpecificPart();
        if (importName.contains(".")) {
            importName = importName.substring(importName.lastIndexOf(".") + 1);
        }
        String importPath = uri.getRawSchemeSpecificPart();
        FObject obj = null;
        try {
            obj = new JavaImporter(ctx).find(importPath, errorLocation);
        } catch (ReflectiveOperationException e1) {
            ToolError.reportError(ErrorMessage.IMPORT_NOT_FOUND, errorLocation, uri.toString());
        }

        ctx = GenUtil.ensureJavaTypesPresent(ctx);
        ctx = ImportDeclaration.extendWithImportCtx(obj, ctx);

        List<TypedModuleSpec> dependencies = getJavaDependencies(obj.getJavaClass());

        assert (uri.toString().substring(0, 5).equals("java:"));
        boolean safe = Globals.checkSafeJavaImport(uri.toString().substring(5));
        ValueType type = GenUtil.javaClassToWyvernType(obj.getJavaClass(), ctx, safe);

        Expression importExp = new FFIImport(new NominalType("system", "java"), importPath, type);
        ctx = ctx.extend(importName, new Variable(importName), type);
        return new Pair<>(new Pair<>(new VarBinding(importName, type, importExp), ctx), dependencies);
    }

    public static List<TypedModuleSpec> getJavaDependencies(Class<?> javaClass) {
        Set<String> modulePaths = new HashSet<>();
        for (Method m : javaClass.getMethods()) {
            Effect[] annotations = m.getAnnotationsByType(Effect.class);
            for (Effect e : annotations) {
                for (String path: e.value()) {
                    String[] parts = path.split("\\.");
                    String modulePath = String.join(".", Arrays.copyOf(parts, parts.length - 1));
                    modulePaths.add(modulePath);
                }
            }
        }

        List<TypedModuleSpec> specs = new ArrayList<>();
        for (String path : modulePaths) {
            Module mod = ModuleResolver.getLocal().resolveModule(path);
            specs.add(mod.getSpec());
        }
        return specs;
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
