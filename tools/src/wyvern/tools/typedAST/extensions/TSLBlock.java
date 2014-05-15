package wyvern.tools.typedAST.extensions;

import wyvern.stdlib.Globals;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractTypedAST;
import wyvern.tools.typedAST.core.binding.Binding;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TSLBlock extends AbstractTypedAST {
	private final TypedAST inner;

	public static class OuterEnviromentBinding implements Binding {
		private final Environment store;

		public OuterEnviromentBinding(Environment store) {
			this.store = store;
		}

		@Override
		public String getName() {
			return "oev";
		}

		@Override
		public Type getType() {
			return null;
		}

		@Override
		public void writeArgsToTree(TreeWriter writer) {

		}

		public Environment getStore() {
			return store;
		}
	}

	public TSLBlock(TypedAST inner) {
		this.inner = inner;
	}

	@Override
	public Type getType() {
		return inner.getType();
	}

	@Override
	public Type typecheck(Environment env, Optional<Type> expected) {
		return inner.typecheck(Globals.getStandardEnv().extend(new OuterEnviromentBinding(env)), expected);
	}

	@Override
	public Value evaluate(Environment env) {
		return inner.evaluate(Environment.getEmptyEnvironment().extend(new OuterEnviromentBinding(env)));
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		Map<String, TypedAST> result = new HashMap<>(1);
		result.put("inner", inner);
		return result;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		return new TSLBlock(newChildren.get("inner"));
	}

	@Override
	public FileLocation getLocation() {
		return null;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {

	}
}
