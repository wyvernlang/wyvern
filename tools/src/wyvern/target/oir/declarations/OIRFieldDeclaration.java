package wyvern.target.oir.declarations;

import wyvern.target.oir.ASTVisitor;
import wyvern.target.oir.OIREnvironment;

public class OIRFieldDeclaration extends OIRMemberDeclaration {
	private String name;
	private OIRType type;
	private boolean isFinal = false;
	
	public OIRFieldDeclaration(String name, OIRType type) {
		super();
		this.name = name;
		this.type = type;
	}
	
	public OIRFieldDeclaration(String name, OIRType type, boolean isFinal) {
		super();
		this.name = name;
		this.type = type;
		this.isFinal = isFinal;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public OIRType getType() {
		return type;
	}
	public void setType(OIRType type) {
		this.type = type;
	}

	@Override
	public <T> T acceptVisitor(ASTVisitor<T> visitor, OIREnvironment oirenv) {
		return visitor.visit(oirenv, this);
	}
}
