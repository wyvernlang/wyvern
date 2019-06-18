package wyvern.target.corewyvernIL.effects;

import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.support.FailureReason;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;

import java.util.HashSet;
import java.util.List;

public final class EffectUtil {

    private EffectUtil() {
    }

    private static EffectSet addEffects(EffectSet effects, ValueType type, GenContext ctx) {
        EffectSet argEffects = getEffects(type, ctx);
        if (argEffects != null) {
            if (effects == null) {
                effects = new EffectSet(new HashSet<>());
            }
            effects.getEffects().addAll(argEffects.getEffects());
        }
        return effects;
    }

    private static EffectSet addHOEffects(EffectSet effects, ValueType type, GenContext ctx) {
        EffectSet argEffects = getHOEffects(type, ctx);
        if (argEffects != null) {
            if (effects == null) {
                effects = new EffectSet(new HashSet<>());
            }
            effects.getEffects().addAll(argEffects.getEffects());
        }
        return effects;
    }


    public static EffectSet getEffects(ValueType type, GenContext ctx) {
        EffectSet effects = null;
        List<DeclType> declTypes = type.getStructuralType(ctx).getDeclTypes();
        for (DeclType declType : declTypes) {
            if (declType instanceof DefDeclType) {
                DefDeclType def = (DefDeclType) (declType);
                EffectSet e = def.getEffectSet();
                if (e != null) {
                    if (effects == null) {
                        effects = new EffectSet(new HashSet<>());
                    }
                    for (Effect effect : e.getEffects()) {
                        effects.getEffects().add(effect);
                    }
                }
                List<FormalArg> recursiveArgs = ((DefDeclType) declType).getFormalArgs();
                for (FormalArg recursiveArg : recursiveArgs) {
                    ValueType argType = recursiveArg.getType();
                    if (goodType(argType) && !argType.equalsInContext(type, ctx, new FailureReason())) {
                        addHOEffects(effects, argType, ctx);
                    }
                }
                ValueType resultType = ((DefDeclType) declType).getRawResultType();
                if (goodType(resultType) && !resultType.equalsInContext(type, ctx, new FailureReason())) {
                    addEffects(effects, resultType, ctx);
                }
            }
        }
        return effects;
    }

    private static boolean goodType(ValueType t) {
        return !t.toString().contains("this.T") && !t.toString().contains("generic__U");
    }

    public static EffectSet getHOEffects(ValueType type, GenContext ctx) {
        EffectSet effects = null;
        StructuralType structuralType = type.getStructuralType(ctx);
        List<DeclType> declTypes = structuralType.getDeclTypes();
        GenContext thisCtx = ctx.extend(structuralType.getSelfSite(), structuralType);
        for (DeclType declType : declTypes) {
            if (declType instanceof DefDeclType) {
                List<FormalArg> recursiveArgs = ((DefDeclType) declType).getFormalArgs();
                for (FormalArg recursiveArg : recursiveArgs) {
                    ValueType argType = recursiveArg.getType();
                    if (goodType(argType) && !argType.equalsInContext(type, thisCtx, new FailureReason())) {
                        effects = addEffects(effects, argType, thisCtx);
                    }
                }
                ValueType resultType = ((DefDeclType) declType).getRawResultType();
                if (goodType(resultType) && !type.equalsInContext(resultType, thisCtx, new FailureReason())) {
                    effects = addHOEffects(effects, resultType, thisCtx);
                }
            }
        }
        return effects;
    }
}
