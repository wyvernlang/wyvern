package wyvern.target.corewyvernIL.generics;

import wyvern.target.corewyvernIL.effects.Effect;
import wyvern.target.corewyvernIL.effects.EffectSet;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;

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
                EffectSet effectSet = EffectSet.parseEffects("", ga.getEffect(), false, loc);
                for (Effect e : effectSet.getEffects()) {
                    if (e.getPath() != null) {
                        continue;
                    }
                    try {
                        ValueType vt = ctx.lookupType(e.getName(), loc);
                        if (vt instanceof NominalType) {
                            NominalType nt = (NominalType) vt;
                            e.setPath(nt.getPath());
                        }
                    } catch (ToolError toolError) { }
                }
                effectSet.contextualize(ctx);
                return new GenericArgument(effectSet);
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

    public GenericArgument adapt(View v) {
        switch (this.getKind()) {
            case TYPE:
                return new GenericArgument(this.getType().adapt(v));
            case EFFECT:
                return new GenericArgument(this.getEffect().adapt(v));
            default:
                throw new RuntimeException("Unhandled corewyvernIL generic argument kind: " + this.getKind());
        }
    }

    public GenericArgument doAvoid(String varName, TypeContext ctx, int depth) {
        switch (this.getKind()) {
            case TYPE:
                return new GenericArgument(this.getType().doAvoid(varName, ctx, depth));
            case EFFECT:
                return new GenericArgument(this.getEffect().doAvoid(varName, ctx, depth));
            default:
                throw new RuntimeException("Unhandled corewyvernIL generic argument kind: " + this.getKind());
        }
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
                throw new RuntimeException("Unhandled corewyvernIL generic argument kind: " + this.kind);
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
                throw new RuntimeException("Unhandled corewyvernIL generic argument kind: " + this.kind);
        }
    }

    @Override
    public String toString() {
        switch (this.kind) {
            case TYPE:
                return "TYPE: " + this.getType().toString();
            case EFFECT:
                return "EFFECT: " + this.getEffect().toString();
            default:
                throw new RuntimeException("Unhandled corewyvernIL generic argument kind: " + this.kind);
        }
    }
}
