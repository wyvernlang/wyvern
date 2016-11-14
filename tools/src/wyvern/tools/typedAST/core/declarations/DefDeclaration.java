package wyvern.tools.typedAST.core.declarations;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.decltype.AbstractTypeMember;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TopLevelContext;
import wyvern.target.corewyvernIL.support.TypeGenContext;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.evaluation.Closure;
import wyvern.tools.typedAST.core.expressions.Assignment;
import wyvern.tools.typedAST.core.expressions.Invocation;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.evaluation.ValueBinding;
import wyvern.tools.typedAST.interfaces.BoundCode;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.transformers.ExpressionWriter;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Environment;
import wyvern.tools.types.QualifiedType;
import wyvern.tools.types.Type;
import wyvern.tools.types.TypeResolver;
import wyvern.tools.types.UnresolvedType;
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.types.extensions.Tuple;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.GetterAndSetterGeneration;
import wyvern.tools.util.TreeWritable;
import wyvern.tools.util.TreeWriter;

//Def's canonical form is: def NAME : TYPE where def m() : R -> def : m : Unit -> R

public class DefDeclaration extends Declaration implements CoreAST, BoundCode, TreeWritable {
	protected ExpressionAST body; // HACK
	private String name;
	private Type type;
	private List<NameBinding> argNames; // Stored to preserve their names mostly for environments etc.
	private List<FormalArg> argILTypes = new LinkedList<FormalArg>();// store to preserve IL arguments types and return types
	private wyvern.target.corewyvernIL.type.ValueType returnILType = null;
    private List<String> generics;

    public static final String GENERIC_PREFIX = "__generic__";
    public static final String GENERIC_MEMBER = "T";

	public DefDeclaration(String name, Type returnType, List<String> generics, List<NameBinding> argNames,
						  TypedAST body, boolean isClassDef, FileLocation location) {
		if (argNames == null) { argNames = new LinkedList<NameBinding>(); }
		this.type = getMethodType(argNames, returnType);
		this.name = name;
		this.body = (ExpressionAST) body;
		this.argNames = argNames;
		this.isClass = isClassDef;
		this.location = location;

        this.generics = (generics != null) ? generics : new LinkedList<String>();
	}

	public DefDeclaration(String name, Type returnType, List<NameBinding> argNames,
						  TypedAST body, boolean isClassDef, FileLocation location) {
        this(name, returnType, null, argNames, body, isClassDef, location);
	}

	public DefDeclaration(String name, Type fullType, List<NameBinding> argNames,
						   TypedAST body, boolean isClassDef) {
		if (argNames == null) { argNames = new LinkedList<NameBinding>(); }
		this.type = fullType;
		this.name = name;
		this.body = (ExpressionAST) body;
		this.argNames = argNames;
		this.isClass = isClassDef;
        this.generics = new LinkedList<String>();
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
	    DefDeclaration dd = new DefDeclaration(name, type, argNames, newChildren.get("body"), isClass);
        dd.location = this.location;
        return dd;
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
	public DeclType genILType(GenContext ctx) {
		List<FormalArg> args = new LinkedList<FormalArg>();

        ctx = this.serializeArguments(args, ctx);

		DefDeclType ret = new DefDeclType(getName(), getResultILType(ctx), args);
		return ret;
	}

    private GenContext serializeArguments(List<FormalArg> args, GenContext ctx) {
        if(isGeneric()) {
            for(String s : this.generics) {
                ValueType type = this.genericStructuralType(s);
                String genName = GENERIC_PREFIX + s;
                args.add(new FormalArg(genName, type));

                ctx = new TypeGenContext(s, genName, ctx);
                ctx = ctx.extend(genName, new Variable(genName), type);
            }
        }

        for (NameBinding b : argNames) {
			String bName = b.getName();
            Type t =  b.getType();
            ValueType type = t.getILType(ctx);
            FormalArg fa = new FormalArg(bName, type);
			args.add(fa);
			ctx = ctx.extend(bName, new Variable(bName), type);
		}
        return ctx;
    }

    private boolean isGeneric() {
        return !this.generics.isEmpty();
    }

    public static  boolean isGeneric(FormalArg a) {
        return a.getName().startsWith(GENERIC_PREFIX);
    }

	private ValueType getResultILType(GenContext ctx) {
		Arrow a = (Arrow) this.type;
        return a.getResult().getILType(ctx);
	}

	@Override
	public wyvern.target.corewyvernIL.decl.Declaration generateDecl(GenContext ctx, GenContext thisContext) {
		List<FormalArg> args = new LinkedList<FormalArg>();
		GenContext methodContext = thisContext;
		if(isGeneric()) {

            for(String s : this.generics) {
                String genName = GENERIC_PREFIX + s;
                ValueType type = this.genericStructuralType(s);
                args.add(new FormalArg(genName, type));

                methodContext = new TypeGenContext(s, genName, methodContext);
                thisContext = new TypeGenContext(s, genName, thisContext); // TODO +s
            }
		}

		for (NameBinding b : argNames) {
;			ValueType argType = b.getType().getILType(thisContext);
			args.add(new FormalArg(b.getName(), argType));
			methodContext = methodContext.extend(b.getName(), new Variable(b.getName()), argType);
			thisContext = thisContext.extend(b.getName(), new Variable(b.getName()), argType);
		}
		this.returnILType = this.getResultILType(thisContext);
		this.argILTypes = args;
		return new wyvern.target.corewyvernIL.decl.DefDeclaration(
				        getName(), args, getResultILType(thisContext), body.generateIL(methodContext, this.returnILType, null), getLocation());
	}


	@Override
	public wyvern.target.corewyvernIL.decl.Declaration topLevelGen(GenContext ctx, List<TypedModuleSpec> dependencies) {
		return generateDecl(ctx, ctx);
	}

	@Override
	public void addModuleDecl(TopLevelContext tlc) {
		List<Expression> args = new LinkedList<Expression>();
		for(NameBinding arg : argNames) {
			args.add(new Variable(arg.getName()));
		}
		if (tlc.getReceiverName() == null)
			throw new RuntimeException("must set receiver name before addModuleDecl on a def");
		Expression body = new MethodCall(new Variable(tlc.getReceiverName()), name, args, this);
		
		if (argILTypes == null)
			throw new NullPointerException("need to call topLevelGen/generateDecl before addModuleDecl");
		wyvern.target.corewyvernIL.decl.DefDeclaration decl =
			new wyvern.target.corewyvernIL.decl.DefDeclaration(name, getArgILTypes(), getReturnILType(), body, getLocation());
		
		DeclType dt = genILType(tlc.getContext());
		tlc.addModuleDecl(decl,dt);
	}

	public List<FormalArg> getArgILTypes() {
		return argILTypes;
	}

	public wyvern.target.corewyvernIL.type.ValueType getReturnILType() {
		return returnILType;
	}
	

	/**
	 * Generate a getter method declaration for the field of an object.
	 * @param ctx: context to evaluate in.
	 * @param receiver: the object for which to make the getter.
	 * @param varName: the name of the field.
	 * @param varType: the type of the field.
	 * @return a declaration for an appropriate getter method.
	 */
	public static DefDeclaration generateGetter (GenContext ctx, wyvern.tools.typedAST.core.expressions.Variable receiver,
											  String varName, Type varType) {
		
		// The body of the getter is an invocation of the form: receiver.varName
		String getterName = GetterAndSetterGeneration.varNameToGetter(varName);
		Invocation getterBody = new Invocation(receiver, varName, null, null);
		
		// Make and return the declaration.
		wyvern.tools.typedAST.core.declarations.DefDeclaration getterDecl;
		getterDecl = new wyvern.tools.typedAST.core.declarations.DefDeclaration(getterName, varType, new LinkedList<>(),
																				getterBody, false, null);
		return getterDecl;
		
	}
	
	/**
	 * Generate a setter method declaration for the field of an object.
	 * @param ctx: context to evaluate in.
	 * @param receiver: the object for which to make the setter.
	 * @param varName: the name of the field.
	 * @param varType: the type of the field.
	 * @return a declaration for an appropriate setter method.
	 */
	public static DefDeclaration generateSetter (GenContext ctx, wyvern.tools.typedAST.core.expressions.Variable receiver,
											  String varName, Type varType) {

		// The body of the setter is an assignment of the form: receiver.varName = x
		String setterName = GetterAndSetterGeneration.varNameToSetter(varName);
		Invocation fieldGet = new Invocation(receiver, varName, null, null);
		wyvern.tools.typedAST.core.expressions.Variable valueToAssign;
		valueToAssign = new wyvern.tools.typedAST.core.expressions.Variable(new NameBindingImpl("x", null), null);
		Assignment setterBody = new Assignment(fieldGet, valueToAssign, null);
		
		// The setter takes one argument x : varType; its signature is varType -> Unit
		LinkedList<NameBinding> setterArgs = new LinkedList<>();
		setterArgs.add(new NameBindingImpl("x", varType));
		Type unitType = new UnresolvedType("Unit", receiver.getLocation());
		//Arrow setterArrType = new Arrow(varType, unitType);
		
		// Make and return the declaration.
		DefDeclaration setterDecl;
		setterDecl = new DefDeclaration(setterName, unitType, setterArgs, setterBody, false, null);
		return setterDecl;
		
	}

    public static StructuralType genericStructuralType(String genericName) {
        List<DeclType> bodyDecl = new LinkedList<>(); // these are the declarations internal to the struct
        bodyDecl.add(new AbstractTypeMember(genericName)); // the body contains only a abstract type member representing the generic type

        StructuralType genType = new StructuralType(GENERIC_PREFIX + genericName, bodyDecl);
        return genType;
    }

    @Override
    public StringBuilder prettyPrint() {
        StringBuilder sb = new StringBuilder();
        sb.append("def " + name + " : (");
        String sep = "";
        for (FormalArg arg : argILTypes) {
            sb.append(sep); sep = ", ";
            try {
                sb.append(arg.prettyPrint());
            } catch (IOException e) {
                sb.append("Unknown");
            }
        }
        sb.append(") -> ");
        sb.append(type.toString());
        sb.append(" = ");
        sb.append(body.prettyPrint());
        return sb;
    }
}
