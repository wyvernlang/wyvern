package wyvern.tools.typedAST.binding;

import wyvern.tools.typedAST.Keyword;
import wyvern.tools.typedAST.TypedAST;

public class KeywordNameBinding extends AbstractBinding implements NameBinding {
	private Keyword keyword;
	
	public KeywordNameBinding(String name, Keyword keyword) {
		super(name, keyword.getType());
		this.keyword = keyword;
	}

	public TypedAST getUse() {
		return keyword;
	}
}
