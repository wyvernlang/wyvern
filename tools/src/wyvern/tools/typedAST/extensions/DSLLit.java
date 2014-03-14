package wyvern.tools.typedAST.extensions;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractTypedAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

import java.util.Map;

/**
 * Created by Ben Chung on 3/11/14.
 */
public class DSLLit extends AbstractTypedAST {
	public DSLLit(String inner) {

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
	public Map<String, TypedAST> getChildren() {
		return null;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		return null;
	}

	@Override
	public FileLocation getLocation() {
		return null;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {

	}
}
