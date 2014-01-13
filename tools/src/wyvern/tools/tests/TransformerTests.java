package wyvern.tools.tests;

import junit.framework.Assert;
import org.junit.Test;
import wyvern.DSL.DSL;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.transformers.TypedAST.AbstractASTTransformer;
import wyvern.tools.types.Environment;

import java.util.ArrayList;

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
		pair.typecheck(Environment.getEmptyEnvironment());
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
		assert(transformed.evaluate(Environment.getEmptyEnvironment()).equals(new IntegerConstant(4)));
	}
}
