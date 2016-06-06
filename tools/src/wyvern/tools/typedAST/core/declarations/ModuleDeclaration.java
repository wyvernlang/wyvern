package wyvern.tools.typedAST.core.declarations;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import javafx.beans.binding.Bindings;
import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.ContextBinding;
import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.VarBinding;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.JavaValue;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.modules.LoadedType;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.GenUtil;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.TypeGenContext;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.interop.FObject;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.evaluation.ValueBinding;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.core.expressions.Instantiation;
import wyvern.tools.typedAST.core.expressions.TupleObject;
import wyvern.tools.typedAST.core.values.Obj;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.EnvironmentExtender;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.Pair;
import wyvern.tools.util.Reference;
import wyvern.tools.util.TreeWriter;

public class ModuleDeclaration extends Declaration implements CoreAST {
	private final String name;
	private final EnvironmentExtender inner;
	private ClassType subTypeType;
	private FileLocation location;
	private ClassType selfType;
	private Reference<Environment> importEnv = new Reference<>(Environment.getEmptyEnvironment());
	private Reference<Environment> dclEnv = new Reference<>(Environment.getEmptyEnvironment());
	private Reference<Environment> typeEnv = new Reference<>(Environment.getEmptyEnvironment());
	private boolean resourceFlag;

	public ModuleDeclaration(String name, EnvironmentExtender inner, FileLocation location, boolean isResource) {
		this.name = name;
		this.inner = inner;
		this.location = location;
		this.resourceFlag = isResource;
		selfType = new ClassType(dclEnv, new Reference<>(), new LinkedList<>(), null, name);
		subTypeType = new ClassType(typeEnv, new Reference<>(), new LinkedList<>(), null, name);
		if (isResource) {
			selfType.setAsResource();
			subTypeType.setAsResource();
		} else {
			selfType.setAsModule();
			subTypeType.setAsModule();
		}
	}


	@Override
	public String getName() {
		return name;
	}

	@Override
	protected Type doTypecheck(Environment env) {
		inner.typecheck(env, Optional.empty());

		// TODO: Implement type checking for modules (resource vs import etc).
		// System.out.println("DEBUG: Type checking a module declaration named " + this.name);
		// System.out.println("DEBUG: Is it a resource module? " + this.resourceFlag);

		return new Unit();
	}

	private Iterable<TypedAST> getInnerIterable() {
		if (inner instanceof Sequence) {
			return ((Sequence) inner).getIterator();
		}
		final Reference<Boolean> gotten = new Reference<>(false);
		return () -> new Iterator<TypedAST>() {
			@Override
			public boolean hasNext() {
				return !gotten.get();
			}

			@Override
			public EnvironmentExtender next() {
				gotten.set(true);
				return inner;
			}
		};
	}

	boolean extGuard = false;
	@Override
	protected Environment doExtend(Environment old, Environment against) {
		if (!extGuard) {
			dclEnv.set(inner.extend(dclEnv.get(), old.extend(dclEnv.get())));
		}
		return old.extend(new NameBindingImpl(name, selfType)).extend(new TypeBinding(name, subTypeType));
	}

	boolean typeGuard = false;
	@Override
	public Environment extendType(Environment extend, Environment against) {
		if (!typeGuard) {
			for (TypedAST ast : getInnerIterable()) {
				if (ast instanceof ImportDeclaration) {
					importEnv.set(((ImportDeclaration) ast).extendType(importEnv.get(), Globals.getStandardEnv()));
				} else if (ast instanceof EnvironmentExtender) {
					Environment delta = ((EnvironmentExtender) ast).extendType(Environment.getEmptyEnvironment(), importEnv.get().extend(Globals.getStandardEnv()));
					dclEnv.set(dclEnv.get().extend(delta));
					delta.getBindings().stream()
							.flatMap(bndg -> (bndg instanceof TypeBinding)? Stream.of((TypeBinding)bndg) : Stream.empty())
							.forEach(bndg -> typeEnv.set(typeEnv.get().extend(bndg)));
				}
			}
			typeGuard = true;
		}
		return extend;
	}

	boolean nameGuard = false;
	@Override
	public Environment extendName(Environment env, Environment against) {
		if (!nameGuard) {
			for (TypedAST ast : getInnerIterable()) {
				if (ast instanceof ImportDeclaration) {
					importEnv.set(((ImportDeclaration) ast).extendName(importEnv.get(), Globals.getStandardEnv()));
				} else if (ast instanceof EnvironmentExtender) {
					dclEnv.set(((EnvironmentExtender) ast).extendName(dclEnv.get(),
							Globals.getStandardEnv().extend(importEnv.get()).extend(dclEnv.get())));
				}
			}
			nameGuard = true;
		}
		return env.extend(new NameBindingImpl(name, selfType)).extend(new TypeBinding(name, subTypeType));
	}

	@Override
	public EvaluationEnvironment extendWithValue(EvaluationEnvironment old) {
		return old.extend(new ValueBinding(name, selfType));
	}

	@Override
	public void evalDecl(EvaluationEnvironment evalEnv, EvaluationEnvironment declEnv) {
		ValueBinding selfBinding = declEnv.lookup(name).get();
		EvaluationEnvironment objEnv = EvaluationEnvironment.EMPTY;
		Value selfV = new Obj(inner.evalDecl(objEnv), null);
		selfBinding.setValue(selfV);
	}

	@Override
	public Type getType() {
		return new Unit();
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		Map<String, TypedAST> childMap = new HashMap<>();
		if (inner != null)
			childMap.put("body", inner);
		return childMap;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		ModuleDeclaration newDecl = new ModuleDeclaration(name, (EnvironmentExtender) newChildren.get("body"), getLocation(), isResource());
		newDecl.selfType = selfType;
		newDecl.subTypeType = subTypeType;
		newDecl.importEnv = importEnv;
		newDecl.typeEnv = typeEnv;
		newDecl.dclEnv = dclEnv;
		return newDecl;
	}

	@Override
	public void codegenToIL(GenerationEnvironment environment, ILWriter writer) {
		throw new RuntimeException("Cannot codegen modules yet");
	}

	@Override
	public FileLocation getLocation() {
		return location;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(name);
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		visitor.visit(this);
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
	private Expression innerTranslate(Sequence normalSeq, GenContext ctx) {
		/* Sequence.innerTranslate */
		return normalSeq.generateModuleIL(ctx, true);
	}


	/**
	 * @see wraoLetWithIterator
	 *
	 * @param impInstSeq the sequence of import and instantiate
	 * @param normalSeq the rest sequence
	 * @param ctx the context
	 * @return new IL expression
	 */
	private Expression wrapLet(Sequence impInstSeq, Sequence normalSeq, GenContext ctx, List<TypedModuleSpec> dependencies) {
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
	private Expression wrapLetWithIterator(Iterator<TypedAST> ai, Sequence normalSeq, GenContext ctx, List<TypedModuleSpec> dependencies) {
		if(!ai.hasNext()) {
			return innerTranslate(normalSeq, ctx);
		}

		TypedAST ast = ai.next();
		if (ast instanceof ImportDeclaration) {

			// must be import
			ImportDeclaration imp = (ImportDeclaration) ast;
			
			Pair<VarBinding, GenContext> bindingAndCtx = imp.genBinding(ctx, dependencies);
			
			Expression e = wrapLetWithIterator(ai, normalSeq, bindingAndCtx.second, dependencies);
			final Let letBinding = new Let(bindingAndCtx.first, e);
			
			//ValueType t = letBinding.typeCheck(ctx); // sanity check - catch errors early
			return letBinding;
		} else {
			// must be instantiate

			Instantiation inst = (Instantiation) ast;
			// generate arguments
			TypedAST argument = inst.getArgs();
			List<Expression> args = new LinkedList<Expression>();
		    if (argument instanceof TupleObject) {
		    	for (ExpressionAST arg : ((TupleObject) argument).getObjects()) {
		    		args.add(arg.generateIL(ctx, null));
		    	}
		    } else {
		    	if(! (argument instanceof UnitVal)) {
		    		/* single argument */
			    	args.add(((ExpressionAST)argument).generateIL(ctx, null));
		    	}
		    	/* no argument */
		    }

			MethodCall instValue =
					new MethodCall(
							new wyvern.target.corewyvernIL.expression.Variable(inst.getUri().getSchemeSpecificPart().toString()) /*path*/,
							inst.getUri().getSchemeSpecificPart().toString(), args, this);
			final ValueType type = instValue.typeCheck(ctx);
			GenContext newContext = ctx.extend(inst.getName(), instValue, type);

			Expression e = wrapLetWithIterator(ai, normalSeq, newContext, dependencies);
			return new Let(inst.getName(), type, instValue, e);
		}
	}


	private List<FormalArg> getTypes(Sequence reqSeq, GenContext ctx, List<LoadedType> loadedTypes) {
		/* generate the formal arguments by requiring sequence */
		List<FormalArg> types = new LinkedList<FormalArg>();
		for(Declaration d : reqSeq.getDeclIterator()) {
			ImportDeclaration req = (ImportDeclaration) d;
			String name = req.getUri().getSchemeSpecificPart();
			wyvern.target.corewyvernIL.type.ValueType type = null;
			if (ctx.isPresent(name)) {
				type = ctx.lookupType(name);
			} else {
				LoadedType lt = ctx.getInterpreterState().getResolver().resolveType(name);
				type = new NominalType(lt.getModule().getSpec().getInternalName(), lt.getTypeName());
				//bindings.add(binding);
				loadedTypes.add(lt);
			}
			types.add(new FormalArg(req.getAsName(), type));
		}
		return types;
	}


	public boolean isResource() {
		return this.resourceFlag;
	}


	/**
	 * For resource module: translate into def method(list of require types) : </br>
	 * resource type { let (sequences of instantiate/import) in rest}; </br>
	 * @see filterRequires
	 * @see filterImportInstantiates
	 * @see filterNormal
	 * @see wrapLet
	 * For non-resource module: translate into a value
	 */
	@Override
	public wyvern.target.corewyvernIL.decl.Declaration topLevelGen(GenContext ctx, List<TypedModuleSpec> dependencies) {
		GenContext methodContext = ctx;
		Sequence reqSeq = new DeclSequence();
		Sequence impInstSeq = new DeclSequence();
		Sequence normalSeq = new Sequence();
		if(inner instanceof Sequence || inner instanceof DeclSequence) {
			/* classify declarations */
			reqSeq = ((DeclSequence) inner).filterRequires();
			impInstSeq = ((DeclSequence) inner).filterImportInstantiates();
			normalSeq = ((DeclSequence) inner).filterNormal();
		} else {
			/* single declaration in module */
			if(inner instanceof Instantiation) impInstSeq = Sequence.append(impInstSeq, inner);
			else normalSeq = Sequence.append(normalSeq, inner);
		}

		List<FormalArg> formalArgs;
		List<LoadedType> loadedTypes = new LinkedList<LoadedType>();
		formalArgs = getTypes(reqSeq, ctx, loadedTypes); // translate requiring modules to method parameters
		for (LoadedType lt : loadedTypes) {
			// include the declaration itself
			final String qualifiedName = lt.getModule().getSpec().getQualifiedName();
			final String internalName = lt.getModule().getSpec().getInternalName();
			methodContext = methodContext.extend(internalName, new Variable(internalName), lt.getModule().getSpec().getType());
			// include the type abbreviation
			methodContext = new TypeGenContext(lt.getTypeName(), internalName, methodContext);
			if (dependencies != null)
				dependencies.add(lt.getModule().getSpec());
		}

		/* adding parameters to environments */
		for(FormalArg arg : formalArgs) {
			methodContext = methodContext.extend(arg.getName(), new Variable(arg.getName()), arg.getType());
		}
	    /* importing modules and instantiations are translated into let sentence */
		wyvern.target.corewyvernIL.expression.Expression body = wrapLet(impInstSeq, normalSeq, methodContext, dependencies);
		TypeContext tempContext = methodContext.getInterpreterState().getResolver().extendContext(methodContext, dependencies);
		wyvern.target.corewyvernIL.type.ValueType returnType = body.typeCheck(tempContext);

		if(isResource() == false) {
			/* non resource module translated into value */
			return new wyvern.target.corewyvernIL.decl.ValDeclaration(name, returnType, body, getLocation());
		}
		/* resource module translated into method */
		return new wyvern.target.corewyvernIL.decl.DefDeclaration(name, formalArgs, returnType, body, getLocation());
	}
}
