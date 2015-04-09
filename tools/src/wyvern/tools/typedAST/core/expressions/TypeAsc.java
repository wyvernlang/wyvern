package wyvern.tools.typedAST.core.expressions;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractTypedAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.TypeResolver;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.TreeWriter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Ben Chung on 3/13/14.
 */
public class TypeAsc extends AbstractTypedAST {

	private final TypedAST exn;
	private Type should;

	public TypeAsc(TypedAST exn, Type should) {

		this.exn = exn;
		this.should = should;
	}

	@Override
	public Type getType() {
		return should;
	}

	@Override
	public Type typecheck(Environment env, Optional<Type> expected) {
		should = TypeResolver.resolve(should, env);
		if (!(exn.typecheck(env, Optional.empty()).subtype(should)))
			throw new RuntimeException();
		return should;
	}

	@Override
	public Value evaluate(EvaluationEnvironment env) {
		return exn.evaluate(env);
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		Map<String, TypedAST> output = new HashMap<>();
		output.put("exn", exn);
		return output;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		return new TypeAsc(newChildren.get("exn"), should);
	}

	@Override
	public FileLocation getLocation() {
		return null;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(exn, should);
	}
}
