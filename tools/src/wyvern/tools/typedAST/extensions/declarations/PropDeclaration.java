package wyvern.tools.typedAST.extensions.declarations;

import wyvern.tools.typedAST.CoreAST;
import wyvern.tools.typedAST.CoreASTVisitor;
import wyvern.tools.typedAST.Declaration;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.Value;
import wyvern.tools.typedAST.binding.NameBinding;
import wyvern.tools.typedAST.binding.NameBindingImpl;
import wyvern.tools.typedAST.binding.ValueBinding;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class PropDeclaration extends Declaration implements CoreAST {
	Type definitionType;
	NameBinding binding;
	
	public PropDeclaration(String name, Type type) {
		binding = new NameBindingImpl(name, type);
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(binding.getName(), definitionType);
	}

	@Override
	protected Type doTypecheck(Environment env) {
		if (binding.getType() == null) {
			// this.definitionType = this.definition.typecheck(env);
			this.binding = new NameBindingImpl(binding.getName(), definitionType);
		}
		return binding.getType();
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		visitor.visit(this);
	}
	
	public NameBinding getBinding() {
		return binding;
	}

	@Override
	public Type getType() {
		return binding.getType();
	}

	@Override
	public String getName() {
		return binding.getName();
	}

	@Override
	protected Environment doExtend(Environment old) {
		return old.extend(binding);
	}

	@Override
	protected Environment extendWithValue(Environment old) {
		Environment newEnv = old.extend(new ValueBinding(binding.getName(), binding.getType()));
		return newEnv;
		//Environment newEnv = old.extend(new ValueBinding(binding.getName(), defValue));
	}

	@Override
	protected void evalDecl(Environment evalEnv, Environment declEnv) {
		// Value defValue = definition.evaluate(evalEnv);
		// ValueBinding vb = (ValueBinding) declEnv.lookup(binding.getName());
		// vb.setValue(defValue);
	}

	private int line = -1;
	public int getLine() {
		return this.line; // TODO: NOT IMPLEMENTED YET.
	}
}