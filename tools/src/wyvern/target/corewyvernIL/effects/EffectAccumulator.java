/** Accumulates effects into a set.
 *
 * @author vzhao
 */

package wyvern.target.corewyvernIL.effects;

import java.util.HashSet;
import java.util.Set;

import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.ValueType;

public class EffectAccumulator {
    private Set<Effect> effectSet;

    public EffectAccumulator() {
        this.effectSet = new HashSet<Effect>();
    }

    public void addEffect(Effect e) {
        effectSet.add(e);
    }

    public void addEffects(Set<Effect> effects) {
        effectSet.addAll(effects);
    }

    public Set<Effect> getEffectSet() {
        return effectSet;
    }

    @Override
    public String toString() {
        return effectSet == null ? "null" : effectSet.toString().replace("[", "{").replace("]", "}");
    }

    public void avoidVar(String varName, TypeContext ctx) {
        if (hasFreeVar(varName)) {
            Set<Effect> newSet = new HashSet<Effect>();
            for (Effect e : effectSet) {
                newSet.addAll(e.doAvoid(varName, ctx, ValueType.INIT_RECURSION_DEPTH));
            }
            effectSet = newSet;
        }
    }

    private boolean hasFreeVar(String varName) {
        for (Effect e : effectSet) {
            if (e.hasFreeVariable(varName)) {
                return true;
            }
        }
        return false;
    }
}