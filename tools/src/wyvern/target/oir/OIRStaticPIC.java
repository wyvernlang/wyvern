package wyvern.target.oir;

import java.util.HashSet;

import wyvern.target.oir.declarations.OIRClassDeclaration;
import wyvern.target.oir.declarations.OIRType;

public class OIRStaticPIC {
	public static OIRProgram program = OIRProgram.program;
	private HashSet<String> classesWithMethod;
	private String methodName;
	
	public OIRStaticPIC (HashSet<String> classesWithMethod, String methodName)
	{
		this.methodName = methodName;
		this.classesWithMethod = classesWithMethod;
	}
	
	public OIRStaticPIC (String methodName)
	{
		this.methodName = methodName;
		this.classesWithMethod = new HashSet <String> ();
	}
	
	public boolean containsClass (String className)
	{
		return classesWithMethod.contains(className);
	}
	
	public void addClassName (String name)
	{
		classesWithMethod.add(name);
	}
}
