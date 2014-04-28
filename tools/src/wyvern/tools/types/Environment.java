package wyvern.tools.types;

import wyvern.tools.typedAST.core.binding.Binding;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.TypeBinding;
import wyvern.tools.typedAST.core.binding.ValueBinding;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.util.TreeWritable;
import wyvern.tools.util.TreeWriter;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class Environment implements TreeWritable {
	private Environment parentEnvironment;
	private Environment extEnv = null;
	private String name;
	private Binding binding;

	private Environment(Environment environment, Binding binding) {
		this.parentEnvironment = environment;
		this.binding = binding;
		if (binding != null)
			this.name = binding.getName();
	}

	private Environment(Environment environment, Binding binding, Environment extEnv) {
		this(environment, binding);
		this.extEnv = extEnv;
	}

	public Environment extend(Binding binding) {
		return new Environment(this, binding, extEnv);
	}
	
	public Environment extend(Environment env) {
		if (env.binding == null)
			return this;
		
		return new Environment(extend(env.parentEnvironment), env.binding, extEnv);
	}

	public static Environment getEmptyEnvironment() {
		return emptyEnvironment;
	}

	private static Environment emptyEnvironment = new Environment(null, null);

	public NameBinding lookup(String name) {
		if (this.name == null)
			return null;
		if (this.name.equals(name) && this.binding instanceof NameBinding)
			return (NameBinding) binding;
		return parentEnvironment.lookup(name);
	}

	public TypeBinding lookupType(String name) {
		if (this.name == null)
			return null;
		if (this.name.equals(name) && this.binding instanceof TypeBinding)
			return (TypeBinding) binding;
		return parentEnvironment.lookupType(name);
	}

	public Value getValue(String name) {
		if (this.name == null)
			return null;
		if (this.name.equals(name) && this.binding instanceof ValueBinding)
			return ((ValueBinding) binding).getValue(this);
		return parentEnvironment.getValue(name);
	}

	public <T> Optional<T> lookupBinding(String name, Class<T> bindingType) {
		if (this.name == null)
			return Optional.empty();
		if (this.name.equals(name) && bindingType.isAssignableFrom(this.binding.getClass()))
			return Optional.of((T)binding);
		return parentEnvironment.lookupBinding(name, bindingType);
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		/*Environment env = this;
		while (env != null) {
			writer.writeArgs(binding);
			env = env.parentEnvironment;
		}*/
	}

	public Environment setInternalEnv(Environment internalEnv) {
		internalEnv.extEnv = this;
		return internalEnv;
	}

	public Environment getExternalEnv() {
		return extEnv;
	}

	public List<Binding> getBindings() {
		LinkedList<Binding> bindings = new LinkedList<>();
		writeBinding(bindings);
		return bindings;
	}

	private void writeBinding(List<Binding> binding) {
		if (this.binding != null)
			binding.add(this.binding);
		if (parentEnvironment != null)
			parentEnvironment.writeBinding(binding);
	}

	public String toString() {
		if (this.binding == null)
			return "";
		if (parentEnvironment == null || parentEnvironment.binding == null)
			return this.binding.toString();
		else
			return this.binding.toString() + ", " + parentEnvironment.toString();
	}
}