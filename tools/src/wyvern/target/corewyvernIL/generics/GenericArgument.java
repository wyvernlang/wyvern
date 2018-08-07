package wyvern.target.corewyvernIL.generics;

import wyvern.target.corewyvernIL.effects.EffectSet;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;

/**
 * A union type of the things that can serve as generic arguments in corewyvernIL.
 *
 * @author justinlubin
 */
public class GenericArgument {
    private final GenericKind kind;

    private final ValueType type;
    private final EffectSet effect;

    public static GenericArgument fromHighLevel(GenContext ctx, FileLocation loc, wyvern.tools.generics.GenericArgument ga) {
        switch (ga.getKind()) {
            case TYPE:
                return new GenericArgument(ga.getType().getILType(ctx));
            case EFFECT:
                return new GenericArgument(EffectSet.parseEffects("", ga.getEffect(), false, loc));
            default:
                throw new RuntimeException("Unhandled generic argument kind: " + ga.getKind());
        }
    }

    public GenericArgument(ValueType type) {
        this.kind = GenericKind.TYPE;
        this.type = type;
        this.effect = null;
    }

    public GenericArgument(EffectSet effect) {
        this.kind = GenericKind.EFFECT;
        this.type = null;
        this.effect = effect;
    }

    public GenericArgument(GenericKind kind, Object data) {
        this.kind = kind;
        switch (kind) {
            case TYPE:
                this.type = (ValueType) data;
                this.effect = null;
                break;
            case EFFECT:
                this.type = null;
                this.effect = (EffectSet) data;
                break;
            default:
                throw new RuntimeException("Unhandled corewyvernIL generic argument kind: " + kind);
        }
    }

    public GenericKind getKind() {
        return this.kind;
    }

    public ValueType getType() {
        if (this.kind != GenericKind.TYPE) {
            throw new RuntimeException("Access to TYPE union member with mismatched kind " + this.kind);
        }
        return this.type;
    }

    public EffectSet getEffect() {
        if (this.kind != GenericKind.EFFECT) {
            throw new RuntimeException("Access to EFFECT union member with mismatched kind " + this.kind);
        }
        return this.effect;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GenericArgument)) {
            return false;
        }
        GenericArgument other = (GenericArgument) obj;

        if (this.kind != other.kind) {
            return false;
        }

        switch (this.kind) {
            case TYPE:
                return this.getType().equals(other.getType());
            case EFFECT:
                return this.getEffect().equals(other.getEffect());
            default:
                throw new RuntimeException("Unhandled corewyvernIL generic argument kind: " + kind);
        }
    }

    @Override
    public int hashCode() {
        switch (this.kind) {
            case TYPE:
                return this.getType().hashCode() << 1;
            case EFFECT:
                return this.getEffect().hashCode() << 1 | 1;
            default:
                throw new RuntimeException("Unhandled corewyvernIL generic argument kind: " + kind);
        }
    }
}
