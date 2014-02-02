package wyvern.tools.typedAST.core.declarations;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Closure;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.ValueBinding;
import wyvern.tools.typedAST.extensions.TypeAsc;
import wyvern.tools.typedAST.interfaces.BoundCode;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.types.extensions.Tuple;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.Pair;
import wyvern.tools.util.TreeWriter;

//Def's canonical form is: def NAME : TYPE where def m() : R -> def : m : Unit -> R

public class DefDeclaration extends Declaration implements CoreAST, BoundCode {
	protected TypedAST body; // HACK
	private NameBinding nameBinding;
	private Type type;
	private List<NameBinding> argNames; // Stored to preserve their names mostly for environments etc.

	private TypeAsc typeAsc;
	private List<Pair<String, TypeAsc>> args;
	
	public DefDeclaration(String name, Type fullType, List<NameBinding> argNames, TypedAST body, boolean isClassDef, FileLocation location) {
		this(name, (env) -> fullType, argNames.stream().map(binding -> new Pair<String,TypeAsc>(binding.getName(), env -> binding.getType())).collect(Collectors.<Pair<String,TypeAsc>>toList()), body, isClassDef, location);
	}

	public DefDeclaration(String name, TypeAsc asc, List<Pair<String ,TypeAsc>> args, TypedAST body, boolean isClass, FileLocation location) {
		nameBinding = new NameBindingImpl(name, null);
		this.typeAsc = asc;
		this.args = args;
		this.body = body;
		this.isClass = isClass;
		this.location = location;
	}

	
	public static Arrow getMethodType(List<NameBinding> args, Type returnType) {
		Type argType = null;
		if (args.size() == 0) {
			argType = Unit.getInstance();
			return new Arrow(argType, returnType);
		} else if (args.size() == 1) {
			argType = args.get(0).getType();
			return new Arrow(argType, returnType);
		} else {
			argType = new Tuple(args);
			return new Arrow(argType, returnType);
		}
	}
	

	private boolean isClass;
	public boolean isClass() {
		return isClass;
	}

	@Override
	public String getName() {
		return nameBinding.getName();
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(nameBinding.getName(), body);
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
		childMap.put("body", body);
		return childMap;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		return new DefDeclaration(nameBinding.getName(), type, argNames, newChildren.get("body"), isClass, location);
	}

	@Override
	protected Type doTypecheck(Environment env) {
		nameBinding = new NameBindingImpl(nameBinding.getName(), getIType(env));
		argNames = args.stream().map(pair->new NameBindingImpl(pair.first, pair.second.getAsc(env))).collect(Collectors.toList());

		Environment extEnv = env;
		for (NameBinding bind : argNames) {
			extEnv = extEnv.extend(bind);
		}
		if (body != null) {
			Type bodyType = body.typecheck(extEnv); // Can be null for meth inside type!
			
			Type retType;
			type = nameBinding.getType();
			if (type instanceof Arrow) {
				retType = ((Arrow) type).getResult();
			} else {
				retType = type;
			}

			if (bodyType != null && !bodyType.subtype(retType))
				ToolError.reportError(ErrorMessage.NOT_SUBTYPE, this, bodyType.toString(), ((Arrow)type).getResult().toString());
		}
		return type;
	}

	@Override
	protected Environment doExtend(Environment old) {
		nameBinding = new NameBindingImpl(nameBinding.getName(), getIType(old));
		Environment newEnv = old.extend(nameBinding);
		return newEnv;
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
		Environment newEnv = old.extend(new ValueBinding(nameBinding.getName(), nameBinding.getType()));
		return newEnv;
	}

	@Override
	public void evalDecl(Environment evalEnv, Environment declEnv) {
		Closure closure = new Closure(this, evalEnv);
		ValueBinding vb = (ValueBinding) declEnv.lookup(nameBinding.getName());
		vb.setValue(closure);
	}

	private FileLocation location = FileLocation.UNKNOWN;
	
	@Override
	public FileLocation getLocation() {
		return location; 
	}

	@Override
	public Environment extendTypes(Environment env) {
		return env;
	}

	@Override
	public Environment extendNames(Environment env) {
		Arrow type = getIType(env);
		nameBinding = new NameBindingImpl(getName(), type);

		return env.extend(nameBinding);
	}

	private Arrow getIType(Environment env) {
		List<Type> argsTypes = args.stream().map(pair->pair.second.getAsc(env)).collect(Collectors.toList());
		return new Arrow(Tuple.fromList(argsTypes), typeAsc.getAsc(env));
	}
}