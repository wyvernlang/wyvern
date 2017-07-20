package wyvern.tools.types.extensions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.WyvernException;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.types.AbstractTypeImpl;
import wyvern.tools.types.SubtypeRelation;
import wyvern.tools.types.Type;

public class Effect extends AbstractTypeImpl {
	public Effect() {  }
	
	@Override
	public String toString() {
		return "Effect";
	}

	@Override
	public boolean subtype(Type other, HashSet<SubtypeRelation> subtypes) {
		return other instanceof Effect; // ok for now?
	}

	@Override
	public Map<String, Type> getChildren() {
		return new HashMap<>();
	}

	@Override
	public Type cloneWithChildren(Map<String, Type> newChildren) {
		return this;
	}

	@Override
	public Type cloneWithBinding(TypeBinding binding) {
		return new Effect(); //??
	}

    @Override
    @Deprecated
    public ValueType generateILType() {
        throw new WyvernException("Primitive type conversion unimplmented"); //TODO
    }


    @Override
	public boolean equals(Object other) { return other instanceof Effect; }

	@Override
	public ValueType getILType(GenContext ctx) {
		return wyvern.target.corewyvernIL.support.Util.effectType();
	}
}
