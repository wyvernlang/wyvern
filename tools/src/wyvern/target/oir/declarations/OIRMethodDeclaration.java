package wyvern.target.oir.declarations;

import java.util.List;

import wyvern.target.oir.ASTVisitor;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;

public class OIRMethodDeclaration extends OIRAST {
	private OIRType returnType;
	private String name;
	private List<OIRFormalArg> args;
	public OIRType getReturnType() {
		return returnType;
	}
	public void setReturnType(OIRType returnType) {
		this.returnType = returnType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<OIRFormalArg> getArgs() {
		return args;
	}
	public void setArgs(List<OIRFormalArg> args) {
		this.args = args;
	}
	
	public void addArgs (OIRFormalArg arg)
	{
		args.add(arg);
	}
	
	public OIRMethodDeclaration(OIRType returnType, String name, List<OIRFormalArg> args) {
		super();
		this.returnType = returnType;
		this.name = name;
		this.args = args;
	}
	@Override
	public <T> T acceptVisitor(ASTVisitor<T> visitor, OIREnvironment oirenv) {
		return visitor.visit(oirenv, this);
	}
	
}
