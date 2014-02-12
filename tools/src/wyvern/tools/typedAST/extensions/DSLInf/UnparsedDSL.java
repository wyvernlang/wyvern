package wyvern.tools.typedAST.extensions.DSLInf;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.lex.Token;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.LineSequenceParser;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ben Chung on 2/9/14.
 */
public class UnparsedDSL implements TypedAST {
	private TypedAST insert;
	private List<Token> unparsed;

	public UnparsedDSL(TypedAST insert, List<Token> unparsed) {
		this.insert = insert;
		this.unparsed = unparsed;
	}

	@Override
	public Type getType() {
		return null;
	}

	@Override
	public Type typecheck(Environment env) {
		return null;
	}

	@Override
	public Value evaluate(Environment env) {
		return null;
	}

	@Override
	public LineParser getLineParser() {
		return null;
	}

	@Override
	public LineSequenceParser getLineSequenceParser() {
		return null;
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		HashMap<String, TypedAST> children = new HashMap<>();
		children.put("insert", insert);
		return children;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		return new UnparsedDSL(newChildren.get("insert"), unparsed);
	}

	@Override
	public FileLocation getLocation() {
		return null;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {

	}
}
