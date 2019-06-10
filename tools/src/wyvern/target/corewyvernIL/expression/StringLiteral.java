package wyvern.target.corewyvernIL.expression;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import wyvern.stdlib.support.backend.BytecodeOuterClass;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.effects.EffectAccumulator;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;

public class StringLiteral extends Literal implements Invokable {

    private java.lang.String value;

    public StringLiteral(java.lang.String value) {
        this(value, FileLocation.UNKNOWN);
    }

    public StringLiteral(java.lang.String value, FileLocation loc) {
        super(Util.stringType(), loc);
        this.value = value;
    }

    public java.lang.String getValue() {
        return value;
    }

    public void setValue(java.lang.String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
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
            StringLiteral other = (StringLiteral) obj;
            return getValue().equals(other.getValue());
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public ValueType typeCheck(TypeContext env, EffectAccumulator effectAccumulator) {
        return Util.stringType();
    }

    @Override
    public void doPrettyPrint(Appendable dest, String indent) throws IOException {
        dest.append('"').append(value).append('"');
    }

    @Override
    public BytecodeOuterClass.Expression emitBytecode() {
        BytecodeOuterClass.Expression.Literal.Builder literal = BytecodeOuterClass.Expression.Literal.newBuilder().setStringLiteral(value);
        return BytecodeOuterClass.Expression.newBuilder().setLiteral(literal).build();
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
        return Util.stringType();
    }

    @Override
    public Value invoke(String methodName, List<Value> args, FileLocation loc) {
        switch (methodName) {
        case "<": return new BooleanLiteral(value.compareTo(((StringLiteral) args.get(0)).getValue()) < 0);
        case ">": return new BooleanLiteral(value.compareTo(((StringLiteral) args.get(0)).getValue()) > 0);
        case "==": return new BooleanLiteral(value.compareTo(((StringLiteral) args.get(0)).getValue()) == 0);
        case "<=": return new BooleanLiteral(value.compareTo(((StringLiteral) args.get(0)).getValue()) <= 0);
        case ">=": return new BooleanLiteral(value.compareTo(((StringLiteral) args.get(0)).getValue()) >= 0);
        case "!=": return new BooleanLiteral(value.compareTo(((StringLiteral) args.get(0)).getValue()) != 0);
        case "+": return new StringLiteral(value + ((StringLiteral) args.get(0)).getValue());
        case "equals": return new BooleanLiteral(value.equals(((StringLiteral) args.get(0)).getValue()));
        case "length": return new IntegerLiteral(value.length());
        case "charAt": return new CharacterLiteral(value.charAt(((IntegerLiteral) args.get(0)).getValue()));
        case "substring": return new StringLiteral(value.substring(((IntegerLiteral) args.get(0)).getValue(), ((IntegerLiteral) args.get(1)).getValue()));
        case "concat": return new StringLiteral(value.concat(((StringLiteral) args.get(0)).getValue()));
        case "indexOf": return new IntegerLiteral(value.indexOf(((IntegerLiteral) args.get(0)).getValue()));
        default: throw new RuntimeException("runtime error: string operation " + methodName + "not supported by the runtime");
        }
    }

    @Override
    public Value getField(String fieldName) {
        throw new RuntimeException("no fields");
    }

}
