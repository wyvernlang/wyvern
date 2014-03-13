package wyvern.tools.tests;

import org.junit.Test;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.TypeBinding;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.TypeResolver;
import wyvern.tools.types.UnresolvedType;
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.types.extensions.Bool;
import wyvern.tools.types.extensions.Int;

import java.util.HashSet;

/**
 * Created by Ben Chung on 3/11/14.
 */
public class ResolverTests {
	@Test
	public void testSimple() throws IllegalAccessException {
		Type toResolve = new UnresolvedType("x");
		Environment ctx = Environment.getEmptyEnvironment().extend(new TypeBinding("x", Int.getInstance()));
		Type resolved = TypeResolver.resolve(toResolve, ctx, new HashSet<Type>());
		System.out.println();
	}
	@Test
	public void testArrow() throws IllegalAccessException {
		Type toResolve = new Arrow(new UnresolvedType("x"), new UnresolvedType("y"));
		Environment ctx = Environment.getEmptyEnvironment().extend(new TypeBinding("x", Int.getInstance())).extend(new TypeBinding("y", Bool.getInstance()));
		Type resolved = TypeResolver.resolve(toResolve, ctx, new HashSet<Type>());
		System.out.println();
	}
}
