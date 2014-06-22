package wyvern.tools.types;

import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.extensions.TypeInv;

import java.lang.reflect.Field;
import java.util.*;

//Sigh...
public class TypeResolver {
	public interface Resolvable {
		public Map<String, Type> getTypes();
		public Type setTypes(Map<String, Type> newTypes);
	}

	public static Type resolve(Type input, Environment ctx) {
		try {
			return resolve(input, ctx, new HashSet<Type>());
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private static WeakHashMap<Type, Type> resolved = new WeakHashMap<>();
	public static Type resolve(Type input, Environment ctx, HashSet<Type> visited) throws IllegalAccessException {
		// System.out.println("Resolving... " + input);
		
		if (resolved.containsKey(input)) {
			return resolved.get(input);
		}
		Type result = iresolve(input, ctx, visited);
		resolved.put(input, result);
		return result;
	}


	private static Type iresolve(Type input, Environment ctx, HashSet<Type> visited) throws IllegalAccessException {
		// System.out.println("Resolving: " + input + " of class " + input.getClass());
		
		if (input instanceof UnresolvedType)
			return ((UnresolvedType) input).resolve(ctx);

		if (input instanceof Resolvable) {
			Map<String, Type> toResolve = ((Resolvable) input).getTypes();
			toResolve.replaceAll((key, type) -> {
				try {
					visited.add(type);
					return resolve(type, ctx, visited);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			});
			return ((Resolvable) input).setTypes(toResolve);
		}


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

		if (input instanceof TypeInv) { // This might be a variable!
			return ((TypeInv)input).resolve(ctx);
		}
		
		return input;
	}
}
