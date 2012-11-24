package wyvern.tools.typedAST;

import wyvern.tools.parsing.LineParser;
import wyvern.tools.types.Environment;
import wyvern.tools.types.KeywordType;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class Keyword extends AbstractTypedAST implements Value {
	private LineParser parser;
	
	public Keyword(LineParser parser) {
		this.parser = parser;
	}

	@Override
	public Type getType() {
		return KeywordType.getInstance();
	}

	@Override
	public LineParser getLineParser() {
		return parser;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		// nothing to write; we don't expect to roundtrip this type
	}

	@Override
	public Type typecheck() {
		return getType();
	}

	@Override
	public Value evaluate(Environment env) {
		return this;
	}

}
