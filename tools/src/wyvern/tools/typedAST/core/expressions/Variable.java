package wyvern.tools.typedAST.core.expressions;

import static wyvern.tools.errors.ErrorMessage.VARIABLE_NOT_DECLARED;
import static wyvern.tools.errors.ToolError.reportError;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractTypedAST;
import wyvern.tools.typedAST.core.Assignment;
import wyvern.tools.typedAST.core.binding.Binding;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.values.VarValue;
import wyvern.tools.typedAST.interfaces.Assignable;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;


public class Variable extends AbstractTypedAST implements CoreAST, Assignable {
	private NameBinding binding;
	
	public Variable(NameBinding binding, FileLocation location) {
		this.binding = binding;
		this.location = location;
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
				reportError(VARIABLE_NOT_DECLARED, this, name);
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

	private FileLocation location = FileLocation.UNKNOWN;
	public FileLocation getLocation() {
		return this.location;
	}
}