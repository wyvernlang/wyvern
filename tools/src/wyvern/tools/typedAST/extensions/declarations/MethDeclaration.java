package wyvern.tools.typedAST.extensions.declarations;

import java.util.List;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.parsing.BodyParser;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.LineSequenceParser;
import wyvern.tools.rawAST.LineSequence;
import wyvern.tools.typedAST.AbstractTypedAST;
import wyvern.tools.typedAST.BoundCode;
import wyvern.tools.typedAST.CachingTypedAST;
import wyvern.tools.typedAST.CoreAST;
import wyvern.tools.typedAST.CoreASTVisitor;
import wyvern.tools.typedAST.Declaration;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.Value;
import wyvern.tools.typedAST.binding.NameBinding;
import wyvern.tools.typedAST.binding.NameBindingImpl;
import wyvern.tools.typedAST.binding.ValueBinding;
import wyvern.tools.typedAST.extensions.Closure;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.types.extensions.Tuple;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.Pair;
import wyvern.tools.util.TreeWriter;

public class MethDeclaration extends Declaration implements CoreAST, BoundCode {
	protected TypedAST body; // HACK
	private NameBinding binding;
	private Type type;
	private List<NameBinding> args;
	private boolean isClassMeth;
	
	public MethDeclaration(String name, List<NameBinding> args, Type returnType, TypedAST body, boolean isClassMeth, FileLocation location) {
		Type argType = null;
		if (args.size() == 0) {
			argType = Unit.getInstance();
			type = new Arrow(argType, returnType);
		} else if (args.size() == 1) {
			argType = args.get(0).getType();
			type = new Arrow(argType, returnType); 
		} else {
			argType = new Tuple(args);
			type = new Arrow(argType, returnType); 
		}
		binding = new NameBindingImpl(name, type);
		this.body = body;
		this.args = args;
		this.isClassMeth = isClassMeth;
		this.location = location;
	}
	
	public boolean isClassMeth() {
		return isClassMeth;
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
		if (body != null) body.typecheck(extEnv); // Can be null for meth inside type!
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