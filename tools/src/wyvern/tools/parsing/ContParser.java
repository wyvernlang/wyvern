package wyvern.tools.parsing;

import wyvern.tools.rawAST.LineSequence;
import wyvern.tools.typedAST.TypedAST;
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
	
	public interface EnvironmentResolver {
		public Environment getEnv(TypedAST elem);
	}
	
	public TypedAST parse(EnvironmentResolver r);

}