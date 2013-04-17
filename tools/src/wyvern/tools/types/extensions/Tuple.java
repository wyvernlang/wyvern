package wyvern.tools.types.extensions;

import java.util.HashSet;
import java.util.List;

import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.types.AbstractTypeImpl;
import wyvern.tools.types.SubtypeRelation;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class Tuple extends AbstractTypeImpl {
	private Type[] types;
	
	public Tuple(Type[] types) {
		this.types = types;
	}
	
	public Tuple(List<NameBinding> bindings) {
		this.types = new Type[bindings.size()];
		for (int i = 0; i < bindings.size(); i++) {
			this.types[i] = bindings.get(i).getType();
		}
	}

	Type[] getTypes() {
		return types;
	}
	
	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(types);	
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(types.length + 2);
		
		if (types.length > 1)
			builder.append(types[0].toString());
		for (int i = 1; i < types.length; i++) {
			builder.append("*" + types[i].toString());
		}
		return builder.toString();
	}
	
	@Override
	public boolean equals(Object otherT) {
		if (!(otherT instanceof Tuple))
			return false;
		
		if (((Tuple)otherT).types.length != types.length)
			return false;
		
		for (int i = 0; i < types.length; i++) {
			if (((Tuple)otherT).types[i] != types[i])
				return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		int hash = 23;
		for (Type type : types)
			hash = hash*37 + type.hashCode();
		
		return hash;
	}	

	@Override
	public boolean subtype(Type other, HashSet<SubtypeRelation> subtypes) {
		// FIXME: Implement S-RcdWidth, S-RcdDepth, and S-RcdPerm I suppose. :)
		
		return super.subtype(other, subtypes);
	}
}