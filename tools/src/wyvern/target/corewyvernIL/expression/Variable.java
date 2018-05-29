package wyvern.target.corewyvernIL.expression;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import wyvern.target.corewyvernIL.BindingSite;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.effects.EffectAccumulator;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.target.corewyvernIL.type.ValueType;

public class Variable extends Expression implements Path {

    private String name;
    private BindingSite site;

    public Variable(BindingSite site) {
        this(site.getName());
        this.site = site;
    }
    public Variable(String name) {
        super();
        this.name = name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Variable other = (Variable) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public void doPrettyPrint(Appendable dest, String indent) throws IOException {
        dest.append(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public ValueType typeCheck(TypeContext env, EffectAccumulator effectAccumulator) {
        return env.lookupTypeOf(name);
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> emitILVisitor, S state) {
        return emitILVisitor.visit(state, this);
    }

    @Override
    public Value interpret(EvalContext ctx) {
        Value exp =  ctx.lookupValue(name);
        return exp;
    }

    @Override
    public boolean isPath() {
        return true;
    }

    @Override
    public Path adapt(View v) {
        return v.adapt(this);
    }

    public Set<String> getFreeVariables() {
        Set<String> freeVars = new HashSet<>();
        freeVars.add(this.getName());
        return freeVars;
    }
}
