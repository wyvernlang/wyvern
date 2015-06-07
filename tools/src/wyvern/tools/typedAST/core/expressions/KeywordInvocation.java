package wyvern.tools.typedAST.core.expressions;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.TreeWriter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class KeywordInvocation implements TypedAST {
	private final TypedAST tgt;
	private final String id;
	private final TypedAST lit;

	public KeywordInvocation(TypedAST l, String id, TypedAST lit) {
		this.tgt = l;
		this.id = id;
		this.lit = lit;
	}

	@Override
	public Type getType() {
		return null;
	}

	@Override
	public Type typecheck(Environment env, Optional<Type> expected) {
		return null;
	}

	@Override
	public Value evaluate(EvaluationEnvironment env) {
		return null;
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		HashMap<String, TypedAST> out = new HashMap<>();
		out.put("tgt", tgt);
		return out;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		return new KeywordInvocation(newChildren.get("tgt"), id, lit);
	}

	@Override
	public FileLocation getLocation() {
		return null;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {

	}
}
