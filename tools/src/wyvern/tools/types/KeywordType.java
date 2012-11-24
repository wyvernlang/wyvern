package wyvern.tools.types;

import wyvern.tools.util.TreeWriter;

public class KeywordType extends AbstractTypeImpl {
	private KeywordType() { }
	private static KeywordType instance = new KeywordType();
	public static KeywordType getInstance() { return instance; }

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		// nothing to write		
	}
	
	@Override
	public String toString() {
		return "Keyword";
	}

}
