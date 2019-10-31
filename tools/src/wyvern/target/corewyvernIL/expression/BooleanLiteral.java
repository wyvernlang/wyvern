package wyvern.target.corewyvernIL.expression;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import wyvern.stdlib.support.backend.BytecodeOuterClass;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.effects.EffectAccumulator;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;

public class BooleanLiteral extends Literal implements Invokable {
    private Boolean value;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (value ? 1231 : 1237);
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
        BooleanLiteral other = (BooleanLiteral) obj;
        if (value != other.value) {
            return false;
        }
        return true;
    }

    public BooleanLiteral(boolean value) {
        super(Util.booleanType(), null);
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public void doPrettyPrint(Appendable dest, String indent) throws IOException {
        dest.append(value ? "true" : "false");
    }

    @Override
    public BytecodeOuterClass.Expression emitBytecode() {
        BytecodeOuterClass.Expression.Literal.Builder literal = BytecodeOuterClass.Expression.Literal.newBuilder().setBooleanLiteral(value);
        return BytecodeOuterClass.Expression.newBuilder().setLiteral(literal).build();
    }

    @Override
    public ValueType typeCheck(TypeContext env, EffectAccumulator effectAccumulator) {
        return Util.booleanType();
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
        return Util.booleanType();
    }

    @Override
    public Value invoke(String methodName, List<Value> args, FileLocation loc) {
        switch (methodName) {
        case "ifTrue":
            if (this.value) {
                return new SuspendedTailCall(this.getType(), this.getLocation()) {
                    @Override public Value interpret(EvalContext ignored) {
                        return ((ObjectValue) args.get(0)).invoke("apply", new ArrayList<>());
                    }

                    @Override
                    public ValueType typeCheck(TypeContext ctx, EffectAccumulator effectAccumulator) {
                        // TODO Auto-generated method stub
                        return null;
                    }
                };
            }
            return new SuspendedTailCall(this.getType(), this.getLocation()) {
                @Override public Value interpret(EvalContext ignored) {
                    return ((ObjectValue) args.get(1)).invoke("apply", new ArrayList<>());
                }

                @Override
                public ValueType typeCheck(TypeContext ctx, EffectAccumulator effectAccumulator) {
                    // TODO Auto-generated method stub
                    return null;
                }
            };
        case "&&":
              return new BooleanLiteral(this.value && ((BooleanLiteral) args.get(0)).value);
        case "||":
              return new BooleanLiteral(this.value || ((BooleanLiteral) args.get(0)).value);
        case "!":
            return new BooleanLiteral(!this.value);
        case "==":
            return new BooleanLiteral(this.value.compareTo(((BooleanLiteral) args.get(0)).getValue()) == 0);
        case "!=":
            return new BooleanLiteral(this.value.compareTo(((BooleanLiteral) args.get(0)).getValue()) != 0);
        default:
            throw new RuntimeException();
        }
    }

    @Override
    public Value getField(String fieldName) {
        throw new RuntimeException("no fields");
    }
}

