package wyvern.tools.tests;

import org.junit.Assert;
import org.junit.Test;
import wyvern.DSL.DSL;
import wyvern.tools.typedAST.core.Invocation;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.transformers.TypedAST.AbstractASTTransformer;
import wyvern.tools.typedAST.transformers.Types.AbstractTypeTransformer;
import wyvern.tools.types.Environment;
import wyvern.tools.types.OperatableType;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.types.extensions.Int;
import wyvern.tools.types.extensions.Str;
import wyvern.tools.util.Reference;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Optional;

/**
 * Created by Ben Chung on 1/11/14.
 */
public class TransformerTests {
	private static class IdentityTransformer extends AbstractASTTransformer {
		@Override
		public TypedAST transform(TypedAST input) {
			return defaultTransformation(input);
		}
	}

	private TypedAST compile(String src) {
		ArrayList<String> strs = new ArrayList<>();
		strs.add(src);
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs, new ArrayList<DSL>());
		wyvern.stdlib.Compiler.flush();
		pair.typecheck(Environment.getEmptyEnvironment(), Optional.empty());
		return pair;
	}

	@Test
	public void testIdentity1() {
		TypedAST ast = compile("val x : Int = 2\nx");
		TypedAST transformed = new IdentityTransformer().transform(ast);
		assert(transformed.evaluate(Environment.getEmptyEnvironment()).equals(new IntegerConstant(2)));
	}

	@Test
	public void testIdentity2() {
		TypedAST ast = compile("class K\n\tval x = 2\n\tclass def create():K\n\t\tnew\nK.create().x");
		TypedAST transformed = new IdentityTransformer().transform(ast);
		assert(transformed.evaluate(Environment.getEmptyEnvironment()).equals(new IntegerConstant(2)));
	}

	private static class SimpleReplacer extends AbstractASTTransformer {
		private final TypedAST replace;
		private final TypedAST with;

		public SimpleReplacer(TypedAST replace, TypedAST with) {
			this.replace = replace;
			this.with = with;
		}

		@Override
		public TypedAST transform(TypedAST input) {
			if (input != null && input.equals(replace))
				return with;
			else
				return defaultTransformation(input);
		}
	}

	@Test
	public void testRep1() {
		TypedAST ast = compile("val x : Int = 2\nx");
		TypedAST transformed = new SimpleReplacer(new IntegerConstant(2), new IntegerConstant(4)).transform(ast);
		Assert.assertEquals(transformed.evaluate(Environment.getEmptyEnvironment()), (new IntegerConstant(4)));
	}

	@Test
	public void testRep2() {
		TypedAST ast = compile("class K\n\tval x = 2\n\tclass def create():K\n\t\tnew\nK.create().x");
		TypedAST transformed = new SimpleReplacer(new IntegerConstant(2), new IntegerConstant(4)).transform(ast);
		Assert.assertEquals(transformed.evaluate(Environment.getEmptyEnvironment()), (new IntegerConstant(4)));
	}

	@Test
	public void testTTTransform() {
		ClassType ct = new ClassType(new Reference<>(Environment.getEmptyEnvironment().extend(new NameBindingImpl("x", Int.getInstance()))),
				new Reference<Environment>(null), new LinkedList<String>());
		Type result = new AbstractTypeTransformer(){
			@Override
			public Type transform(Type type) {
				if (type instanceof Int) {
					return Str.getInstance();
				}
				return defaultTransformation(type);
			}
		}.transform(ct);
		Assert.assertEquals(((OperatableType)result).checkOperator(new Invocation(null, "x", null, null), Environment.getEmptyEnvironment()), Str.getInstance());
	}
}
