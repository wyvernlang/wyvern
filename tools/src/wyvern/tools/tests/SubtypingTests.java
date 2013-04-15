package wyvern.tools.tests;

import java.io.Reader;
import java.io.StringReader;

import junit.framework.Assert;

import org.junit.Test;

import wyvern.stdlib.Globals;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.BodyParser;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.simpleParser.Phase1Parser;
import wyvern.tools.typedAST.Declaration;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Unit;

public class SubtypingTests {

	@Test
	public void testSimple() {
		Reader reader = new StringReader("\n"
				+"type Foo\n"
				+"    prop p : Int\n"
				+"    meth m1(arg : Int)\n"
				+"    meth m2() : Int\n"
				+"\n"
				+"type Bar\n"
				+"    meth Foo1() : Foo\n"
				+"    meth Foo2(arg : Int) : Foo\n"
				+"\n"
				+"class SomeClass\n"
				+"    implements Foo\n"
				+"\n"
				+"    var pty : Int\n"
				+"\n"
				+"    meth m1(arg : Int)\n"
				+"        this.m1(arg)\n"
				+"\n"
				+"    meth m2() : Int\n"
				+"        42\n"
				+"\n"
				+"    meth m3() : Foo\n"
				+"        new\n"
				+"\n"
				+"    meth m4(a : Int) : Foo\n"
				+"        new\n"
				);
		RawAST parsedResult = Phase1Parser.parse("Test", reader);
		Assert.assertEquals("{$I {$L type Foo {$I {$L prop p : Int $L} {$L meth m1 (arg : Int) $L} {$L meth m2 () : Int $L} $I} $L} {$L type Bar {$I {$L meth Foo1 () : Foo $L} {$L meth Foo2 (arg : Int) : Foo $L} $I} $L} {$L class SomeClass {$I {$L implements Foo $L} {$L var pty : Int $L} {$L meth m1 (arg : Int) {$I {$L this . m1 (arg) $L} $I} $L} {$L meth m2 () : Int {$I {$L 42 $L} $I} $L} {$L meth m3 () : Foo {$I {$L new $L} $I} $L} {$L meth m4 (a : Int) : Foo {$I {$L new $L} $I} $L} $I} $L} $I}",
				parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();

		TypedAST typedAST = parsedResult.accept(BodyParser.getInstance(), env);
		Assert.assertEquals("[[MutableTypeDeclaration(), MutableTypeDeclaration(), MutableClassDeclaration()]]", typedAST.toString());		

		// FIXME: Type checking Declarations is different!!!
		if (typedAST instanceof Declaration) {
			((Declaration) typedAST).typecheckAll(env);
		} else {
			Type resultType = typedAST.typecheck(env);
			Assert.assertEquals(Unit.getInstance(), resultType);
		}
	}

	@Test
	public void testClassImplements() {
		// TODO: This should fail because "class implements" should check for class methods!
		Reader reader = new StringReader("\n"
				+"type Foo\n"
				+"    prop p : Int\n"
				+"    meth m1(arg : Int)\n"
				+"    meth m2() : Int\n"
				+"\n"
				+"type Bar\n"
				+"    meth Foo1() : Foo\n"
				+"    meth Foo2(arg : Int) : Foo\n"
				+"\n"
				+"class SomeClass\n"
				+"    implements Foo\n"
				+"    class implements Bar\n"
				+"\n"
				+"    var pty : Int\n"
				+"\n"
				+"    meth m1(arg : Int)\n"
				+"        this.m1(arg)\n"
				+"\n"
				+"    meth m2() : Int\n"
				+"        42\n"
				+"\n"
				+"    meth m3() : Foo\n"
				+"        new\n"
				+"\n"
				+"    meth m4(a : Int) : Foo\n"
				+"        new\n"
				);
		RawAST parsedResult = Phase1Parser.parse("Test", reader);
		Assert.assertEquals("{$I {$L type Foo {$I {$L prop p : Int $L} {$L meth m1 (arg : Int) $L} {$L meth m2 () : Int $L} $I} $L} {$L type Bar {$I {$L meth Foo1 () : Foo $L} {$L meth Foo2 (arg : Int) : Foo $L} $I} $L} {$L class SomeClass {$I {$L implements Foo $L} {$L class implements Bar $L} {$L var pty : Int $L} {$L meth m1 (arg : Int) {$I {$L this . m1 (arg) $L} $I} $L} {$L meth m2 () : Int {$I {$L 42 $L} $I} $L} {$L meth m3 () : Foo {$I {$L new $L} $I} $L} {$L meth m4 (a : Int) : Foo {$I {$L new $L} $I} $L} $I} $L} $I}",
				parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();

		TypedAST typedAST = parsedResult.accept(BodyParser.getInstance(), env);
		Assert.assertEquals("[[MutableTypeDeclaration(), MutableTypeDeclaration(), MutableClassDeclaration()]]", typedAST.toString());		

		try {
			// FIXME: Type checking Declarations is different!!!
			if (typedAST instanceof Declaration) {
				((Declaration) typedAST).typecheckAll(env);
			} else {
				Type resultType = typedAST.typecheck(env);
				Assert.assertEquals(Unit.getInstance(), resultType);
			}
		} catch (ToolError e) {
			Assert.assertEquals("wyvern.tools.errors.ToolError: SomeClass is not a subtype of Bar on line number Test:11,15", e.toString());
			return;
		}
		
		Assert.fail("Expected Wyvern compiler to detect error!");
	}

	@Test
	public void testRecursiveSubtype1() {
		Reader reader = new StringReader("\n"
				+"type A\n"
				+"    meth m(arg : B)\n"
				+"\n"
				+"type B\n"
				+"    meth m(arg : A)\n"
				+"\n"
				);
		RawAST parsedResult = Phase1Parser.parse("Test", reader);
		Assert.assertEquals("{$I {$L type A {$I {$L meth m (arg : B) $L} $I} $L} {$L type B {$I {$L meth m (arg : A) $L} $I} $L} $I}",
				parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();

		TypedAST typedAST = parsedResult.accept(BodyParser.getInstance(), env);
		Assert.assertEquals("[[MutableTypeDeclaration(), MutableTypeDeclaration()]]", typedAST.toString());		
		
		// TODO:
	}
}