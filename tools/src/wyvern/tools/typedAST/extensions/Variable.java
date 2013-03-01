package wyvern.tools.typedAST.extensions;

import static wyvern.tools.errors.ErrorMessage.VARIABLE_NOT_DECLARED;
import static wyvern.tools.errors.ToolError.reportError;
import wyvern.tools.typedAST.Assignable;
import wyvern.tools.typedAST.AbstractTypedAST;
import wyvern.tools.typedAST.Assignment;
import wyvern.tools.typedAST.CoreAST;
import wyvern.tools.typedAST.CoreASTVisitor;
import wyvern.tools.typedAST.Value;
import wyvern.tools.typedAST.binding.Binding;
import wyvern.tools.typedAST.binding.NameBinding;
import wyvern.tools.typedAST.extensions.values.VarValue;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;


public class Variable extends AbstractTypedAST implements CoreAST, Assignable {
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
	public Type typecheck(Environment env) {
		Type type = getType();
		if (type == null) {
			String name = binding.getName();
			binding = env.lookup(name);
			if (binding == null)
				reportError(VARIABLE_NOT_DECLARED, name, this);
			else
				type = binding.getType();
		}
		return type;
	}

	@Override
	public Value evaluate(Environment env) {
		//Value value = binding.getValue(env);
		Value value = env.getValue(binding.getName());
		assert value != null;
		if (value instanceof VarValue) {
			return ((VarValue)value).getValue();
		}
		return value;
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Value evaluateAssignment(Assignment ass, Environment env) {
		Value value = env.getValue(binding.getName());
		if (!(value instanceof VarValue)) {
			throw new RuntimeException("Trying to assign a non-var");
		}
		VarValue varValue = (VarValue)value;
		
		Value newValue = ass.getValue().evaluate(env);
		varValue.setValue(newValue);
		return newValue;
	}

	private int line = -1;
	public int getLine() {
		return this.line; // TODO: NOT IMPLEMENTED YET.
	}
}
