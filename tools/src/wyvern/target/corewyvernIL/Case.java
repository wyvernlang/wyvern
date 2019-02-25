package wyvern.target.corewyvernIL;

import java.util.List;
import java.util.stream.Collectors;

import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.decltype.ConcreteTypeMember;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.EffectDeclType;
import wyvern.target.corewyvernIL.effects.Effect;
import wyvern.target.corewyvernIL.effects.EffectSet;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.RefinementType;
import wyvern.target.corewyvernIL.type.StructuralType;
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
    public static ValueType getAdaptedPattern(NominalType pat, ValueType matchType, IExpr matchExpr, TypeContext ctx) {
        return adapt(pat, matchType, matchExpr, ctx);
    }

    @Override
    public FileLocation getLocation() {
        return pattern.getLocation();
    }
    
    private static DeclType mapRefinement(DeclType dt, Path p) {
        if (dt instanceof EffectDeclType) {
            return new EffectDeclType(dt.getName(), new EffectSet(new Effect(p, dt.getName(), dt.getLocation())), dt.getLocation());
        } else {
            return new ConcreteTypeMember(dt.getName(), new NominalType(p, dt.getName()));
        }
    }

    private static ValueType adapt(NominalType basePattern, ValueType matchType, IExpr matchExpr, TypeContext ctx) {
        if (matchType instanceof NominalType) {
            // if matchExpr is a path and if matchType has type members, refine the basePattern so that its type members are known to be the same
            if (matchExpr instanceof Path) {
                StructuralType matchST = matchType.getStructuralType(ctx);
                List<DeclType> newDTs = matchST.getDeclTypes().stream()
                    .filter(dt -> dt.isTypeOrEffectDecl())
                    .map(dt -> mapRefinement(dt, (Path) matchExpr))
                    .collect(Collectors.toList());
                if (!newDTs.isEmpty()) {
                    return new RefinementType(basePattern, newDTs, basePattern, "dontcare");
                }
            }
            return basePattern;
        } else if (matchType instanceof RefinementType) {
            ValueType newBase = adapt(basePattern, ((RefinementType) matchType).getBase(), matchExpr, ctx);
            return new RefinementType(newBase, (RefinementType) matchType);
        } else {
            throw new RuntimeException("invariant broken: match type must be a nominal type or a refinement");
        }
    }
}
