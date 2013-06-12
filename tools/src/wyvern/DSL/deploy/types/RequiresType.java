package wyvern.DSL.deploy.types;

import wyvern.DSL.deploy.typedAST.architecture.properties.RequiresProperty;
import wyvern.tools.types.SubtypeRelation;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

import java.util.HashSet;

public class RequiresType implements Type {
	private RequiresProperty base;

	public RequiresType(RequiresProperty base) {

		this.base = base;
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
