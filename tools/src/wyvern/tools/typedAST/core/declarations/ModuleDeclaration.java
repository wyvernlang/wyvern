package wyvern.tools.typedAST.core.declarations;

import java.io.File;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import wyvern.target.corewyvernIL.BindingSite;
import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.VarBinding;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.effects.EffectSet;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.SeqExpr;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.modules.Module;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.ModuleResolver;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.TypeOrEffectGenContext;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.generics.GenericParameter;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.typedastvisitor.TypedASTVisitor;
import wyvern.tools.types.NamedType;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.util.Pair;

public class ModuleDeclaration extends DeclarationWithGenerics implements CoreAST {
    private final String name;
    private final TypedAST inner;
    private FileLocation location;
    private NamedType ascribedType;
    private boolean resourceFlag;
    private final List<NameBindingImpl> args;

    private final List<ImportDeclaration> imports;
    private boolean isAnnotated;
    private EffectSet effectSet;

    public ModuleDeclaration(String name, List imports, List<GenericParameter> generics, List<NameBindingImpl> args,
            TypedAST inner, NamedType type, FileLocation location, boolean isResource, boolean isAnnotated,
            String effects) {
        this.name = name;
        this.inner = inner;
        this.location = location;
        this.resourceFlag = isResource;
        ascribedType = type;
        this.args = args;
        this.imports = imports;
        this.generics = generics;
        this.isAnnotated = isAnnotated;
        this.effectSet = EffectSet.parseEffects(name, effects, false, location);
        if (args.isEmpty() && imports.isEmpty() && this.effectSet != null && !this.effectSet.getEffects().isEmpty()) {
            ToolError.reportError(ErrorMessage.PURE_MODULE_ANNOTATION, location);
        }
    }

    public EffectSet getEffectSet() {
        return effectSet;
    }

    public List<NameBindingImpl> getArgs() {
        return args;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "module " + name;
    }

    @Override
    public FileLocation getLocation() {
        return location;
    }

    public TypedAST getInner() {
        return inner;
    }

    @Override
    public DeclType genILType(GenContext ctx) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public wyvern.target.corewyvernIL.decl.Declaration generateDecl(GenContext ctx, GenContext thisContext) {
        return null;
    }

    public boolean isAnnotated() {
        return isAnnotated;
    }

    @Override
    public <S, T> T acceptVisitor(TypedASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }

    /**
     * Generate the rest part of a module (not import/instantiate/require)
     *
     * @param normalSeq the declaration sequence
     * @param ctx       the context
     * @return the IL expression
     */
    private IExpr innerTranslate(Sequence normalSeq, GenContext ctx) {
        /* Sequence.innerTranslate */
        // The real work is done by the sequence itself.
        return normalSeq.generateModuleIL(ctx, true);
    }

    /**
     * Computes and returns the set of arguments this module requires.
     *
     * loadedTypes is updated with all the types that had to be loaded in order to
     * specify the required types.
     *
     * @param ctx
     * @param loadedTypes
     * @return a list of formal arguments
     */

    private List<FormalArg> getTypes(GenContext ctx, List<Module> loadedTypes) {
        /* generate the formal arguments by requiring sequence */
        List<FormalArg> types = new LinkedList<FormalArg>();

        for (NameBindingImpl arg : args) {
            types.add(getType(ctx, loadedTypes, null, arg));
        }

        return types;
    }

    /**
     * Obtain the type of the module.
     * 
     * @param ctx         context of the method
     * @param loadedTypes a list of Modules objects that has loaded types
     * @param location    file location
     * @param arg         the name binding argument
     * @return a FormalArg object that contains the type of the module.
     */
    private FormalArg getType(GenContext ctx, List<Module> loadedTypes, FileLocation location, NameBindingImpl arg) {
        // initialize resulting type to null.
        FormalArg resultType = null;

        // get the type of the argument
        Object argType = arg.getType();

        // initialize typeName
        String typeName;

        if (argType instanceof Arrow) {
            // case when argType is an Arrow type (a lambda expression).

            // obtain the value type from Arrow type.
            ValueType intermediateLanguageType = ((Arrow) argType).getILType(ctx);
            resultType = new FormalArg(arg.getName(), intermediateLanguageType);

            // obtain the type of the formal arguments (of arrow type).
            List<Type> list = ((Arrow) argType).getArguments();

            // obtain the result type (of arrow type).
            Type arrowResultType = ((Arrow) argType).getResult();

            // loop through all the formal arguments and return arguments and add them to
            // loadedTypes list.
            for (Type argument : list) {
                typeName = ((NamedType) argument).getFullName();
                if (!ctx.isPresent(typeName, false)) {
                    // case when the context is not present.
                    Module lt = resolveLoadedTypes(ctx, typeName);
                    loadedTypes.add(lt);
                }
            }

            // add result type of the arrow type to loadedTypes list.
            typeName = ((NamedType) arrowResultType).getFullName();
            if (!ctx.isPresent(typeName, false)) {
                // case when the context is not present.
                Module lt = resolveLoadedTypes(ctx, typeName);
                loadedTypes.add(lt);
            }

        } else {
            // obtain the name of the type.
            typeName = ((NamedType) argType).getFullName();
            ValueType valueType = this.getType(ctx, loadedTypes, location, typeName);
            resultType = new FormalArg(arg.getName(), valueType);
        }

        return resultType;
    }

    /**
     * Obtain the type of the module.
     * 
     * @param ctx         context of the method
     * @param loadedTypes a list of Modules objects that has loaded types
     * @param location    file location
     * @param typeName    the name of the type
     * @return a FormalArg object that contains the type of the module.
     */
    private ValueType getType(GenContext ctx, List<Module> loadedTypes, FileLocation location, String typeName) {
        // initialize resulting value type to null;
        ValueType valueType = null;

        if (ctx.isPresent(typeName, false)) {
            // case when the context is present.
            valueType = ctx.lookupType(typeName, location);
        } else {
            // case when the context is not present.
            Module lt = resolveLoadedTypes(ctx, typeName);
            valueType = new NominalType(lt.getSpec().getInternalName(), lt.getSpec().getDefinedTypeName());
            loadedTypes.add(lt);
        }
        return valueType;
    }

    /**
     * Resolve types from context
     * 
     * @param ctx      the context
     * @param typeName the name of the type
     * @return a Module object with resolved loaded types.
     */
    private Module resolveLoadedTypes(GenContext ctx, String typeName) {
        Module lt = ctx.getInterpreterState().getResolver().resolveType(typeName);
        return lt;
    }

    public boolean isResource() {
        return this.resourceFlag;
    }

    private boolean isPlatformPath(String platform, String path) {
        // Return true if file path ends with /platform/X/FILENAME (where X is a
        // platform)
        // Pattern p = Pattern.compile("/platform/" + platform + "/[^/]*$");

        // Return true if file path includes platform/X (where X is a platform)
        final String separatorPattern = Pattern.quote(File.separator);
        // Pattern p = Pattern.compile("platform" + separatorPattern + platform);
        // TODO: generalize me - just use the above. But this involves fixing a bug.
        // HACK: avoid certain problematic libraries being interpreted as platform
        // dependent. need to fix this later
        Pattern p = Pattern
                .compile("platform" + separatorPattern + platform + separatorPattern + "[^" + separatorPattern + "]*$");
        return p.matcher(path).find();
    }

    private void separatePlatformDependencies(LinkedList<ImportDeclaration> platformDependent,
            LinkedList<ImportDeclaration> platformIndependent) {
        for (ImportDeclaration d : imports) {
            URI uri = d.getUri();
            /*
             * if (d instanceof ImportDeclaration) { ImportDeclaration decl =
             * (ImportDeclaration)d; uri = decl.getUri(); } else if (d instanceof
             * Instantiation) { Instantiation decl = (Instantiation)d; uri = decl.getUri();
             * }
             */
            if (uri == null || !uri.getScheme().equals("wyv")) {
                platformIndependent.addLast(d);
            } else {
                File f = ModuleResolver.getLocal().resolve(uri.getSchemeSpecificPart(), false);
                boolean isPlatPath = isPlatformPath(ModuleResolver.getLocal().getPlatform(), f.getAbsolutePath());
                if (isPlatPath) {
                    platformDependent.addLast(d);
                } else {
                    platformIndependent.addLast(d);
                }
            }
        }
    }

    /**
     * Translates imports, adding them to the passed-in SeqExpr. Updates the list of
     * dependencies. Returns an extended context
     */
    public GenContext translateImports(LinkedList<ImportDeclaration> imports, GenContext ctx, SeqExpr seq,
            List<TypedModuleSpec> dependencies) {
        GenContext current = ctx;
        for (ImportDeclaration imp : imports) {
            Pair<VarBinding, GenContext> bindingAndCtx = imp.genBinding(current, dependencies);
            current = bindingAndCtx.getSecond();
            seq.addBindingLast(bindingAndCtx.getFirst());
        }
        return current;
    }

    /**
     * For resource module: translate into def method(list of require types) : </br>
     * resource type { let (sequences of instantiate/import) in rest}; </br>
     * 
     * @see filterRequires
     * @see filterImportInstantiates
     * @see filterNormal
     * @see wrapLet For non-resource module: translate into a value
     *
     *      Called by ModuleResolver.load() to generate IL code for a module
     */
    @Override
    public wyvern.target.corewyvernIL.decl.Declaration topLevelGen(GenContext ctx, List<TypedModuleSpec> dependencies) {
        GenContext methodContext = ctx;
        LinkedList<ImportDeclaration> platformDependentImports = new LinkedList<ImportDeclaration>();
        LinkedList<ImportDeclaration> platformIndependentImports = new LinkedList<ImportDeclaration>();
        separatePlatformDependencies(platformDependentImports, platformIndependentImports);
        Sequence normalSeq = (inner instanceof Sequence) ? ((DeclSequence) inner).filterNormal() : new Sequence(inner);

        List<FormalArg> formalArgs = new LinkedList<>();

        // Add the generic parameters to the list of formal arguments, if they exist
        if (this.generics != null) {
            GenContext[] contexts = new GenContext[1];
            contexts[0] = methodContext;
            addGenericParameters(contexts, formalArgs);
            methodContext = contexts[0];
        }

        List<Module> loadedTypes = new LinkedList<Module>();
        formalArgs.addAll(getTypes(methodContext, loadedTypes)); // get the types of the module parameters
        wyvern.target.corewyvernIL.type.ValueType ascribedValueType = ascribedType == null ? null
                : this.getType(methodContext, loadedTypes, ascribedType.getLocation(), ascribedType.getFullName());
        for (Module lt : loadedTypes) {
            // include the declaration itself
            final BindingSite internalSite = lt.getSpec().getSite();
            methodContext = methodContext.extend(internalSite, new Variable(internalSite), lt.getSpec().getType());
            // include the type abbreviation
            methodContext = ImportDeclaration.addDepsToCtx(lt, methodContext);
            methodContext = new TypeOrEffectGenContext(lt.getSpec().getDefinedTypeName(), internalSite, methodContext);
            if (dependencies != null) {
                dependencies.add(lt.getSpec());
                dependencies.addAll(lt.getDependencies());
            }
        }

        /* adding parameters to environments */
        for (FormalArg arg : formalArgs) {
            methodContext = methodContext.extend(arg.getName(), new Variable(arg.getName()), arg.getType());
        }

        /* importing modules and instantiations are translated into a SeqExpr */
        SeqExpr seqExpr = new SeqExpr();
        GenContext extended = translateImports(platformDependentImports, methodContext, seqExpr, dependencies);
        seqExpr = new SeqExpr(); // throw away the bindings for platform dependencies, they will be added back
                                 // later
        extended = translateImports(platformIndependentImports, extended, seqExpr, dependencies);
        wyvern.target.corewyvernIL.expression.IExpr body = innerTranslate(normalSeq, extended);
        TypeContext tempContext = methodContext.getInterpreterState().getResolver()
                .extendContext(/* ctxWithPlatDeps */methodContext, dependencies);
        seqExpr.merge(body);
        body = seqExpr;

        wyvern.target.corewyvernIL.type.ValueType returnType = body.typeCheck(tempContext, null);
        if (ascribedValueType != null) {
            returnType = ascribedValueType;
        }

        // if (returnType.isEffectUnannotated(methodContext)) {
        // TODO[ql] here is where we want to transform the body
        // }

        if (platformDependentImports.size() > 0) {
            // We have platform-dependent dependencies, return a corewyvernIL
            // ModuleDeclaration
            List<Pair<ImportDeclaration, ValueType>> moduleDependencies = new LinkedList<>();
            for (ImportDeclaration imp : platformDependentImports) {
                Pair<VarBinding, GenContext> bindingCtx = imp.genBinding(methodContext,
                        new LinkedList<TypedModuleSpec>());
                moduleDependencies.add(new Pair<ImportDeclaration, ValueType>(imp, bindingCtx.getFirst().getType()));
            }
            return new wyvern.target.corewyvernIL.decl.ModuleDeclaration(name, formalArgs, returnType, body,
                    moduleDependencies, getLocation());
        }
        if (!isResource() && formalArgs.isEmpty()) {
            /* non resource module translated into value */
            return new wyvern.target.corewyvernIL.decl.ValDeclaration(name, returnType, body, getLocation());
        }
        /* resource module translated into method */
        wyvern.target.corewyvernIL.decl.DefDeclaration defDecl = new wyvern.target.corewyvernIL.decl.DefDeclaration(
                name, formalArgs, returnType, body, getLocation(), effectSet);
        return defDecl;
    }

    public List<ImportDeclaration> getImports() {
        return imports;
    }
}
