package wyvern.tools.typedAST.core.expressions;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractTypedAST;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.values.VarValue;
import wyvern.tools.typedAST.interfaces.*;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.TypeResolver;
import wyvern.tools.util.TreeWriter;

import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;

import static wyvern.tools.errors.ErrorMessage.VARIABLE_NOT_DECLARED;
import static wyvern.tools.errors.ToolError.reportError;


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
	public Type typecheck(Environment env, Optional<Type> expected) {
		Type type = getType();
		if (type == null) {
			String name = binding.getName();
			binding = env.lookup(name);
			if (binding == null)
				reportError(VARIABLE_NOT_DECLARED, this, name);
			else
				type = binding.getType();
		}
		return TypeResolver.resolve(type,env);
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
	public void checkAssignment(Assignment ass, Environment env) {
		VarBinding vb = env.lookupBinding(binding.getName(), VarBinding.class).orElseThrow(() -> new RuntimeException("Cannot set a non-existent or immutable var"));
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

	@Override
	public Map<String, TypedAST> getChildren() {
		return new Hashtable<>();
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> nc) {
		return new Variable(binding, location);
	}

	private FileLocation location = FileLocation.UNKNOWN;
	public FileLocation getLocation() {
		return this.location;
	}
}