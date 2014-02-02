package wyvern.tools.typedAST.core.expressions;

import static wyvern.tools.errors.ErrorMessage.VARIABLE_NOT_DECLARED;
import static wyvern.tools.errors.ToolError.reportError;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.AbstractTypedAST;
import wyvern.tools.typedAST.core.Assignment;
import wyvern.tools.typedAST.core.binding.Binding;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.values.VarValue;
import wyvern.tools.typedAST.interfaces.*;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;


public class Variable extends AbstractTypedAST implements CoreAST, Assignable {
	private NameBinding binding;

	private String name;
	private Type type;
	
	public Variable(NameBinding binding, FileLocation location) {
		this(binding.getName(), location);
	}

	public Variable(String name, FileLocation location) {
		this.name = name;
		this.location = location;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public Type getType() {
		if (type == null)
			ToolError.reportError(ErrorMessage.VARIABLE_NOT_DECLARED, location, name);
		return type;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(name);
	}

	@Override
	public Type typecheck(Environment env) {
		binding = env.lookup(name);
		if (binding == null)
			reportError(VARIABLE_NOT_DECLARED, this, name);
		else
			type = binding.getType();
		return type;
	}

	@Override
	public Value evaluate(Environment env) {
		//Value value = binding.getValue(env);
		Value value = env.getValue(name);
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
		Value value = env.getValue(name);
		if (!(value instanceof VarValue)) {
			throw new RuntimeException("Trying to assign a non-var");
		}
		VarValue varValue = (VarValue)value;
		
		Value newValue = ass.getValue().evaluate(env);
		varValue.setValue(newValue);
		return newValue;
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		return new Hashtable<>();
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> nc) {
		Variable variable = new Variable(name, location);
		variable.type = type;
		return variable;
	}

	private FileLocation location = FileLocation.UNKNOWN;
	public FileLocation getLocation() {
		return this.location;
	}
}