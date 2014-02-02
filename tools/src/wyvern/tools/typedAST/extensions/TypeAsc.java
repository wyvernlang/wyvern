package wyvern.tools.typedAST.extensions;

import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.Reference;

/**
 * Created by Ben Chung on 1/26/14.
 */
public interface TypeAsc {
	Type getAsc(Environment env);

	public static TypeAsc cacheAsc(TypeAsc inp) {
		if (inp instanceof CacheAsc)
			return inp;
		final Reference<Type> cached = new Reference<>();
		return new CacheAsc(cached, inp);
	}

	static class CacheAsc implements TypeAsc {
		private final Reference<Type> cached;
		private final TypeAsc inp;

		public CacheAsc(Reference<Type> cached, TypeAsc inp) {
			this.cached = cached;
			this.inp = inp;
		}

		@Override
		public Type getAsc(Environment env) {
			if (cached == null) {
				cached.set(inp.getAsc(env));
			}
			return cached.get();
		}
	}
}
