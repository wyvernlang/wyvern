package wyvern.target.corewyvernIL;

import java.io.IOException;

import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.type.ValueType;

public class FormalArg extends ASTNode implements IASTNode {

    private BindingSite site;
    private ValueType type;

    public FormalArg(String name, ValueType type) {
        this.site = new BindingSite(name);
        this.type = type;
    }

    @Override
    public void doPrettyPrint(Appendable dest, String indent) throws IOException {
        dest.append(site.getName()).append(':');
        type.doPrettyPrint(dest, indent);
    }

    public String getName() {
        return site.getName();
    }

    public BindingSite getSite() {
        return site;
    }

    public ValueType getType() {
        return type;
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> emitILVisitor, S state) {
        return emitILVisitor.visit(state, this);
    }
}
