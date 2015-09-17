package wyvern.target.corewyvernIL;

import java.util.LinkedList;
import java.util.List;

import wyvern.target.corewyvernIL.binding.Binding;
import wyvern.target.corewyvernIL.binding.NameBinding;
import wyvern.target.corewyvernIL.binding.TypeBinding;

public class Environment {
	private Environment nextEnvironment;
	private String name;
	private Binding binding;
	
	private static Environment rootEnvironment = new Environment(null, null);
	
	public static Environment getRootEnvironment ()
	{
		return rootEnvironment;
	}
	
	public Environment (Environment parent, Binding binding)
	{
		if (parent != null)
		{
			this.nextEnvironment = parent.nextEnvironment;	
			parent.nextEnvironment = this;
		}
		
		this.binding = binding;
		if (binding != null)
			this.name = binding.getName();
	}
	
	public void setBinding (Binding binding)
	{
		this.binding = binding;
		if (binding != null)
			this.name = binding.getName ();
	}
	
	public Binding getBinding ()
	{
		return binding;
	}
	public Environment extend(Binding binding) {
		return new Environment(this, binding);
	}
	
	public Environment extend(Environment env) {
		if (env.binding == null)
			return this;
		
		return new Environment(extend(env.nextEnvironment), env.binding);
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
		return nextEnvironment.lookup(name);
	}

	public TypeBinding lookupType(String name) {
		if (this.name == null)
			return null;
		if (this.name.equals(name) && this.binding instanceof TypeBinding)
			return (TypeBinding) binding;
		return nextEnvironment.lookupType(name);
	}

	public List<Binding> getBindings() {
		LinkedList<Binding> bindings = new LinkedList<>();
		writeBinding(bindings);
		return bindings;
	}

	private void writeBinding(List<Binding> binding) {
		if (this.binding != null)
			binding.add(this.binding);
		if (nextEnvironment != null)
			nextEnvironment.writeBinding(binding);
	}
}