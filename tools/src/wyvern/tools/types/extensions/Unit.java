package wyvern.tools.types.extensions;

import java.util.HashSet;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.types.AbstractTypeImpl;
import wyvern.tools.types.Type;
import wyvern.tools.types.TypeUtils;
import wyvern.tools.util.TreeWriter;

public class Unit extends AbstractTypeImpl {
	private Unit() { }
	private static Unit instance = new Unit();
	public static Unit getInstance() { return instance; }
	
	@Override
	public void writeArgsToTree(TreeWriter writer) {
		// nothing to write		
	}
	
	@Override
	public String toString() {
		return "Unit";
	}

	@Override
	public boolean subtype(Type other, HashSet<TypeUtils.SubtypeRelation> subtypes) {
		return super.subtype(other, subtypes);
	}
}