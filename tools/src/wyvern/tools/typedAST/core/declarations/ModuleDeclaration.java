package wyvern.tools.typedAST.core.declarations;

import java.io.File;
import java.net.URI;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.VarBinding;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.modules.Module;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.ModuleResolver;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.TypeOrEffectGenContext;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.NamedType;
import wyvern.tools.types.Type;
import wyvern.tools.types.UnresolvedType;
import wyvern.tools.util.Pair;

public class ModuleDeclaration extends Declaration implements CoreAST {
	private final String name;
	private final TypedAST inner;
	private FileLocation location;
	private NamedType ascribedType;
	private boolean resourceFlag;
	private final List<NameBindingImpl> args;

	public ModuleDeclaration(String name, List<NameBindingImpl> args, TypedAST inner, NamedType type, FileLocation location, boolean isResource) {
		this.name = name;
		this.inner = inner;
		this.location = location;
		this.resourceFlag = isResource;
		ascribedType = type;
		this.args = args;
	}


	@Override
	public String getName() {
		return name;
	}

	@Override
	public FileLocation getLocation() {
		return location;
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

	/**
	 * Generate the rest part of a module (not import/instantiate/require)
	 *
	 * @param normalSeq the declaration sequence
	 * @param ctx the context
	 * @return the IL expression
	 */
	private IExpr innerTranslate(Sequence normalSeq, GenContext ctx) {
		/* Sequence.innerTranslate */
		// The real work is done by the sequence itself.
		return normalSeq.generateModuleIL(ctx, true);
	}


	/**
	 * @see wrapLetWithIterator
	 *
	 * @param impInstSeq the sequence of import and instantiate
	 * @param normalSeq the rest sequence
	 * @param ctx the context
	 * @return new IL expression
	 */
	private IExpr wrapLet(Sequence impInstSeq, Sequence normalSeq, GenContext ctx, List<TypedModuleSpec> dependencies) {
		Iterator<TypedAST> ai = impInstSeq.iterator();
		return wrapLetWithIterator(ai, normalSeq, ctx, dependencies);
	}


	/**
	 * translate import/instantiate sequence into a let sequence and wrap the rest part inside the sequence. </br>
	 * import A as copyA => let copyA = A in {rest} </br>
	 * instantiate B(...) as copyB => let copyB = B(...) in {rest} </br>
	 *
	 * @param ai the declaration iterator
	 * @param normalSeq the rest part of the module (not instantiate/import/require)
	 * @param ctx the context
	 * @return the whole expression
	 */
    private IExpr wrapLetWithIterator(Iterator<TypedAST> ai, Sequence normalSeq, GenContext ctx, List<TypedModuleSpec> dependencies) {
        return wrapLetCtxWithIterator(ai, normalSeq, ctx, dependencies).first;
    }

	/**
	 * translate import/instantiate sequence into a let sequence and wrap the rest part inside the sequence. </br>
	 * import A as copyA => let copyA = A in {rest} </br>
	 * instantiate B(...) as copyB => let copyB = B(...) in {rest} </br>
	 *
	 * @param ai the declaration iterator
	 * @param normalSeq the rest part of the module (not instantiate/import/require)
	 * @param ctx the context
	 * @return the whole expression
	 */
	private Pair<IExpr, GenContext> wrapLetCtxWithIterator(Iterator<TypedAST> ai, Sequence normalSeq, GenContext ctx, List<TypedModuleSpec> dependencies) {
		if(!ai.hasNext()) {
			// we are done with imports/instantiates, so translate the main body of the module
			return new Pair(innerTranslate(normalSeq, ctx), ctx);
		}

		// otherwise, wrap with the outermost import/instantiate and proceed with a recursive call
		TypedAST ast = ai.next();
		if (ast instanceof ImportDeclaration) {

			// must be import
			ImportDeclaration imp = (ImportDeclaration) ast;
			
			Pair<VarBinding, GenContext> bindingAndCtx = imp.genBinding(ctx, dependencies);
			
			IExpr e = wrapLetWithIterator(ai, normalSeq, bindingAndCtx.second, dependencies);
			final Let letBinding = new Let(bindingAndCtx.first, e);
			
			//ValueType t = letBinding.typeCheck(ctx); // sanity check - catch errors early
			return new Pair(letBinding, bindingAndCtx.second);
		} else {
			// must be instantiate

			Instantiation inst = (Instantiation) ast;
			// generate arguments
			List<TypedAST> arguments = inst.getArgs();
			List<IExpr> args = new LinkedList<IExpr>();
	    	for (TypedAST arg : arguments) {
	    		args.add(((ExpressionAST) arg).generateIL(ctx, null, dependencies));
	    	}

			MethodCall instValue =
					new MethodCall(
							new wyvern.target.corewyvernIL.expression.Variable(inst.getUri().getSchemeSpecificPart().toString()) /*path*/,
							inst.getUri().getSchemeSpecificPart().toString(), args, this);
			final ValueType type = instValue.typeCheck(ctx, null);
			GenContext newContext = ctx.extend(inst.getName(), instValue, type);

			// translate the inner part of the sequence
			IExpr e = wrapLetWithIterator(ai, normalSeq, newContext, dependencies);
			
			// then wrap it with the declaration we just identified
			return new Pair(new Let(inst.getName(), type, instValue, e), newContext);
		}
	}

	/**
	 * Computes and returns the set of arguments this module requires.
	 * 
	 * loadedTypes is updated with all the types that had to be loaded in
	 * order to specify the required types.
	 * 
	 * @param reqSeq
	 * @param ctx
	 * @param loadedTypes
	 * @return
	 */
	private List<FormalArg> getTypes(GenContext ctx, List<Module> loadedTypes) {
		/* generate the formal arguments by requiring sequence */
		List<FormalArg> types = new LinkedList<FormalArg>();
		for(NameBindingImpl a : args) {
			String typeName = ((NamedType)a.getType()).getFullName();
			wyvern.target.corewyvernIL.type.ValueType type = getType(ctx,
					loadedTypes, null, typeName);
			types.add(new FormalArg(a.getName(), type));
		}
		return types;
	}


	private wyvern.target.corewyvernIL.type.ValueType getType(GenContext ctx,
			List<Module> loadedTypes, FileLocation location, String name) {
		wyvern.target.corewyvernIL.type.ValueType type = null;
		if (ctx.isPresent(name, false)) {
			type = ctx.lookupType(name, location);
		} else {
			Module lt = ctx.getInterpreterState().getResolver().resolveType(name);
			type = new NominalType(lt.getSpec().getInternalName(), lt.getSpec().getDefinedTypeName());
			//bindings.add(binding);
			loadedTypes.add(lt);
		}
		return type;
	}


	public boolean isResource() {
		return this.resourceFlag;
	}

    private boolean isPlatformPath(String platform, String path) {
        // Return true if file path ends with /platform/X/FILENAME (where X is a platform)
        Pattern p = Pattern.compile("/platform/" + platform + "/[^/]*$");
        return p.matcher(path).find();
    }

    private Pair<DeclSequence,DeclSequence> separatePlatformDependencies(Sequence impInstSeq) {
        Sequence platformDependent = new DeclSequence();
        Sequence platformIndependent = new DeclSequence();
        for (Declaration d : impInstSeq.getDeclIterator()) {
            URI uri = null;
            if (d instanceof ImportDeclaration) {
                ImportDeclaration decl = (ImportDeclaration)d;
                uri = decl.getUri();
            } else if (d instanceof Instantiation) {
                Instantiation decl = (Instantiation)d;
                uri = decl.getUri();
            }
            if (uri == null || !uri.getScheme().equals("wyv")) {
                platformIndependent = Sequence.append(platformIndependent, d);
            } else {
                File f = ModuleResolver.getLocal().resolve(uri.getSchemeSpecificPart(), false);
                if (isPlatformPath(ModuleResolver.getLocal().getPlatform(), f.getAbsolutePath()))
                    platformDependent = Sequence.append(platformDependent, d);
                else
                    platformIndependent = Sequence.append(platformIndependent, d);
            }
        }

        return new Pair<>((DeclSequence)platformIndependent, (DeclSequence)platformDependent);
    }

	/**
	 * For resource module: translate into def method(list of require types) : </br>
	 * resource type { let (sequences of instantiate/import) in rest}; </br>
	 * @see filterRequires
	 * @see filterImportInstantiates
	 * @see filterNormal
	 * @see wrapLet
	 * For non-resource module: translate into a value
	 * 
	 * Called by ModuleResolver.load() to generate IL code for a module
	 */
	@Override
	public wyvern.target.corewyvernIL.decl.Declaration topLevelGen(GenContext ctx, List<TypedModuleSpec> dependencies) {
		GenContext methodContext = ctx;
		Sequence impInstSeq = new DeclSequence();
        Sequence platformDependentSeq = new DeclSequence();
		Sequence normalSeq = new Sequence();
		if(inner instanceof Sequence || inner instanceof DeclSequence) {
			/* classify declarations */
            Pair<DeclSequence, DeclSequence> pair = separatePlatformDependencies(((DeclSequence) inner).filterImportInstantiates());
            impInstSeq = pair.first;
            platformDependentSeq = pair.second;
			normalSeq = ((DeclSequence) inner).filterNormal();
		} else {
			/* single declaration in module */
			if(inner instanceof Instantiation) impInstSeq = Sequence.append(impInstSeq, inner);
			else normalSeq = Sequence.append(normalSeq, inner);
		}

		List<FormalArg> formalArgs;
		List<Module> loadedTypes = new LinkedList<Module>();
		formalArgs = getTypes(ctx, loadedTypes); // translate requiring modules to method parameters
		wyvern.target.corewyvernIL.type.ValueType ascribedValueType = ascribedType == null ? null : this.getType(ctx, loadedTypes, ascribedType.getLocation(), ascribedType.getFullName());
		for (Module lt : loadedTypes) {
			// include the declaration itself
			final String internalName = lt.getSpec().getInternalName();
			methodContext = methodContext.extend(internalName, new Variable(internalName), lt.getSpec().getType());
			// include the type abbreviation
			methodContext = new TypeOrEffectGenContext(lt.getSpec().getDefinedTypeName(), internalName, methodContext);
			if (dependencies != null) {
				dependencies.add(lt.getSpec());
                dependencies.addAll(lt.getDependencies());
			}
		}

		/* adding parameters to environments */
		for(FormalArg arg : formalArgs) {
			methodContext = methodContext.extend(arg.getName(), new Variable(arg.getName()), arg.getType());
		}
    /* importing modules and instantiations are translated into let sentence */
		// Note: must wrap methodContext with platformDependent types first, or we will be unable to access platform-dependent imports
    GenContext ctxWithPlatDeps = wrapLetCtxWithIterator(platformDependentSeq.iterator(), new Sequence(), methodContext, new LinkedList<>()).second;
		wyvern.target.corewyvernIL.expression.IExpr body = wrapLet(impInstSeq, normalSeq, ctxWithPlatDeps, dependencies);
		TypeContext tempContext = methodContext.getInterpreterState().getResolver().extendContext(ctxWithPlatDeps, dependencies);
		wyvern.target.corewyvernIL.type.ValueType returnType = body.typeCheck(tempContext, null);
    	//GenContext ctxWithModule = ctxWithPlatDeps.extend(name, new Variable(name), returnType);
		if (ascribedValueType != null)
			returnType = ascribedValueType;

		/* commenting this out, it looks bogus!
		if (isResource() == false) {
			if (returnType.isResource(tempContext))
				ToolError.reportError(ErrorMessage.MUST_BE_A_RESOURCE_MODULE, this, this.getName());
		}*/
        if (platformDependentSeq.iterator().hasNext()) {
            // We have platform-dependent dependencies, return a corewyvernIL ModuleDeclaration
            List<Pair<ImportDeclaration, ValueType>> moduleDependencies = new LinkedList<>();
            Iterator<TypedAST> it = platformDependentSeq.iterator();
            while (it.hasNext()) {
                ImportDeclaration imp = (ImportDeclaration)it.next();
                Pair<VarBinding, GenContext> bindingCtx = imp.genBinding(methodContext, new LinkedList<TypedModuleSpec>());
                moduleDependencies.add(new Pair(imp, bindingCtx.first.getType()));
            }
            return new wyvern.target.corewyvernIL.decl.ModuleDeclaration(name, formalArgs, returnType, body, moduleDependencies, getLocation());
        }
		if(isResource() == false && formalArgs.isEmpty()) {
			/* non resource module translated into value */
			return new wyvern.target.corewyvernIL.decl.ValDeclaration(name, returnType, body, getLocation());
		}
		/* resource module translated into method */
		return new wyvern.target.corewyvernIL.decl.DefDeclaration(name, formalArgs, returnType, body, getLocation());
	}
}
