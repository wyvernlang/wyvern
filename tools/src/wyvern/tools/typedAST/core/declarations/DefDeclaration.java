package wyvern.tools.typedAST.core.declarations;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.types.extensions.Tuple;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.TreeWriter;

//Def's canonical form is: def NAME : TYPE where def m() : R -> def : m : Unit -> R

public class DefDeclaration extends Declaration implements CoreAST, BoundCode {
	protected TypedAST body; // HACK
	private NameBinding nameBinding;
	private Type type;
	private List<NameBinding> argNames; // Stored to preserve their names mostly for environments etc.
	
	public DefDeclaration(String name, Type fullType, List<NameBinding> argNames, TypedAST body, boolean isClassDef, FileLocation location) {
		if (argNames == null) { argNames = new LinkedList<NameBinding>(); }
		this.type = fullType; // getMethodType(args, returnType);
		nameBinding = new NameBindingImpl(name, this.type);
		this.body = body;
		this.argNames = argNames;
		this.isClass = isClassDef;
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
		Environment extEnv = env;
		for (NameBinding bind : argNames) {
			extEnv = extEnv.extend(bind);
		}
		if (body != null) {
			Type bodyType = body.typecheck(extEnv); // Can be null for meth inside type!
			
			Type retType;
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

}