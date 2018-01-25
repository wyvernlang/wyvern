/** Accumulates effects into a set.
 *
 * @author vzhao
 */

package wyvern.target.corewyvernIL.effects;

import java.util.HashSet;
import java.util.Set;

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
}