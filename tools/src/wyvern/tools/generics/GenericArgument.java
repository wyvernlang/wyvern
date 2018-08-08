package wyvern.tools.generics;

import wyvern.tools.types.Type;

/**
 * A union type of the things that can serve as generic arguments.
 *
 * @author justinlubin
 */
public class GenericArgument {
    private final GenericKind kind;

    private final Type type;
    private final String effect;

    public GenericArgument(Type type) {
        this.kind = GenericKind.TYPE;
        this.type = type;
        this.effect = null;
    }

    public GenericArgument(String effect) {
        this.kind = GenericKind.EFFECT;
        this.type = null;
        this.effect = effect;
    }

    public GenericArgument(GenericKind kind, Object data) {
        this.kind = kind;
        switch (kind) {
            case TYPE:
                this.type = (Type) data;
                this.effect = null;
                break;
            case EFFECT:
                this.type = null;
                this.effect = (String) data;
                break;
            default:
                throw new RuntimeException("Unhandled generic argument kind: " + kind);
        }
    }

    public GenericKind getKind() {
        return this.kind;
    }

    public Type getType() {
        if (this.kind != GenericKind.TYPE) {
            throw new RuntimeException("Access to TYPE union member with mismatched kind " + this.kind);
        }
        return this.type;
    }

    public String getEffect() {
        if (this.kind != GenericKind.EFFECT) {
            throw new RuntimeException("Access to EFFECT union member with mismatched kind " + this.kind);
        }
        return this.effect;
    }

    @Override
    public String toString() {
        switch (this.kind) {
            case TYPE:
                return "TYPE: " + this.getType().toString();
            case EFFECT:
                return "EFFECT: " + this.getEffect();
            default:
                throw new RuntimeException("Unhandled generic argument kind: " + this.kind);
        }
    }
}
