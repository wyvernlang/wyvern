package wyvern.target.corewyvernIL.expression;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.effects.EffectAccumulator;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;

public class CharacterLiteral extends Literal implements Invokable {
    private java.lang.Character value;

    public CharacterLiteral(java.lang.Character value) {
        this(value, FileLocation.UNKNOWN);
    }

    public CharacterLiteral(java.lang.Character value, FileLocation loc) {
        super(Util.charType(), loc);
        this.value = value;
    }

    public java.lang.Character getValue() {
        return value;
    }

    public void setValue(java.lang.Character value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        try {
            CharacterLiteral other = (CharacterLiteral) obj;
            return this.getValue().equals(other.getValue());
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public ValueType typeCheck(TypeContext env, EffectAccumulator effectAccumulator) {
        return getType();
    }

    @Override
    public void doPrettyPrint(Appendable dest, String indent) throws IOException {
        dest.append('\'').append(value).append('\'');
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> emitILVisitor, S state) {
        return emitILVisitor.visit(state, this);
    }

    @Override
    public Set<String> getFreeVariables() {
        return new HashSet<>();
    }


    @Override
    public ValueType getType() {
        return Util.charType();
    }

    @Override
    public Value invoke(String methodName, List<Value> args, FileLocation loc) {
        switch (methodName) {
        case "<": return new BooleanLiteral(this.value.compareTo(((CharacterLiteral) args.get(0)).getValue()) < 0);
        case ">": return new BooleanLiteral(this.value.compareTo(((CharacterLiteral) args.get(0)).getValue()) > 0);
        case "==": return new BooleanLiteral(this.value.compareTo(((CharacterLiteral) args.get(0)).getValue()) == 0);
        case "<=": return new BooleanLiteral(this.value.compareTo(((CharacterLiteral) args.get(0)).getValue()) <= 0);
        case ">=": return new BooleanLiteral(this.value.compareTo(((CharacterLiteral) args.get(0)).getValue()) >= 0);
        case "!=": return new BooleanLiteral(this.value.compareTo(((CharacterLiteral) args.get(0)).getValue()) != 0);
        default: throw new RuntimeException("runtime error: character operation " + methodName + "not supported by the runtime");
        }
    }

    @Override
    public Value getField(String fieldName) {
        throw new RuntimeException("no fields");
    }

}
