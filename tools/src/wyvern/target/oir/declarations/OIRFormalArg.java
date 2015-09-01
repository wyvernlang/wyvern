package wyvern.target.oir.declarations;

import wyvern.target.oir.ASTVisitor;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;

public class OIRFormalArg extends OIRAST {
	private String name;
	private OIRType type;
	public OIRFormalArg(String name, OIRType type) {
		super();
		this.name = name;
		this.type = type;
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
		// TODO Auto-generated method stub
		return null;
	}
}
