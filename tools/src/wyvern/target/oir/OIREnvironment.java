package wyvern.target.oir;

import java.util.LinkedList;
import java.util.List;

import wyvern.target.oir.declarations.OIRClassDeclaration;
import wyvern.target.oir.declarations.OIRFormalArg;
import wyvern.target.oir.declarations.OIRInterface;
import wyvern.target.oir.declarations.OIRMemberDeclaration;
import wyvern.target.oir.declarations.OIRMethod;
import wyvern.target.oir.declarations.OIRMethodDeclaration;
import wyvern.target.oir.declarations.OIRType;

public class OIREnvironment {
	private OIREnvironment nextEnvironment;
	private String name;
	private OIRBinding binding;
	
	private static OIREnvironment rootEnvironment = new OIREnvironment(null, null);
	
	public static OIREnvironment getRootEnvironment ()
	{
		return rootEnvironment;
	}
	
	public OIREnvironment (OIREnvironment prev, OIRBinding binding)
	{
		if (prev != null)
		{
			if (prev.nextEnvironment != null)
			{
				this.nextEnvironment = prev.nextEnvironment;
			}
			
			prev.nextEnvironment = this;
		}
		
		this.binding = binding;
		if (binding != null)
			this.name = binding.getName();
	}
	
	public void setBinding (OIRBinding binding)
	{
		this.binding = binding;
		if (binding != null)
			this.name = binding.getName ();
	}
	
	public OIRBinding getBinding ()
	{
		return binding;
	}
	public OIREnvironment extend(OIRBinding binding) {
		return new OIREnvironment(this, binding);
	}
	
	public OIREnvironment extend(OIREnvironment env) {
		if (env.binding == null)
			return this;
		
		return new OIREnvironment(extend(env.nextEnvironment), env.binding);
	}

	public static OIREnvironment getEmptyEnvironment() {
		return emptyEnvironment;
	}

	private static OIREnvironment emptyEnvironment = new OIREnvironment(null, null);

	public OIRType lookup(String name) {
		if (name == null)
			return null;
		if (name.equals(name) && binding instanceof OIRNameBinding)
			return binding.getType();
		if (binding.getType() instanceof OIRClassDeclaration)
		{
			OIRClassDeclaration classDecl;
			OIRType toReturn;
			
			classDecl = ((OIRClassDeclaration)binding.getType());
		
			if (classDecl.getSelfName() == name)
				return binding.getType();
			
			toReturn = classDecl.getTypeForMember(name);
			if (toReturn != null)
				return toReturn;
			
			for (OIRMemberDeclaration memDecl : classDecl.getMembers())
			{
				if (memDecl instanceof OIRMethod)
				{
					OIRMethodDeclaration methDecl = ((OIRMethod)memDecl).getDeclaration();
					
					for (OIRFormalArg arg : methDecl.getArgs())
					{
						if (arg.getName() == name)
							return arg.getType();
					}
				}
			}
		}
		
		if (binding.getType() instanceof OIRInterface)
		{
			OIRInterface classDecl;
			OIRType toReturn;
			
			classDecl = ((OIRInterface)binding.getType());
		
			if (classDecl.getSelfName() == name)
				return binding.getType();
			
			toReturn = classDecl.getTypeForMember(name);
			if (toReturn != null)
				return toReturn;
			
			for (OIRMethodDeclaration methDecl : classDecl.getMethods())
			{
				for (OIRFormalArg arg : methDecl.getArgs())
				{
					if (arg.getName() == name)
						return arg.getType();
				}
			}
		}
		
		return nextEnvironment.lookup(name);
	}

	public OIRType lookupType(String name) {
		if (this.name == null)
			return null;
		if (this.name.equals(name) && this.binding instanceof OIRTypeBinding)
			return binding.getType();
		return nextEnvironment.lookupType(name);
	}

	public List<OIRBinding> getBindings() {
		LinkedList<OIRBinding> bindings = new LinkedList<>();
		writeBinding(bindings);
		return bindings;
	}

	private void writeBinding(List<OIRBinding> binding) {
		if (this.binding != null)
			binding.add(this.binding);
		if (nextEnvironment != null)
			nextEnvironment.writeBinding(binding);
	}
}
