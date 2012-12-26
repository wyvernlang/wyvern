package wyvern.tools.typedAST.extensions;

import wyvern.tools.typedAST.AbstractTypedAST;
import wyvern.tools.typedAST.CoreAST;
import wyvern.tools.typedAST.CoreASTVisitor;
import wyvern.tools.typedAST.Value;
import wyvern.tools.typedAST.binding.NameBinding;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;


public class Variable extends AbstractTypedAST implements CoreAST {
	private NameBinding binding;
	
	public Variable(NameBinding binding) {
		this.binding = binding;
	}

	public String getName() {
		return this.binding.getName();
	}
	
	@Override
	public Type getType() {
		return binding.getType();
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(binding.getName());		
	}

	@Override
	public Type typecheck() {
		return getType();
	}

	@Override
	public Value evaluate(Environment env) {
		Value value = env.getValue(binding.getName());
		assert value != null;
		return value;
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		visitor.visit(this);
	}

}
