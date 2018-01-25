package wyvern.target.oir.expressions;

import java.util.List;

import wyvern.target.oir.ASTVisitor;
import wyvern.target.oir.OIREnvironment;
import wyvern.target.oir.declarations.OIRType;

public class OIRNew extends OIRExpression {
    private List<OIRExpression> args;
    private String typeName;

    public List<OIRExpression> getArgs() {
        return args;
    }

    public void setArgs(List<OIRExpression> args) {
        this.args = args;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public OIRNew(List<OIRExpression> args, String typeName) {
        super();
        this.args = args;
        this.typeName = typeName;
    }

    @Override
    public OIRType typeCheck(OIREnvironment oirEnv) {
        setExprType(oirEnv.lookupType(typeName));
        return getExprType();
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }
}
