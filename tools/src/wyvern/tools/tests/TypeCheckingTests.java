package wyvern.tools.tests;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import wyvern.stdlib.Globals;
import wyvern.tools.parsing.Wyvern;
import wyvern.tools.typedAST.interfaces.TypedAST;

public class TypeCheckingTests {
	@Test
	public void testTypeMembers1() throws IOException, CopperParserException {
		String input =
				"module TestModule\n" +
				"type TestType\n" +
				"  type TestTypeInner\n" +
				"    def bar() : Int\n" +
				"";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		
		// System.out.println(res.typecheck(Globals.getStandardEnv(), Optional.empty()));
		
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv(), Optional.empty()).toString(), "TYPE()");
		
		// System.out.println(res.evaluate(Globals.getStandardEnv()).toString());
		
		Assert.assertEquals(res.evaluate(Globals.getStandardEnv()).toString(), "()");		
	}

	@Test
	public void testTypeMembers2() throws IOException, CopperParserException {
		String input =
				"module TestModule\n" +
				"type M\n" +
				"  type A\n" +
				"    def create() : A\n" +
				"";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		
		// System.out.println("Parsed OK.");
		
		// System.out.println("typecheck returned: " + res.typecheck(Globals.getStandardEnv(), Optional.empty()));
		
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv(), Optional.empty()).toString(), "TYPE()");
		
		// System.out.println("evaluate returned: " + res.evaluate(Globals.getStandardEnv()).toString());
		
		Assert.assertEquals(res.evaluate(Globals.getStandardEnv()).toString(), "()");		
	}

	@Test
	public void testTypeMembers3() throws IOException, CopperParserException {
		String input =
				"module TestModule\n" +
				"type M\n" +
				"  class A\n" +
				"    def create() : A = new\n" +
				"";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		
		System.out.println("Parsed OK.");
		
		System.out.println("typecheck returned: " + res.typecheck(Globals.getStandardEnv(), Optional.empty()));
		
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv(), Optional.empty()).toString(), "TYPE()");
		
		System.out.println("evaluate returned: " + res.evaluate(Globals.getStandardEnv()).toString());
		
		Assert.assertEquals(res.evaluate(Globals.getStandardEnv()).toString(), "()");		
	}

	@Test
	public void testTypeMembers4() throws IOException, CopperParserException {
		String input =
			    "type M\n" +
				"  class A\n" +
				"    def foo() : Int\n" +
				"    def create() : A = new\n" +
				"\n" +
				"def f(i : Int) : M = ~\n" +
				"  new\n" +
				"    class A\n" +
				"      def foo() = i\n" +
				"    def create():A = new\n" +
				"\n" +
				"val m1:M = f(1)\n" + // "new, 1st" instance of M with class A
				"val m2:M = f(2)\n" + // "new, 2nd" instance of M with class A
				"\n" +
				"val m1a : m1.A = m1.A.create()\n" +
				"printInteger(m1a.foo())\n" + // 1
				"\n" +
				"val m2a : m2.A = m2.A.create()\n" +
				"printInteger(m1a.foo())\n" + // 2
				"";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		
		System.out.println("Parsed OK.");
		
		// System.out.println("typecheck returned: " + res.typecheck(Globals.getStandardEnv(), Optional.empty()));
		
		// Assert.assertEquals(res.typecheck(Globals.getStandardEnv(), Optional.empty()).toString(), "TYPE()");
		
		// System.out.println("evaluate returned: " + res.evaluate(Globals.getStandardEnv()).toString());
		
		// Assert.assertEquals(res.evaluate(Globals.getStandardEnv()).toString(), "()");		
	}
}

