package wyvern.tools.types;

import wyvern.tools.parsing.LineParser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public abstract class AbstractTypeImpl implements Type {

    @Override
    public LineParser getParser() {
        return null;
    }

	@Override
	public boolean subtype(Type other, HashSet<SubtypeRelation> subtypes) {
		// S-Refl
		if (this.equals(other)) {
			return true;
		}

		// S-Assumption
		if (subtypes.contains(new SubtypeRelation(this, other))) {
			return true;
		}
		
		// S-Trans
		HashSet<Type> t2s = new HashSet<Type>();
		for (SubtypeRelation sr : subtypes) {
			if (sr.getSubtype().equals(this)) {
				t2s.add(sr.getSupertype());
			}
		}
		for (Type t : t2s) {
			if (subtypes.contains(new SubtypeRelation(t, other))) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean subtype(Type other) {
		return this.subtype(other, new HashSet<SubtypeRelation>());
	}
	
	public boolean isSimple() {
		return true; // default is correct for most types
	}
	@Override
	public Map<String, Type> getChildren() {
		return new HashMap<>();
	}

	@Override
	public Type cloneWithChildren(Map<String, Type> newChildren) {
		return this;
	}
}