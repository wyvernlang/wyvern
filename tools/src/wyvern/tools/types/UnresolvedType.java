package wyvern.tools.types;

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

}
