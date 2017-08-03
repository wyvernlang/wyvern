package wyvern.target.corewyvernIL.effects;

import java.util.HashSet;
import java.util.Set;

public class EffectAccumulator {
	private Set<Effect> effectSet;
	
	public EffectAccumulator(Set<Effect> effects) {
		effectSet = effects;
	}
	
	public void initializeSet() {
		if (effectSet==null) {
			effectSet = new HashSet<Effect>();
		}
	}
	
	public void addEffects(Set<Effect> effects) {
		initializeSet();
		effectSet.addAll(effects);
	}
	
	public Set<Effect> getEffectSet() {
		return effectSet;
	}
}