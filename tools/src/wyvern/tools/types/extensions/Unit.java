package wyvern.tools.types.extensions;

import wyvern.tools.types.AbstractTypeImpl;
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

}
