package wyvern.tools.typedAST.binding;

import wyvern.tools.typedAST.Keyword;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.Value;
import wyvern.tools.types.Environment;

public class KeywordNameBinding extends AbstractBinding implements NameBinding {
	private Keyword keyword;
	
	public KeywordNameBinding(String name, Keyword keyword) {
		super(name, keyword.getType());
		this.keyword = keyword;
	}

	public TypedAST getUse() {
		return keyword;
	}

	@Override
	public Value getValue(Environment env) {
		return keyword;
	}
}