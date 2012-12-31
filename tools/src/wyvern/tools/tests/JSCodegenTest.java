package wyvern.tools.tests;

import java.io.Reader;
import java.io.StringReader;

import junit.framework.Assert;

import org.junit.Test;

import wyvern.stdlib.Globals;
import wyvern.tools.parsing.CoreParser;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.simpleParser.Phase1Parser;
import wyvern.tools.typedAST.CoreAST;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.Value;
import wyvern.tools.typedAST.visitors.JSCodegenVisitor;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Int;

public class JSCodegenTest {
	
	private TypedAST doCompile(String input) {
		Reader reader = new StringReader(input);
		RawAST parsedResult = Phase1Parser.parse(reader);
		Environment env = Globals.getStandardEnv();
		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		Type resultType = typedAST.typecheck(env);
		return typedAST;
	}
	
	@Test
	public void testValDecl() {
		TypedAST typedAST = doCompile("val x = 5\nx");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		Assert.assertEquals("var x = 5;\nx", visitor.getCode());
	}
	
	@Test
	public void testArithmetic() {
		TypedAST typedAST = doCompile("3*4+5*6");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		Assert.assertEquals("3 * 4 + 5 * 6", visitor.getCode());
	}
	
	@Test
	public void testLambdaCall() {
		TypedAST typedAST = doCompile("(fn x : Int => x)(1)");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		Assert.assertEquals("(function(x) { return x; })(1)", visitor.getCode());
	}
	
	@Test
	public void testLambdaCallAdd() {
		TypedAST typedAST = doCompile("(fn x : Int => x + 1)(1)");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		Assert.assertEquals("(function(x) { return x + 1; })(1)", visitor.getCode());
	}
	
	
	@Test
	public void testHigherOrder() {
		TypedAST typedAST = doCompile("fn f : Int -> Int => fn x : Int => f(f(x))");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		Assert.assertEquals("function(f) { return function(x) { return (f)((f)(x)); }; }", visitor.getCode());
	}
	
	@Test
	public void testHigherOrderApplication() {
		TypedAST typedAST = doCompile("val applyTwice = fn f : Int -> Int => fn x : Int => f(f(x))\n"
				+"val addOne = fn x : Int => x + 1\n"
				+"applyTwice(addOne)(1)");
		JSCodegenVisitor visitor = new JSCodegenVisitor();
		((CoreAST)typedAST).accept(visitor);
		Assert.assertEquals("var applyTwice = function(f) { return function(x) { return (f)((f)(x)); }; };\n"+
							"var addOne = function(x) { return x + 1; };\n"+
							"((applyTwice)(addOne))(1)", visitor.getCode());
	}
}
