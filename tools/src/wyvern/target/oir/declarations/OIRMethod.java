package wyvern.target.oir.declarations;

import wyvern.target.oir.ASTVisitor;
import wyvern.target.oir.OIREnvironment;
import wyvern.target.oir.expressions.OIRExpression;

public class OIRMethod extends OIRMemberDeclaration {
    private OIRMethodDeclaration declaration;
    private OIRExpression body;
    private OIREnvironment environment;

    public OIRMethod(OIREnvironment environment,
            OIRMethodDeclaration declaration, OIRExpression body) {
        super();
        this.environment = environment;
        this.declaration = declaration;
        this.body = body;

        for (OIRFormalArg formalArg : declaration.getArgs()) {
            environment.addName(formalArg.getName(), formalArg.getType());
        }
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
    public <S, T> T acceptVisitor(ASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }
    @Override
    public OIRType getType() {
        return declaration.getReturnType();
    }
    public OIREnvironment getEnvironment() {
        return environment;
    }
}
