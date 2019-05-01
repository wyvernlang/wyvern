package wyvern.tools.typedAST.core.declarations;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.BindingSite;
import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.VarBinding;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.FieldGet;
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.modules.Module;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.ModuleResolver;
import wyvern.target.corewyvernIL.support.TopLevelContext;
import wyvern.target.corewyvernIL.support.TypeOrEffectGenContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.interop.FFI;
import wyvern.tools.interop.FFIImport;
import wyvern.tools.interop.FObject;
import wyvern.tools.interop.GenUtil;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.typedastvisitor.TypedASTVisitor;
import wyvern.tools.util.Pair;

public class ImportDeclaration extends Declaration implements CoreAST {
    private URI uri;
    private FileLocation location;
    private boolean requireFlag;
    private boolean metadataFlag;
    private String asName;
    private boolean isLifted;

    public ImportDeclaration(URI inputURI, FileLocation location, String image, boolean isRequire, boolean isMetadata, boolean isLifted) {
        this.uri = inputURI;
        this.location = location;
        this.requireFlag = isRequire;
        this.metadataFlag = isMetadata;
        this.asName = image;
        this.isLifted = isLifted;
    }

    @Override
    public StringBuilder prettyPrint() {
        StringBuilder sb = new StringBuilder();
        String uriString = "null";
        if (uriString != null) {
            uriString = uri.toString();
        }
//        String binderString = "<todo>";
//        String locationString = "null";
//        if (location != null) {
//            locationString = location.toString();
//        }
//        String requireString = String.valueOf(requireFlag);
//        String metadataString = String.valueOf(metadataFlag);
        sb.append("ImportDeclaration(uri=" + uriString
//                 + ", binder=" + binderString
//                 + ", location=" + locationString
//                 + ", requireFlag=" + requireString
//                 + ", metadataFlag=" + metadataString
//                 + ", asName=" + asName
                 + ")");
        return sb;
    }

    public URI getUri() {
        return uri;
    }

    public boolean isLifted() {
        return isLifted;
    }

    @Override
    public FileLocation getLocation() {
        return location;
    }

    @Override
    public String getName() {
        return asName;
    }

    @Override
    public DeclType genILType(GenContext ctx) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public wyvern.target.corewyvernIL.decl.Declaration generateDecl(GenContext ctx, GenContext thisContext) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isRequire() {
        return this.requireFlag;
    }


    @Override
    public wyvern.target.corewyvernIL.decl.Declaration topLevelGen(GenContext ctx, List<TypedModuleSpec> dependencies) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S, T> T acceptVisitor(TypedASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }


    public static GenContext extendWithImportCtx(FObject obj, GenContext ctx) {
        if (obj.getWrappedValue() instanceof java.lang.Class) {
            // then this is a Class import
            // and we need to extend the context
            Class<?> wrappedValue = (Class<?>) obj.getWrappedValue();
            String qualifiedName = wrappedValue.getName();
            int lastDot = qualifiedName.lastIndexOf('.');
            String className = qualifiedName.substring(lastDot + 1);
            String packageName = qualifiedName.substring(0, lastDot);
            ValueType type = GenUtil.javaClassToWyvernType(wrappedValue, ctx);
            ctx = new TypeOrEffectGenContext(className, new FieldGet(new Variable(GenUtil.javaTypesObjectName), packageName, null), ctx);
        }
        return ctx;
    }

    public Pair<VarBinding, GenContext> genBinding(GenContext ctx, List<TypedModuleSpec> dependencies) {
        return genBinding(ctx.getInterpreterState().getResolver(), ctx, dependencies);
    }

    public Pair<VarBinding, GenContext> genBinding(ModuleResolver resolver, GenContext ctx, List<TypedModuleSpec> dependencies) {
        // add the import's type to the context, and get the import value
        Expression importExp = null;
        String importName = this.getUri().getSchemeSpecificPart();
        ValueType type = null;
        if (importName.contains(".")) {
            importName = importName.substring(importName.lastIndexOf(".") + 1);
        }
        final String scheme = this.getUri().getScheme();
        if (ctx.isPresent(scheme, true)) {
            // TODO: hack; replace this by getting FFI metadata from the type of the scheme
            return FFI.importURI(this.getUri(), ctx, this);
        } else if (scheme.equals("java")) {
            // we are not using Java like a capability in this branch, so check the whitelist!
            if (!Globals.checkSafeJavaImport(this.getUri().getSchemeSpecificPart())) {
                ToolError.reportError(ErrorMessage.UNSAFE_JAVA_IMPORT, this, "java", this.getUri().getSchemeSpecificPart(), "java");
            }
            return FFI.doJavaImport(getUri(), ctx, this);
        } else if (this.getUri().getScheme().equals("python")) {
            String moduleName = this.getUri().getRawSchemeSpecificPart();
            importExp = new FFIImport(new NominalType("system", "python"),
                    moduleName,
                    new NominalType("system", "Dyn"));
            ctx = ctx.extend(importName, new Variable(importName), Util.dynType());
            type = Util.dynType();
        } else if (this.getUri().getScheme().equals("javascript")) {
            if (!Globals.checkSafeJavascriptImport(this.getUri().getSchemeSpecificPart())) {
                ToolError.reportError(ErrorMessage.UNSAFE_JAVA_IMPORT, this, "javascript", this.getUri().getSchemeSpecificPart(), "javascript");
            }
            return FFI.doJavaScriptImport(getUri(), ctx, this);
        } else if (this.isRequire()) {
            // invariant: modules that require things won't call this
            // so special case for scripts that require things
            // right now the only supported case is java
            // TODO: make this more generic
            if (importName.equals("java")) {
                type = Globals.JAVA_IMPORT_TYPE;
                importExp = new FFI(importName, type, this.getLocation());
                ctx = ctx.extend(importName, importExp, type);
            } else if (importName.equals("python")) {
                type = Globals.PYTHON_IMPORT_TYPE;
                importExp = new FFI(importName, type, this.getLocation());
                ctx = ctx.extend(importName, importExp, type);
            } else if (importName.equals("javascript")) {
                type = Globals.JAVASCRIPT_IMPORT_TYPE;
                importExp = new FFI(importName, type, this.getLocation());
                ctx = ctx.extend(importName, importExp, type);
            } else if (importName.equals("platform")) {
                if (resolver.getPlatform().equals("java")) {
                    type = Globals.JAVA_IMPORT_TYPE;
                } else if (resolver.getPlatform().equals("python")) {
                    type = Globals.PYTHON_IMPORT_TYPE;
                } else if (resolver.getPlatform().equals("javascript")) {
                    type = Globals.JAVASCRIPT_IMPORT_TYPE;
                } else {
                    throw new RuntimeException("interpreter state has an unexpected platform " + resolver.getPlatform());
                }
                importExp = new FFI(importName, type, this.getLocation());
                ctx = ctx.extend(importName, importExp, type);
            } else {
                // special case: a script is importing a module called "importName"
                // that requires only java

                // load the module
                String moduleName = this.getUri().getRawSchemeSpecificPart();
                Module m = resolver.resolveModule(moduleName);

                // instantiate the module
                DefDeclType modDeclType = (DefDeclType) ((StructuralType) m.getSpec().getType()).findDecl(Util.APPLY_NAME, ctx);
                if (modDeclType == null || modDeclType.getFormalArgs().size() != 1) {
                    ToolError.reportError(ErrorMessage.SCRIPT_REQUIRED_MODULE_ONLY_JAVA, this);
                }
                List<FormalArg> modArgs = modDeclType.getFormalArgs();
                final ValueType argType = modArgs.get(0).getType();
                List<Expression> args = new LinkedList<Expression>();
                if (argType.equals(Globals.JAVA_IMPORT_TYPE)
                        || (argType.equals(Globals.PLATFORM_IMPORT_TYPE) && resolver.getPlatform().equals("java"))) {
                    args.add(new FFI("java", Globals.JAVA_IMPORT_TYPE, this.getLocation()));
                } else if (argType.equals(Globals.PYTHON_IMPORT_TYPE)
                        || (argType.equals(Globals.PLATFORM_IMPORT_TYPE) && resolver.getPlatform().equals("python"))) {
                    args.add(new FFI("python", Globals.PYTHON_IMPORT_TYPE, this.getLocation()));
                } else if (argType.equals(Globals.JAVASCRIPT_IMPORT_TYPE)
                        || (argType.equals(Globals.PLATFORM_IMPORT_TYPE) && resolver.getPlatform().equals("javascript"))) {
                    args.add(new FFI("javascript", Globals.JAVASCRIPT_IMPORT_TYPE, this.getLocation()));
                } else {
                    // TODO: Better error message
                    ToolError.reportError(ErrorMessage.SCRIPT_REQUIRED_MODULE_ONLY_JAVA, this);
                }

                // type is the result type of the module
                type = modDeclType.getRawResultType();
                final String internalName = m.getSpec().getInternalName();
                Variable importVar = new Variable(internalName);
                importExp = new MethodCall(/*m.getExpression()*/importVar, Util.APPLY_NAME, args, this);
                ctx = resolver.extendGenContext(ctx, m.getDependencies());
                if (!ctx.isPresent(internalName, true)) {
                    ctx = addDepsToCtx(m, ctx);
                    ctx = ctx.extend(internalName, new Variable(internalName), type);
                }
                dependencies.add(m.getSpec());
                dependencies.addAll(m.getDependencies());
                // ctx gets extended in the standard way, with a variable
                String name = this.asName == null ? importName : this.asName;
                ctx = ctx.extend(name, new Variable(name), type);
            }
        } else if (scheme.equals("wyv")) {
            // TODO: need to add types for non-java imports
            String moduleName = this.getUri().getSchemeSpecificPart();
            final Module module = resolver.resolveModule(moduleName, false, isLifted);

            final String internalName = module.getSpec().getInternalName();
            final BindingSite site = module.getSpec().getSite();
            if (this.metadataFlag) {
                if (module.getSpec().getType().isResource(ctx)) {
                    ToolError.reportError(ErrorMessage.NO_METADATA_FROM_RESOURCE, this);
                }
                //Value v = resolver.wrap(module.getExpression(), module.getDependencies()).interpret(Globals.getStandardEvalContext());
                Value v = module.getAsValue(resolver);
                type = v.getType();
            } else {
                type = module.getSpec().getType();
            }
            ctx = resolver.extendGenContext(ctx, module.getDependencies());
            if (!ctx.isPresent(internalName, true)) {
                ctx = addDepsToCtx(module, ctx);
                ctx = ctx.extend(site, new Variable(site), type);
            }
            importExp = new Variable(site);
            dependencies.add(module.getSpec());
            dependencies.addAll(module.getDependencies());
            if (module.getSpec().getDefinedTypeName() != null) {
                ctx = new TypeOrEffectGenContext(importName, (Variable) importExp, ctx);
            } else {
                ctx = ctx.extend(this.asName == null ? importName : this.asName, new Variable(site), type);
            }
        } else {
            ToolError.reportError(ErrorMessage.SCHEME_NOT_RECOGNIZED, this, scheme);
        }
        return new Pair<VarBinding, GenContext>(new VarBinding(this.asName == null ? importName : this.asName, type, importExp), ctx);
    }

    static GenContext addDepsToCtx(Module module, GenContext ctx) {
        for (TypedModuleSpec s : module.getDependencies()) {
            if (!ctx.isPresent(s.getInternalName(), true)) {
                ctx = ctx.extend(s.getSite(), new Variable(s.getSite()), s.getType());
            }
        }
        return ctx;
    }

    @Override
    public void addModuleDecl(TopLevelContext tlc) {
        // no action needed
    }

    @Override
    public void genTopLevel(TopLevelContext tlc) {
        Pair<VarBinding, GenContext> bindingAndCtx = genBinding(tlc.getContext(), tlc.getDependencies());
        VarBinding binding = bindingAndCtx.getFirst();
        GenContext newCtx = bindingAndCtx.getSecond();
        ValueType type = binding.getType(); //binding.getExpression().typeCheck(newCtx, null);
        tlc.addLet(binding.getSite(), type, binding.getExpression(), false);
        tlc.updateContext(newCtx);
    }

    @Override
    public String toString() {
        return prettyPrint().toString();
    }
}
