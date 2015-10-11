package wyvern.tools.typedAST.core.declarations;

import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.decltype.ValDeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.evaluation.Closure;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.evaluation.ValueBinding;
import wyvern.tools.typedAST.interfaces.BoundCode;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.transformers.ExpressionWriter;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.TypeResolver;
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.types.extensions.Tuple;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.TreeWritable;
import wyvern.tools.util.TreeWriter;

import java.util.*;
import java.util.stream.Collectors;

//Def's canonical form is: def NAME : TYPE where def m() : R -> def : m : Unit -> R

public class DefDeclaration extends Declaration implements CoreAST, BoundCode, TreeWritable {
	protected TypedAST body; // HACK
	private String name;
	private Type type;
	private List<NameBinding> argNames; // Stored to preserve their names mostly for environments etc.
	private List<FormalArg> argILTypes = new LinkedList<FormalArg>();// store to preserve IL arguments types and return types
	private wyvern.target.corewyvernIL.type.ValueType returnILType = null;

	public DefDeclaration(String name, Type fullType, List<NameBinding> argNames,
						  TypedAST body, boolean isClassDef, FileLocation location) {
		if (argNames == null) { argNames = new LinkedList<NameBinding>(); }
		this.type = getMethodType(argNames, fullType);
		this.name = name;
		this.body = body;
		this.argNames = argNames;
		this.isClass = isClassDef;
		this.location = location;
	}


	public DefDeclaration(String name, Type fullType, List<NameBinding> argNames,
						   TypedAST body, boolean isClassDef) {
		if (argNames == null) { argNames = new LinkedList<NameBinding>(); }
		this.type = fullType;
		this.name = name;
		this.body = body;
		this.argNames = argNames;
		this.isClass = isClassDef;
		this.location = location;
	}

	private DefDeclaration(String name, Type fullType, List<NameBinding> argNames,
						  TypedAST body, boolean isClassDef, FileLocation location, boolean placeholder) {
		if (argNames == null) { argNames = new LinkedList<NameBinding>(); }
		this.type = fullType;
		this.name = name;
		this.body = body;
		this.argNames = argNames;
		this.isClass = isClassDef;
		this.location = location;
	}


	public static Arrow getMethodType(List<NameBinding> args, Type returnType) {
		Type argType = null;
		if (args.size() == 0) {
			argType = new Unit();
		} else if (args.size() == 1) {
			argType = args.get(0).getType();
		} else {
			argType = new Tuple(args);
		}
		return new Arrow(argType, returnType);
	}
	

	private boolean isClass;
	public boolean isClassMember() {
		return isClass;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(name, type, body);
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		Map<String, TypedAST> childMap = new HashMap<>();
		if (body != null)
			childMap.put("body", body);
		return childMap;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		return new DefDeclaration(name, type, argNames, newChildren.get("body"), isClass, location, true);
	}

    @Override
    public void codegenToIL(GenerationEnvironment environment, ILWriter writer) {
    	ValueType valueType = getType ().generateILType();
        environment.register(getName(), valueType);
        GenerationEnvironment igen = new GenerationEnvironment(environment);

        for (NameBinding nb : argNames)
            igen.register(nb.getName(), valueType);
        writer.write(new wyvern.target.corewyvernIL.decl.DefDeclaration(name,
                argNames.stream().map(nb -> new FormalArg(nb.getName(), nb.getType().generateILType())).collect(Collectors.toList()),
                        type.generateILType(),
                        ExpressionWriter.generate(iw -> {
                            body.codegenToIL(igen, iw);
                        })));
    }

    @Override
	protected Type doTypecheck(Environment env) {
		Environment extEnv = env;
		for (NameBinding bind : argNames) {
			extEnv = extEnv.extend(bind);
		}
		if (body != null) {
			Type bodyType = body.typecheck(extEnv, Optional.of(((Arrow)type).getResult())); // Can be null for def inside type!
			type = TypeResolver.resolve(type, env);
			
			Type retType = ((Arrow)type).getResult();
			
			// System.out.println("bodyType = " + bodyType);
			// System.out.println("retType = " + retType);
			
			if (bodyType != null &&
					!bodyType.subtype(retType))
				ToolError.reportError(ErrorMessage.NOT_SUBTYPE, this, bodyType.toString(), ((Arrow)type).getResult().toString());
		}
		return type;
	}

	@Override
	protected Environment doExtend(Environment old, Environment against) {
		return extendName(old, against);
	}

	@Override
	public List<NameBinding> getArgBindings() {
		return argNames;
	}

	@Override
	public TypedAST getBody() {
		return body;
	}

	@Override
	public EvaluationEnvironment extendWithValue(EvaluationEnvironment old) {
		EvaluationEnvironment newEnv = old.extend(new ValueBinding(name, type));
		return newEnv;
	}

	@Override
	public void evalDecl(EvaluationEnvironment evalEnv, EvaluationEnvironment declEnv) {
		Closure closure = new Closure(this, evalEnv);
		ValueBinding vb = (ValueBinding) declEnv.lookup(name).get();
		vb.setValue(closure);
	}

	private FileLocation location = FileLocation.UNKNOWN;
	
	@Override
	public FileLocation getLocation() {
		return location; 
	}

	@Override
	public Environment extendType(Environment env, Environment against) {
		return env;
	}

	Type resolvedType = null;
	@Override
	public Environment extendName(Environment env, Environment against) {
		for (int i = 0; i < argNames.size(); i++) {
			NameBinding oldBinding = argNames.get(i);
			argNames.set(i, new NameBindingImpl(oldBinding.getName(), TypeResolver.resolve(oldBinding.getType(), against)));
		}
		if (resolvedType == null)
			resolvedType = TypeResolver.resolve(type, against);
		return env.extend(new NameBindingImpl(name, resolvedType));
	}


	@Override
	public Expression generateIL(GenContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public DeclType genILType(GenContext ctx) {
		List<FormalArg> args = new LinkedList<FormalArg>();
		for (NameBinding b : argNames) {
			args.add(new FormalArg(b.getName(), b.getType().getILType(ctx)));
		}
		DefDeclType ret = new DefDeclType(getName(), getResultILType(ctx), args);
		return ret;
	}


	private ValueType getResultILType(GenContext ctx) {
		return ((Arrow)type).getResult().getILType(ctx);
	}


	@Override
	public wyvern.target.corewyvernIL.decl.Declaration generateDecl(GenContext ctx, GenContext thisContext) {
		List<FormalArg> args = new LinkedList<FormalArg>();
		GenContext methodContext = thisContext;
		for (NameBinding b : argNames) {
			ValueType argType = b.getType().getILType(ctx);
			args.add(new FormalArg(b.getName(), argType));
			methodContext = methodContext.extend(b.getName(), new Variable(b.getName()), argType);
		}
		this.returnILType = this.getResultILType(ctx);
		this.argILTypes = args;
		return new wyvern.target.corewyvernIL.decl.DefDeclaration(
				        getName(), args, getResultILType(ctx), body.generateIL(methodContext));
	}


	@Override
	public wyvern.target.corewyvernIL.decl.Declaration topLevelGen(GenContext ctx) {
		return generateDecl(ctx, ctx);
	}


	public List<FormalArg> getArgILTypes() {
		return argILTypes;
	}

	public wyvern.target.corewyvernIL.type.ValueType getReturnILType() {
		return returnILType;
	}
}