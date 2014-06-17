package wyvern.tools.types.extensions;

import wyvern.tools.types.RecordType;
import wyvern.tools.types.SubtypeRelation;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

import java.util.HashSet;
import java.util.Map;

public class TypeInv implements Type {
	Type innerType;
	String invName;

	public TypeInv(Type innerType, String invName) {
		this.innerType = innerType;
		this.invName = invName;
	}

	public Type resolve() {
		return ((RecordType)innerType).getInnerType(invName);
	}

	@Override
	public boolean subtype(Type other, HashSet<SubtypeRelation> subtypes) {
		throw new RuntimeException("Invalid operation (forgot to resolve)?");
	}

	@Override
	public boolean subtype(Type other) {
		throw new RuntimeException("Invalid operation (forgot to resolve)?");
	}

	@Override
	public boolean isSimple() {
		throw new RuntimeException("Invalid operation (forgot to resolve)?");
	}

	@Override
	public Map<String, Type> getChildren() {
		throw new RuntimeException("Invalid operation (forgot to resolve)?");
	}

	@Override
	public Type cloneWithChildren(Map<String, Type> newChildren) {
		throw new RuntimeException("Invalid operation (forgot to resolve)?");
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
	}
}
