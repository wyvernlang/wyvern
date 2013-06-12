package wyvern.DSL.deploy.types;

import wyvern.DSL.deploy.typedAST.architecture.properties.ViaProperty;
import wyvern.tools.types.SubtypeRelation;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

import java.util.HashSet;

public class ViaType implements Type {
	private ViaProperty host;

	public ViaType(ViaProperty host) {
		this.host = host;
	}

	@Override
	public boolean subtype(Type other, HashSet<SubtypeRelation> subtypes) {
		return false;
	}

	@Override
	public boolean subtype(Type other) {
		return false;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
	}
	@Override
	public boolean isSimple() {
		// TODO Auto-generated method stub
		return true;
	}
}
