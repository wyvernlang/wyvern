package wyvern.tools.types;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.util.TreeWriter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class UnresolvedType implements Type {
	private String typeName;

	public UnresolvedType(String typeName) {
		this.typeName = typeName;
	}
	
	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(typeName);
	}
	
	public Type resolve(Environment env) {
		if (env.lookupType(typeName) == null)
			throw new RuntimeException("Cannot find "+typeName +" in environment "+env);
		return env.lookupType(typeName).getUse();
	}
	
	@Override
	public String toString() {
		return "UNRESOLVED: " + typeName;
	}

	@Override
	public boolean subtype(Type other, HashSet<SubtypeRelation> subtypes) {
		ToolError.reportError(ErrorMessage.NOT_SUBTYPE,
				HasLocation.UNKNOWN, this.toString(), other.toString());
		return false; // Unreachable.
	}
	
	@Override
	public boolean subtype(Type other) {
		return this.subtype(other, new HashSet<SubtypeRelation>());
	}

    @Override
	public boolean isSimple() {
		return true;
	}

	@Override
	public Map<String, Type> getChildren() {
		return new HashMap<>();
	}

	@Override
	public Type cloneWithChildren(Map<String, Type> newChildren) {
		throw new RuntimeException("Cannot specify a ref type");
	}
}