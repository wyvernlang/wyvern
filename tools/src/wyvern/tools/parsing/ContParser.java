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

	}

	public static class EmptyWithDecl implements RecordTypeParser {

		@Override
		public void parseTypes(EnvironmentResolver r) {
		}

		@Override
		public void parseInner(EnvironmentResolver r) {
		}

		@Override
		public TypedAST parse(EnvironmentResolver r) {
			return null;
		}
	}

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
        private final Environment iEnv;
        private EnvironmentResolver env;
		private Binding binding;

		public ExtensionResolver(EnvironmentResolver env, Binding binding) {
			this.env = env;
            this.iEnv = Environment.getEmptyEnvironment().extend(binding);
		}

        public ExtensionResolver(EnvironmentResolver Oenv, Environment env) {
            this.env = Oenv;
            this.iEnv = env;
        }

		@Override
		public Environment getEnv(TypedAST elem) {
			return env.getEnv(elem).extend(iEnv);
		}
	}

	public interface EnvironmentResolver {
		public Environment getEnv(TypedAST elem);
	}

	public TypedAST parse(EnvironmentResolver r);

}