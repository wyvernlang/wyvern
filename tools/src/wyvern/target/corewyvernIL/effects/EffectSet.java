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
        return effectSet == null ? "" : effectSet.toString().replace("[", "{").replace("]", "}");
    }

    public EffectSet doAvoid(String varName, TypeContext ctx, int count) {
        if (effectSet.isEmpty()) {
            return this;
        }
        final Set<Effect> newSet = new HashSet<Effect>();
        for (final Effect e:effectSet) {
            newSet.addAll(e.doAvoid(varName, ctx, count));
        }
        return new EffectSet(newSet);
    }

    public EffectSet adapt(View v) {
        final Set<Effect> newSet = new HashSet<Effect>();
        for (final Effect e:effectSet) {
            newSet.add(e.adapt(v));
        }
        return new EffectSet(newSet);
    }

    public void contextualize(GenContext ctx) {
        if (effectSet.isEmpty()) {
            return;
        }
        for (final Effect e:effectSet) {
            e.adaptVariables(ctx);
        }
    }
}
