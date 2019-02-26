package wyvern.target.corewyvernIL.effects;

import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.support.FailureReason;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;

import java.util.HashSet;
import java.util.List;

public final class EffectUtil {

    private EffectUtil() {
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
                    if (!argType.equalsInContext(type, ctx, new FailureReason())) {
                        EffectSet argEffects = getHOEffects(argType, ctx);
                        if (argEffects != null) {
                            if (effects == null) {
                                effects = new EffectSet(new HashSet<>());
                            }
                            effects.getEffects().addAll(argEffects.getEffects());
                        }
                    }
                }
                ValueType resultType = ((DefDeclType) declType).getRawResultType();
                if (!resultType.equalsInContext(type, ctx, new FailureReason())) {
                    EffectSet effects2 = getEffects(resultType, ctx);
                    if (effects2 != null) {
                        if (effects == null) {
                            effects = new EffectSet(new HashSet<>());
                        }
                        effects.getEffects().addAll(effects2.getEffects());
                    }
                }
            }
        }
        return effects;
    }

    public static EffectSet getHOEffects(ValueType type, GenContext ctx) {
        EffectSet effects = null;
        List<DeclType> declTypes = type.getStructuralType(ctx).getDeclTypes();
        for (DeclType declType : declTypes) {
            if (declType instanceof DefDeclType) {
                List<FormalArg> recursiveArgs = ((DefDeclType) declType).getFormalArgs();
                for (FormalArg recursiveArg : recursiveArgs) {
                    ValueType argType = recursiveArg.getType();
                    if (!type.equalsInContext(argType, ctx, new FailureReason())) {
                        EffectSet argEffects = getEffects(argType, ctx);
                        if (argEffects != null) {
                            if (effects == null) {
                                effects = new EffectSet(new HashSet<>());
                            }
                            effects.getEffects().addAll(argEffects.getEffects());
                        }
                    }
                }
                ValueType resultType = ((DefDeclType) declType).getRawResultType();
                if (!type.equalsInContext(resultType, ctx, new FailureReason())) {
                    EffectSet hoEffects = getHOEffects(resultType, ctx);
                    if (hoEffects != null) {
                        if (effects == null) {
                            effects = new EffectSet(new HashSet<>());
                        }
                        effects.getEffects().addAll(hoEffects.getEffects());
                    }
                }
            }
        }
        return effects;
    }
}
