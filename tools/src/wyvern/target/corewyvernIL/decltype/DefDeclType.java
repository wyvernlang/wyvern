package wyvern.target.corewyvernIL.decltype;

import java.util.List;

import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.type.ValueType;


public class DefDeclType extends DeclType{

	private String method;
	private ValueType returnType;
	private List<FormalArg> args;
	
	public DefDeclType(String method, ValueType returnType, List<FormalArg> args) {
		super();
		this.method = method;
		this.returnType = returnType;
		this.args = args;
	}

	public String getMethod ()
	{
		return method;
	}
	
	public void setMethod (String _method)
	{
		method = _method;
	}
	
	public void setReturnType (ValueType _type)
	{
		returnType = _type;
	}
	
	public ValueType getReturnType ()
	{
		return returnType;
	}
	
	public List<FormalArg> getArgs ()
	{
		return args;
	}
}
