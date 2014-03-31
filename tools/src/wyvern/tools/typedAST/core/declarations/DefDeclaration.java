package wyvern.tools.typedAST.core.declarations;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Closure;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.ValueBinding;
import wyvern.tools.typedAST.interfaces.BoundCode;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.TypeResolver;
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.types.extensions.Tuple;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.TreeWriter;

import java.util.*;

//Def's canonical form is: def NAME : TYPE where def m() : R -> def : m : Unit -> R

public class DefDeclaration extends Declaration implements CoreAST, BoundCode {
	protected TypedAST body; // HACK
	private String name;
	private Type type;
	private List<NameBinding> argNames; // Stored to preserve their names mostly for environments etc.

	public DefDeclaration(String name, Type fullType, List<NameBinding> argNames, TypedAST body, boolean isClassDef, FileLocation location) {
		if (argNames == null) { argNames = new LinkedList<NameBinding>(); }
		this.type = getMethodType(argNames, fullType);
		this.name = name;
		this.body = body;
		this.argNames = argNames;
		this.isClass = isClassDef;
		this.location = location;
	}


	public DefDeclaration(String name, Type fullType, List<NameBinding> argNames, TypedAST body, boolean isClassDef) {
		if (argNames == null) { argNames = new LinkedList<NameBinding>(); }
		this.type = fullType;
		this.name = name;
		this.body = body;
		this.argNames = argNames;
		this.isClass = isClassDef;
	}


	public static Arrow getMethodType(List<NameBinding> args, Type returnType) {
		Type argType = null;
		if (args.size() == 0) {
			argType = Unit.getInstance();
		} else if (args.size() == 1) {
			argType = args.get(0).getType();
		} else {
			argType = new Tuple(args);
		}
		return new Arrow(argType, returnType);
	}
	

	private boolean isClass;
	public boolean isClass() {
		return isClass;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		// TODO: implement me
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
		return new DefDeclaration(name, type, argNames, newChildren.get("body"), isClass, location);
	}

	@Override
	protected Type doTypecheck(Environment env) {
		Environment extEnv = env;
		for (NameBinding bind : argNames) {
			extEnv = extEnv.extend(bind);
		}
		if (body != null) {
			Type bodyType = body.typecheck(extEnv, Optional.empty()); // Can be null for meth inside type!
			type = TypeResolver.resolve(type, env);
			
			Type retType = ((Arrow)type).getResult();
			
			if (bodyType != null && !bodyType.subtype(retType))
				ToolError.reportError(ErrorMessage.NOT_SUBTYPE, this, bodyType.toString(), ((Arrow)type).getResult().toString());
		}
		return type;
	}

	@Override
	protected Environment doExtend(Environment old) {
		return extendName(old, old);
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
	public Environment extendWithValue(Environment old) {
		Environment newEnv = old.extend(new ValueBinding(name, type));
		return newEnv;
	}

	@Override
	public void evalDecl(Environment evalEnv, Environment declEnv) {
		Closure closure = new Closure(this, evalEnv);
		ValueBinding vb = (ValueBinding) declEnv.lookup(name);
		vb.setValue(closure);
	}

	private FileLocation location = FileLocation.UNKNOWN;
	
	@Override
	public FileLocation getLocation() {
		return location; 
	}

	@Override
	public Environment extendType(Environment env) {
		return env;
	}

	@Override
	public Environment extendName(Environment env, Environment against) {
		for (int i = 0; i < argNames.size(); i++) {
			NameBinding oldBinding = argNames.get(i);
			argNames.set(i, new NameBindingImpl(oldBinding.getName(), TypeResolver.resolve(oldBinding.getType(), against)));
		}
		return env.extend(new NameBindingImpl(name, TypeResolver.resolve(type, against)));
	}
}