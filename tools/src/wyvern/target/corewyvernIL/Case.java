package wyvern.target.corewyvernIL;

import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.RefinementType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;

public class Case extends ASTNode {
    private BindingSite site;
    private NominalType pattern;
    private Expression body;

    public Case(BindingSite site, NominalType pattern, Expression body) {
        super();
        this.pattern = pattern;
        this.body = body;
        this.site = site;
    }
    public NominalType getPattern() {
        return pattern;
    }
    public Expression getBody() {
        return body;
    }
    public String getVarName() {
        return site.getName();
    }
    public BindingSite getSite() {
        return site;
    }
    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> visitor, S state) {
        // TODO Auto-generated method stub
        return visitor.visit(state, this);
    }
    public ValueType getAdaptedPattern(ValueType matchType) {
        return adapt(this.getPattern(), matchType);
    }

    @Override
    public FileLocation getLocation() {
        return pattern.getLocation();
    }

    private ValueType adapt(NominalType basePattern, ValueType matchType) {
        if (matchType instanceof NominalType) {
            return basePattern;
        } else if (matchType instanceof RefinementType) {
            ValueType newBase = adapt(basePattern, ((RefinementType) matchType).getBase());
            return new RefinementType(newBase, (RefinementType) matchType);
        } else {
            throw new RuntimeException("invariant broken: match type must be a nominal type or a refinement");
        }
    }
}
