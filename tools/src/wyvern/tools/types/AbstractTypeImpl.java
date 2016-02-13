package wyvern.tools.types;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;

public abstract class AbstractTypeImpl implements Type {
	private final FileLocation location;
	private Optional<TypeBinding> resolved;
	
	protected AbstractTypeImpl(FileLocation location) {
		this.location = location;
	}

	protected AbstractTypeImpl() {
		this.location = null;
	}

	@Override
	public FileLocation getLocation() {
		return location;
	}

	@Override
	public void setResolvedBinding(TypeBinding resolvedBinding) {
		resolved = Optional.of(resolvedBinding);
	}

	@Override
	public Optional<TypeBinding> getResolvedBinding() {
		return resolved;
	}

	@Override
	public Type cloneWithBinding(TypeBinding binding) {
		Type inner = this.cloneWithChildren(getChildren());
		inner.setResolvedBinding(binding);
		return inner;
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