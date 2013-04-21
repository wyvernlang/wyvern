package wyvern.DSL.html.typedAST;

import java.util.HashMap;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.LineSequenceParser;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class AttrAST implements TypedAST {
	
	private HashMap<String, TypedAST> attrs;
	private FileLocation loc;

	public AttrAST(HashMap<String,TypedAST> attrs, FileLocation loc) {
		this.attrs = attrs;
		this.loc = loc;
	}
	
	public HashMap<String,TypedAST> getAttrs() {
		return attrs;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		throw new RuntimeException();
	}

	@Override
	public FileLocation getLocation() {
		return this.loc;
	}

	@Override
	public Type getType() {
		throw new RuntimeException();
	}

	@Override
	public Type typecheck(Environment env) {
		throw new RuntimeException();
	}

	@Override
	public Value evaluate(Environment env) {
		throw new RuntimeException();
	}

	@Override
	public LineParser getLineParser() {
		throw new RuntimeException();
	}

	@Override
	public LineSequenceParser getLineSequenceParser() {
		return null;
	}

}
