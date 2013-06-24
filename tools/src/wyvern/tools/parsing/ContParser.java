package wyvern.tools.parsing;

import wyvern.tools.rawAST.LineSequence;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.Binding;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;

public interface ContParser {
	public static class EmptyWithAST implements ContParser {
		private TypedAST elem;

		public EmptyWithAST(TypedAST elem) {
			this.elem = elem;
		}
		
		@Override
		public TypedAST parse(EnvironmentResolver r) {
			return elem;
		}

	};
	public static class SimpleResolver implements EnvironmentResolver {
		private Environment env;

		public SimpleResolver(Environment env) {
			this.env = env;
		}

		@Override
		public Environment getEnv(TypedAST elem) {
			return env;
		}
	}

	public static class ExtensionResolver implements EnvironmentResolver {
		private EnvironmentResolver env;
		private Binding binding;

		public ExtensionResolver(EnvironmentResolver env, Binding binding) {
			this.env = env;
			this.binding = binding;
		}

		@Override
		public Environment getEnv(TypedAST elem) {
			return env.getEnv(elem).extend(binding);
		}
	}

	public interface EnvironmentResolver {
		public Environment getEnv(TypedAST elem);
	}

	public TypedAST parse(EnvironmentResolver r);

}