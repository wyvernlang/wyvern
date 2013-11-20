package wyvern.tools.types;

import java.util.HashSet;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.util.TreeWriter;

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
    public LineParser getParser() {
        return null;
    }

    @Override
	public boolean isSimple() {
		return true;
	}
}