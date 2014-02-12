package wyvern.tools.typedAST.extensions;

import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.Reference;

/**
 * Created by Ben Chung on 1/26/14.
 */
public interface UnparsedType {
	Type getAsc(Environment env);

	public static CacheAsc cacheAsc(UnparsedType inp) {
		if (inp instanceof CacheAsc)
			return (CacheAsc) inp;
		final Reference<Type> cached = new Reference<>();
		return new CacheAsc(cached, inp);
	}

	static public class CacheAsc implements UnparsedType {
		private final Reference<Type> cached;
		private final UnparsedType inp;

		public CacheAsc(Reference<Type> cached, UnparsedType inp) {
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

		public Type getType() {
			return cached.get();
		}
	}
}
