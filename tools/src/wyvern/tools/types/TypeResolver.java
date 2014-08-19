package wyvern.tools.types;

import wyvern.tools.typedAST.core.binding.compiler.KeywordBinding;
import wyvern.tools.typedAST.core.binding.compiler.KeywordInnerBinding;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.core.declarations.KeywordDeclaration;
import wyvern.tools.typedAST.extensions.TSLBlock;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.extensions.MetadataWrapper;
import wyvern.tools.types.extensions.SpliceType;
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
	
	public static Type resolve(Type input, Environment ctx, HashSet<Type> visited) throws IllegalAccessException {
		Type result = iresolve(input, ctx, visited);
		return result;
	}

	private static Type iresolve(Type input, Environment ctx, HashSet<Type> visited) throws IllegalAccessException {
		
		if (input instanceof UnresolvedType)
			return ((UnresolvedType) input).resolve(ctx);

		if (input instanceof SpliceType)
			return resolve(((SpliceType) input).getInner(),
					ctx.lookupBinding("oev", TSLBlock.OuterEnviromentBinding.class).orElseThrow(RuntimeException::new)
							.getStore(), visited);

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
	
	public static Type generateKeywordWrapper(Type hostType, Environment ctx, String keyword) throws Exception {
		System.out.println("Type of host: " + hostType);
		
		KeywordBinding kwBinding = ctx.lookupBinding("keywordEnv", KeywordInnerBinding.class).get().getInnerEnv().lookupKeyword(hostType, keyword);
		if (kwBinding != null) {
			return new MetadataWrapper(kwBinding.getType(), kwBinding.getMetadata().get());
		}
		throw new RuntimeException("Keyword does not exist");
	}
	
}
