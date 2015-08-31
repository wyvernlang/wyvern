package wyvern.tools.typedAST.core.declarations;

import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.expression.New;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.GenUtil;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.core.expressions.Instantiation;
import wyvern.tools.typedAST.core.expressions.TupleObject;
import wyvern.tools.typedAST.core.binding.evaluation.ValueBinding;
import wyvern.tools.typedAST.core.values.Obj;
import wyvern.tools.typedAST.interfaces.*;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.Reference;
import wyvern.tools.util.TreeWriter;

import java.util.*;
import java.util.stream.Stream;

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

	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Expression generateIL(GenContext ctx) {
		/*
		wyvern.target.corewyvernIL.decl.Declaration valDecl = this.generateDecl(ctx, null);
		List<wyvern.target.corewyvernIL.decl.Declaration> decls=
			new ArrayList<wyvern.target.corewyvernIL.decl.Declaration>();
		decls.add(valDecl);
		return new wyvern.target.corewyvernIL.expression.New(decls, ctx.generateName(), null); // type to be implemented
		*/
		return null;
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
	
	private Expression innerTranslate(Sequence normalSeq, GenContext ctx) {
		/* Sequence.innerTranslate */
		return normalSeq.generateModuleIL(ctx);
	}


	private Expression wrapLet(Sequence impInstSeq, Expression e, GenContext ctx) {
		Iterator<TypedAST> ai = impInstSeq.iterator();
		if(!ai.hasNext()) {
			return e;
		} 
		while (ai.hasNext()) {
			TypedAST ast = ai.next();
			if (ast instanceof ImportDeclaration) {
				// must be import
				ImportDeclaration imp = (ImportDeclaration) ast;
				
				e = new Let(imp.getUri().getSchemeSpecificPart(), new wyvern.target.corewyvernIL.expression.Variable(imp.getUri().getSchemeSpecificPart()), e);
			} else {
				// must be instantiate
				Instantiation inst = (Instantiation) ast;
				// generate arguments		
				TypedAST argument = inst.getArgs();
				List<Expression> args = new LinkedList<Expression>();
			    if (argument instanceof TupleObject) {
			    	for (TypedAST arg : ((TupleObject) argument).getObjects()) {
			    		args.add(arg.generateIL(ctx));
			    	}
			    } else {
			    	args.add(argument.generateIL(ctx));
			    }
		
				MethodCall instValue = 
						new MethodCall(
								new wyvern.target.corewyvernIL.expression.Variable(inst.getUri().toString()) /*path*/,
								this.name, args );
				e = new Let(inst.getName(), instValue, e);
			}			
		} 
		return e;
	}


	private List<FormalArg> getTypes(Sequence reqSeq, GenContext ctx) {
		System.out.println(reqSeq);
		List<FormalArg> types = new LinkedList<FormalArg>();
		for(Declaration d : reqSeq.getDeclIterator()) {
			String name = ((ImportDeclaration) d).getUri().getSchemeSpecificPart();
			wyvern.target.corewyvernIL.type.ValueType type = ctx.lookup(name);
			System.out.println("look up result"+type);
			types.add(new FormalArg("as"+name, type));
		}
		return types;
	}


	public boolean isResource() {
		return this.resourceFlag;
	}


	@Override
	public wyvern.target.corewyvernIL.decl.Declaration topLevelGen(GenContext ctx) {
		/* design 
		TypedAST reqSeq = eilterRequires();
		TypedAST impInstSeq = filterImportInstantiates();
		Expression reqList = trans(reqSeq);
		Expression innerSeq = filterNormal();
		Expression newExp = Newexp(name, innerSeq);
		Type reqTypes = GenUtil.getTypes(reqList);
		letExp = GenUtil.genLetWrap(imInstSeq, NewExp);
		fnVal = fnexp(reqTypes, LetExp);
		return ValExp(name, reqTypes, fnVal);
		*/
		System.out.println("inner "+inner);
		GenContext methodContext = ctx;
		Sequence reqSeq = new DeclSequence();
		Sequence impInstSeq = new DeclSequence();
		Sequence normalSeq = new DeclSequence();
		if(inner instanceof Sequence || inner instanceof DeclSequence) {
			reqSeq = ((DeclSequence) inner).filterRequires();
			impInstSeq = ((DeclSequence) inner).filterImportInstantiates();
			normalSeq = ((DeclSequence) inner).filterNormal();
		} else {
			if(inner instanceof Instantiation) impInstSeq = Sequence.append(impInstSeq, inner);
			else normalSeq = Sequence.append(normalSeq, inner);
		}
		System.out.println("ns " + normalSeq);
		List<FormalArg> formalArgs = new LinkedList<FormalArg>();
		formalArgs = getTypes(reqSeq, ctx);
		for(FormalArg arg : formalArgs) {
			System.out.println("at:" + arg.getType());
			methodContext = methodContext.extend(arg.getName(), new Variable(arg.getName()), arg.getType());
		}
		wyvern.target.corewyvernIL.expression.Expression newValue = innerTranslate(normalSeq, methodContext);
		wyvern.target.corewyvernIL.expression.Expression body = wrapLet(impInstSeq, newValue, methodContext);
		System.out.println(body);
		wyvern.target.corewyvernIL.type.ValueType returnType = body.typeCheck(methodContext);
		// non resource to be implemented 
		if(isResource() == false) {
			return new wyvern.target.corewyvernIL.decl.ValDeclaration(name, returnType, body);
		}
		return new wyvern.target.corewyvernIL.decl.DefDeclaration(name, formalArgs, returnType, body);
	}
}
