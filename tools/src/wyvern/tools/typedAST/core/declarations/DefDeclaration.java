package wyvern.tools.typedAST.core.declarations;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.BodyParser;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.LineSequenceParser;
import wyvern.tools.rawAST.LineSequence;
import wyvern.tools.typedAST.abs.AbstractTypedAST;
import wyvern.tools.typedAST.abs.CachingTypedAST;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Closure;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.ValueBinding;
import wyvern.tools.typedAST.interfaces.BoundCode;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.types.extensions.Tuple;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.Pair;
import wyvern.tools.util.TreeWriter;

public class DefDeclaration extends Declaration implements CoreAST, BoundCode {
	protected TypedAST body; // HACK
	private NameBinding binding;
	private Type type;
	private List<NameBinding> args;

	public DefDeclaration(String name, List<NameBinding> args, Type returnType, TypedAST body, boolean isClassFun, FileLocation location) {
		if (args == null) { args = new LinkedList<NameBinding>(); }
		type = getMethodType(args, returnType);
		binding = new NameBindingImpl(name, type);
		this.body = body;
		this.args = args;
		this.isClass = isClassFun;
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
		return binding.getName();
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
	protected Type doTypecheck(Environment env) {
		Environment extEnv = env;
		for (NameBinding bind : args) {
			extEnv = extEnv.extend(bind);
		}
		if (body != null) {
			Type bodyType = body.typecheck(extEnv); // Can be null for meth inside type!
			if (bodyType != null && !bodyType.subtype(((Arrow)type).getResult()))
				ToolError.reportError(ErrorMessage.NOT_SUBTYPE, bodyType.toString(), ((Arrow)type).getResult().toString(), this);
		}
		return type;
	}

	@Override
	protected Environment doExtend(Environment old) {
		Environment newEnv = old.extend(binding);
		return newEnv;
	}

	@Override
	public List<NameBinding> getArgBindings() {
		return args;
	}

	@Override
	public TypedAST getBody() {
		return body;
	}

	@Override
	public Environment extendWithValue(Environment old) {
		Environment newEnv = old.extend(new ValueBinding(binding.getName(), binding.getType()));
		return newEnv;
	}

	@Override
	public void evalDecl(Environment evalEnv, Environment declEnv) {
		Closure closure = new Closure(this, evalEnv);
		ValueBinding vb = (ValueBinding) declEnv.lookup(binding.getName());
		vb.setValue(closure);
	}

	private FileLocation location = FileLocation.UNKNOWN;
	
	@Override
	public FileLocation getLocation() {
		return location; 
	}
}