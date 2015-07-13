package wyvern.target.corewyvernIL.decltype;

import java.util.List;

import wyvern.target.corewyvernIL.EmitOIR;
import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;


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
	
	public List<FormalArg> getFormalArgs ()
	{
		return args;
	}

	@Override
	public <T> T acceptVisitor(ASTVisitor <T> emitILVisitor,
			Environment env, OIREnvironment oirenv) {
		return emitILVisitor.visit(env, oirenv, this);
	}
}
