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
		public TypedAST parse(Environment env) {
			return elem;
		}
		
	};
	
	public TypedAST parse(Environment env);

}