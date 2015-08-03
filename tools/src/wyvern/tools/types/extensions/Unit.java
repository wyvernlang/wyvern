package wyvern.tools.types.extensions;

import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.WyvernException;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.types.AbstractTypeImpl;
import wyvern.tools.types.SubtypeRelation;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

public class Unit extends AbstractTypeImpl {
	public Unit() {  }
	
	@Override
	public void writeArgsToTree(TreeWriter writer) {
		// nothing to write		
	}
	
	@Override
	public String toString() {
		return "Unit";
	}

	@Override
	public boolean subtype(Type other, HashSet<SubtypeRelation> subtypes) {
		return other instanceof Unit;
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
		return new Unit();
	}

    @Override
    public ValueType generateILType() {
        throw new WyvernException("Primitive type conversion unimplmented"); //TODO
    }


    @Override
	public boolean equals(Object other) { return other instanceof Unit; }

	@Override
	public ValueType getILType(GenContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}
}