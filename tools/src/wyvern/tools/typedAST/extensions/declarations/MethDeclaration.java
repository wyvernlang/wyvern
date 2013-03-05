package wyvern.tools.typedAST.extensions.declarations;

import java.util.List;

import wyvern.tools.parsing.CoreParser;
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
	private TypedAST body;
	private NameBinding binding;
	private Type type;
	private List<NameBinding> args;
	
	public MethDeclaration(String name, List<NameBinding> args, Type returnType, TypedAST body) {
		Type argType = null;
		if (args.size() == 0)
			argType = Unit.getInstance();
		else if (args.size() == 1)
			argType = args.get(0).getType();
		else
			argType = new Tuple(args);
		type = new Arrow(argType, returnType); 
		binding = new NameBindingImpl(name, type);
		this.body = body;
		this.args = args;
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
		body.typecheck(extEnv);
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
	protected Environment extendWithValue(Environment old) {
		Environment newEnv = old.extend(new ValueBinding(binding.getName(), binding.getType()));
		return newEnv;
	}

	@Override
	protected void evalDecl(Environment evalEnv, Environment declEnv) {
		Closure closure = new Closure(this, evalEnv);
		ValueBinding vb = (ValueBinding) declEnv.lookup(binding.getName());
		vb.setValue(closure);
	}

	private int line = -1;
	public int getLine() {
		return this.line; // TODO: NOT IMPLEMENTED YET.
	}
}