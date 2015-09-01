package wyvern.target.oir.declarations;

import wyvern.target.oir.ASTVisitor;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;

public class OIRDelegate extends OIRAST {
	private OIRType type;
	private String field;
	
	public OIRDelegate(OIRType type, String field) {
		super();
		this.type = type;
		this.field = field;
	}
	public OIRType getType() {
		return type;
	}
	public void setType(OIRType type) {
		this.type = type;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	@Override
	public <T> T acceptVisitor(ASTVisitor<T> visitor, OIREnvironment oirenv) {
		// TODO Auto-generated method stub
		return null;
	}
}
