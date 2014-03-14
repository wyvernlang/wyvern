package wyvern.tools.types;

import java.lang.reflect.Field;
import java.util.HashSet;

//Sigh...
public class TypeResolver {
	public static Type resolve(Type input, Environment ctx) {
		try {
			return resolve(input, ctx, new HashSet<Type>());
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}


	public static Type resolve(Type input, Environment ctx, HashSet<Type> visited) throws IllegalAccessException {
		if (input instanceof UnresolvedType)
			return ((UnresolvedType) input).resolve(ctx);

		for (Field f : input.getClass().getDeclaredFields()) {
			if (!Type.class.isAssignableFrom(f.getType()))
				continue;
			f.setAccessible(true);
			Type inner = (Type)f.get(input);
			if (inner != null && !(visited.contains(inner))) {
				visited.add(inner);
				Type resolved = resolve(inner, ctx, visited);
				f.set(input, resolved);
			}
		}
		return input;
	}
}
