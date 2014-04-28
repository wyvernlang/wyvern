package wyvern.tools.types;

import wyvern.tools.types.extensions.TypeInv;

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
			f.setAccessible(true);
			if (Type[].class.isAssignableFrom(f.getType())) {
				Type[] inner = (Type[])f.get(input);
				if (inner == null) continue;
				for (int i = 0; i < inner.length; i++)
					if (inner[i] != null && !(visited.contains(inner[i]))) {
						visited.add(inner[i]);
						inner[i] = resolve(inner[i], ctx, visited);
					}

			}
			if (!Type.class.isAssignableFrom(f.getType()))
				continue;
			Type inner = (Type)f.get(input);
			if (inner != null && !(visited.contains(inner))) {
				visited.add(inner);
				Type resolved = resolve(inner, ctx, visited);
				f.set(input, resolved);
			}
		}

		if (input instanceof TypeInv)
			return ((TypeInv)input).resolve();
		return input;
	}
}
