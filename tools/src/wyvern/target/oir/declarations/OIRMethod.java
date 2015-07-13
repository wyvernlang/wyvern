package wyvern.target.oir.declarations;

import wyvern.target.oir.ASTVisitor;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;
import wyvern.target.oir.expressions.OIRExpression;

public class OIRMethod extends OIRMemberDeclaration {
	
	private OIRMethodDeclaration declaration;
	private OIRExpression body;
	
	public OIRMethod(OIRMethodDeclaration declaration, OIRExpression body) {
		super();
		this.declaration = declaration;
		this.body = body;
	}
	public OIRMethodDeclaration getDeclaration() {
		return declaration;
	}
	public void setDeclaration(OIRMethodDeclaration declaration) {
		this.declaration = declaration;
	}
	public OIRExpression getBody() {
		return body;
	}
	public void setBody(OIRExpression body) {
		this.body = body;
	}
	@Override
	public <T> T acceptVisitor(ASTVisitor<T> visitor, OIREnvironment oirenv) {
		return visitor.visit(oirenv, this);
	}
}
