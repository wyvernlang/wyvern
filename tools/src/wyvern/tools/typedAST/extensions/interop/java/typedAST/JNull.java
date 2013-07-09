package wyvern.tools.typedAST.extensions.interop.java.typedAST;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.LineSequenceParser;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.extensions.interop.java.types.JNullType;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class JNull implements TypedAST {
	@Override
	public Type getType() {
		return new JNullType();
	}

	@Override
	public Type typecheck(Environment env) {
		return new JNullType();
	}

	@Override
	public Value evaluate(Environment env) {
		return UnitVal.getInstance(FileLocation.UNKNOWN);
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
	public FileLocation getLocation() {
		return FileLocation.UNKNOWN;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
	}
}
