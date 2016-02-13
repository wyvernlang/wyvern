package wyvern.target.oir;

import java.util.HashMap;
import java.util.LinkedList;

import wyvern.target.oir.declarations.OIRType;

public class OIREnvironment {
	private OIREnvironment parent;
	private HashMap<String, OIRType> nameTable;
	private HashMap<String, OIRType> typeTable;
	private LinkedList<OIREnvironment> children;
	
	private static OIREnvironment rootEnvironment = new OIREnvironment(null);
	
	public static OIREnvironment getRootEnvironment ()
	{
		return rootEnvironment;
	}
	
	public OIREnvironment (OIREnvironment parent)
	{
		children = new LinkedList<OIREnvironment> ();
		nameTable = new HashMap<String, OIRType> ();
		typeTable = new HashMap<String, OIRType> ();
		this.parent = parent;
		
		if (parent != null)
		{
			parent.addChild(this);
		}
	}
	
	public void addChild (OIREnvironment environment)
	{
		children.add (environment);
	}
	
	public void addName (String name, OIRType type)
	{
		nameTable.put(name, type);
	}
	
	public void addType (String name, OIRType type)
	{
		nameTable.put(name, type);
	}

	public OIRType lookup(String name) 
	{
		if (name == null)
			return null;
		
		OIRType type;
		
		type = nameTable.get(name);
		if (type == null)
			return parent.lookup(name);
		return type;
	}

	public OIRType lookupType(String name) {
		if (name == null)
			return null;
		
		OIRType type;
		
		type = nameTable.get(name);
		if (type == null)
			return parent.lookup(name);
		return type;
	}
}
