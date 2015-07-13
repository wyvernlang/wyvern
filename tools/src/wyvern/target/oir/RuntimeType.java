package wyvern.target.oir;

public class RuntimeType {
	private String className;
	private String fieldValueAssigned;
	private long fieldAddress;
	private String fieldValueClass;
	private RuntimeType parent;
	
	public RuntimeType (String className, String fieldValueAssigned, 
			long fieldAddress, String fieldValueClass, RuntimeType parent)
	{
		this.className = className;
		this.fieldAddress = fieldAddress;
		this.fieldValueAssigned = fieldValueAssigned;
		this.fieldValueClass = fieldValueClass;
		this.parent = parent;
	}
}
