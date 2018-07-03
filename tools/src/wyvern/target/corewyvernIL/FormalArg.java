package wyvern.target.corewyvernIL;

import java.io.IOException;

import wyvern.stdlib.support.backend.BytecodeOuterClass;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.type.ValueType;

public class FormalArg extends ASTNode implements IASTNode {

    private BindingSite site;
    private ValueType type;

    public FormalArg(String name, ValueType type) {
        this(new BindingSite(name), type);
    }

    public FormalArg(BindingSite s, ValueType type) {
        this.site = s;
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

    public BytecodeOuterClass.MethodArgument emitBytecode() {
        return BytecodeOuterClass.MethodArgument.newBuilder().setVariable(getName()).setType(type.emitBytecodeType()).build();
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> emitILVisitor, S state) {
        return emitILVisitor.visit(state, this);
    }
}
