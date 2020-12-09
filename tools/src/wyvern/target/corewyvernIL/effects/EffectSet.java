/** Represents any set of effects (definitions, method annotations, etc.)
 *
 * @author vzhao
 */
package wyvern.target.corewyvernIL.effects;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;

public class EffectSet {
    /** Parse string into set of effects. */
    public static EffectSet parseEffects(String name, String effects, boolean effectDecl, FileLocation fileLocation) {
        EffectSet effectSet = null;

        if (effects != null) { // if null, undefined (allowed by parser implementation to occur in type and any method annotations)
            if (effects == "") { // empty list of effects
                effectSet = new EffectSet(new HashSet<Effect>());
            } else if (Pattern.compile("[^a-zA-Z0-9_,. ]").matcher(effects).find()) { // found any non-effect-related chars --> probably an actual DSL block
                ToolError.reportError(ErrorMessage.MISTAKEN_DSL, fileLocation, name, effects);
            } else {
                final Set<Effect> temp = new HashSet<Effect>();
                for (final String e : effects.split(", *")) {
                    final Effect newE = parseEffect(e, name, effectDecl, fileLocation);
                    temp.add(newE);
                }
                effectSet = new EffectSet(temp);
            }
        }

        return effectSet;
    }

    /** Parse string into single Effect object. */
    private static Effect parseEffect(String e, String name, boolean effectDecl, FileLocation fileLocation) {
        e = e.trim(); // remove leading/trailing spaces

        if (e.contains(".")) { // effect from another object
            final String[] pathAndID = e.split("\\.");
            return new Effect(new Variable(pathAndID[0], fileLocation), pathAndID[1], fileLocation);
        } else { // effect defined in the same type or module def
            if (effectDecl && name.equals(e)) { // recursive definition (ex. "effect process = {send, process}")
                ToolError.reportError(ErrorMessage.RECURSIVE_EFFECT, fileLocation, e);
            }
            return new Effect(null, e, fileLocation);
        }
    }

    private final Set<Effect> effectSet;
    public EffectSet(Set<Effect> effectSet) {
        this.effectSet = effectSet;
    }

    public EffectSet(Effect effect) {
        this(Collections.singleton(effect));
    }

    public Set<Effect> getEffects() {
        return effectSet;
    }

    /** Check that all effects in the set are well-formed, reports an error upon the first not found. */
    public void effectsCheck(TypeContext ctx) {
        if (getEffects() != null) {
            getEffects().stream().forEach(e -> e.effectCheck(ctx));
        }
    }

    /** for checking that a set of effects used in a type signature are based on effects found in the signature
     * (such as declared in the signature previously). */
    public void verifyInType(GenContext ctx) {
        if (getEffects() != null) {
            // TODO: what's below should check out, except verifyInType is called a lot when the effects still are on a null variable
            getEffects().stream().forEach(e -> e.findEffectDeclType(ctx));
        }
    }

    private enum DecomposeType { DEF, SUPEREFFECT, SUBEFFECT }

    /**
     * Check if this effect set is a subeffect of es
     */
    public boolean isSubeffectOf(EffectSet es, TypeContext ctx) {
        Set<Effect> effects1 = getEffects();
        Set<Effect> effects2 = es.getEffects();

        if (effects1 == null) {
            return true;
        }

        if (effects2 == null) {
            return effects1.isEmpty();
        }

        // Check if this is a subset of es
        // TODO why I need h1 and h2 to check inclusion
        Set<Effect> h1 = new HashSet<>(effects1);
        Set<Effect> h2 = new HashSet<>(effects2);
        if (h2.containsAll(h1)) {
            return true;
        }

        // Implementation of rule Subeffect-Def-1
        for (Effect e : effects2) {
            EffectSet decomposed = decomposeEffectSet(es, e, DecomposeType.DEF, ctx);
            if (decomposed != null) {
                if (isSubeffectOf(decomposed, ctx)) {
                    return true;
                }
            }
        }

        // Implementation of rule Subeffect-Def-2
        for (Effect e : effects1) {
            EffectSet decomposed = decomposeEffectSet(this, e, DecomposeType.DEF, ctx);
            if (decomposed != null) {
                if (decomposed.isSubeffectOf(es, ctx)) {
                    return true;
                }
            }
        }

        // Implementation of rule Subeffect-Upperbound
        for (Effect e : effects1) {
            EffectSet decomposed = decomposeEffectSet(this, e, DecomposeType.SUPEREFFECT, ctx);
            if (decomposed != null) {
                if (decomposed.isSubeffectOf(es, ctx)) {
                    return true;
                }
            }
        }

        // Implementation of rule Subeffect-Lowerbound
        for (Effect e : effects2) {
            EffectSet decomposed = decomposeEffectSet(es, e, DecomposeType.SUBEFFECT, ctx);
            if (decomposed != null) {
                if (this.isSubeffectOf(decomposed, ctx)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Decompose the definition of e in the EffectSet effects
     * @param effects The effect set that contains e
     * @param e Expose the definition of e
     * @return The effect set after decomposition, or null if e can't be decomposed
     */
    private EffectSet decomposeEffectSet(EffectSet effects, Effect e, DecomposeType t, TypeContext ctx) {
        //TODO I don't understand why I need h
        Set<Effect> h = new HashSet<>(effects.getEffects());
        assert (h.contains(e));

        EffectSet s = null;
        switch (t) {
            case DEF:
                s = e.effectCheck(ctx);
                break;
            case SUPEREFFECT:
                s = e.getSupereffect(ctx);
                break;
            case SUBEFFECT:
                s = e.getSubeffect(ctx);
                break;
            default:
                break;
        }
        if (s != null) {
            View view = View.from(e.getPath(), ctx);
            Set<Effect> eDef = s.adapt(view).getEffects();
            Set<Effect> newEffects = new HashSet<>();
            for (Effect f : effects.getEffects()) {
                if (!f.equals(e)) {
                    newEffects.add(f);
                }
            }
            newEffects.addAll(eDef);
            EffectSet newEffectSet = new EffectSet(newEffects);
            return newEffectSet;
        }
        return null;
    }

    //    /** Return free vars in the set (the paths of the effects). */
    //    public Set<String> getFreeVars(){
    //        Set<String> freeVars = new HashSet<String>();
    //        if (getEffects()!=null) {
    //            getEffects().stream().forEach(e -> freeVars.add(e.getPath().getName()));
    //        }
    //
    //        return freeVars;
    //    }

    @Override
    public String toString() {
        return effectSet == null ? "" : effectSet.toString().replace("[", "{").replace("]", "}").replaceAll("MOD\\$", "");
    }


    /**
     * Avoid variables and effect set stays the same
     * @return Effect set if avoidance is possible, null if not possible
     */
    public EffectSet exactAvoid(String varName, TypeContext ctx, int count) {
        if (effectSet.isEmpty()) {
            return this;
        }
        final Set<Effect> newSet = new HashSet<Effect>();
        for (final Effect e:effectSet) {
            Set<Effect> eAvoid = e.exactAvoid(varName, ctx, count);
            if (Effect.isUnscopedEffect(eAvoid)) {
                return null;
            } else {
                newSet.addAll(eAvoid);
            }
        }
        return new EffectSet(newSet);
    }

    /**
     * Avoid variables, and allows increase in effect set
     * @return Effect set if avoidance is possible, null if not possible
     */
    public EffectSet increasingAvoid(String varName, TypeContext ctx, int count) {
        if (effectSet.isEmpty()) {
            return this;
        }
        final Set<Effect> newSet = new HashSet<Effect>();
        for (final Effect e:effectSet) {
            Set<Effect> eAvoid = e.increasingAvoid(varName, ctx, count);
            if (Effect.isUnscopedEffect(eAvoid)) {
                return null;
            } else {
                newSet.addAll(eAvoid);
            }
        }
        return new EffectSet(newSet);
    }

    /**
     * Avoid variables and allows decrease in effect set
     * @return Effect set if avoidance is possible, null if not possible
     */
    public EffectSet decreasingAvoid(String varName, TypeContext ctx, int count) {
        if (effectSet.isEmpty()) {
            return this;
        }
        final Set<Effect> newSet = new HashSet<Effect>();
        for (final Effect e:effectSet) {
            Set<Effect> eAvoid = e.decreasingAvoid(varName, ctx, count);
            if (Effect.isUnscopedEffect(eAvoid)) {
                return null;
            } else {
                newSet.addAll(eAvoid);
            }
        }
        return new EffectSet(newSet);
    }

    public EffectSet doAvoid(String varName, TypeContext ctx, int count) {
        return increasingAvoid(varName, ctx, count);
    }

    public EffectSet adapt(View v) {
        final Set<Effect> newSet = new HashSet<Effect>();
        for (final Effect e:effectSet) {
            newSet.add(e.adapt(v));
        }
        return new EffectSet(newSet);
    }

    /** replaces variables in the effects with the corresponding gen expressions, where they exist */
    public void contextualize(GenContext ctx) {
        if (effectSet.isEmpty()) {
            return;
        }
        for (final Effect e:effectSet) {
            e.adaptVariables(ctx);
        }
    }

    /** converts variables in the effects without a binding site to ones with a binding site, if possible */
    public void canonicalize(TypeContext ctx) {
        for (final Effect e:effectSet) {
            e.canonicalize(ctx);
        }
    }
}
