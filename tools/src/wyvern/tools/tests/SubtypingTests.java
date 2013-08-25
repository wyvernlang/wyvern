package wyvern.tools.tests;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Iterator;

import junit.framework.Assert;

import org.junit.Test;

import wyvern.stdlib.Globals;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.BodyParser;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.simpleParser.Phase1Parser;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.TypeDeclaration;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.SubtypeRelation;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.types.extensions.TypeType;
import wyvern.tools.types.extensions.Unit;

public class SubtypingTests {

	@Test
	public void testSimple() {
		Reader reader = new StringReader("\n"
				+"type Foo\n"
				+"    prop p : Int\n"
				+"    def m1(arg : Int)\n"
				+"    def m2() : Int\n"
				+"\n"
				+"type Bar\n"
				+"    def Foo1() : Foo\n"
				+"    def Foo2(arg : Int) : Foo\n"
				+"\n"
				+"class SomeClass\n"
				+"    implements Foo\n"
				+"\n"
				+"    var p : Int\n"
				+"\n"
				+"    def m1(arg : Int)\n"
				+"        this.m1(arg)\n"
				+"\n"
				+"    def m2() : Int\n"
				+"        42\n"
				+"\n"
				+"    def m3() : Foo\n"
				+"        new\n"
				+"\n"
				+"    def m4(a : Int) : Foo\n"
				+"        new\n"
				);
		RawAST parsedResult = Phase1Parser.parse("Test", reader);
		Assert.assertEquals("{$I {$L type Foo {$I {$L prop p : Int $L} {$L def m1 (arg : Int) $L} {$L def m2 () : Int $L} $I} $L} {$L type Bar {$I {$L def Foo1 () : Foo $L} {$L def Foo2 (arg : Int) : Foo $L} $I} $L} {$L class SomeClass {$I {$L implements Foo $L} {$L var p : Int $L} {$L def m1 (arg : Int) {$I {$L this . m1 (arg) $L} $I} $L} {$L def m2 () : Int {$I {$L 42 $L} $I} $L} {$L def m3 () : Foo {$I {$L new $L} $I} $L} {$L def m4 (a : Int) : Foo {$I {$L new $L} $I} $L} $I} $L} $I}",
				parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();

		TypedAST typedAST = parsedResult.accept(new BodyParser(), env);
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
				+"    def m1(arg : Int)\n"
				+"    def m2() : Int\n"
				+"\n"
				+"type Bar\n"
				+"    def Foo1() : Foo\n"
				+"    def Foo2(arg : Int) : Foo\n"
				+"\n"
				+"class SomeClass\n"
				+"    implements Foo\n"
				+"    class implements Bar\n"
				+"\n"
				+"    var p : Int\n"
				+"\n"
				+"    def m1(arg : Int)\n"
				+"        this.m1(arg)\n"
				+"\n"
				+"    def m2() : Int\n"
				+"        42\n"
				+"\n"
				+"    def m3() : Foo\n"
				+"        new\n"
				+"\n"
				+"    def m4(a : Int) : Foo\n"
				+"        new\n"
				);
		RawAST parsedResult = Phase1Parser.parse("Test", reader);
		Assert.assertEquals("{$I {$L type Foo {$I {$L prop p : Int $L} {$L def m1 (arg : Int) $L} {$L def m2 () : Int $L} $I} $L} {$L type Bar {$I {$L def Foo1 () : Foo $L} {$L def Foo2 (arg : Int) : Foo $L} $I} $L} {$L class SomeClass {$I {$L implements Foo $L} {$L class implements Bar $L} {$L var p : Int $L} {$L def m1 (arg : Int) {$I {$L this . m1 (arg) $L} $I} $L} {$L def m2 () : Int {$I {$L 42 $L} $I} $L} {$L def m3 () : Foo {$I {$L new $L} $I} $L} {$L def m4 (a : Int) : Foo {$I {$L new $L} $I} $L} $I} $L} $I}",
				parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();

		TypedAST typedAST = parsedResult.accept(new BodyParser(), env);
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
				+"    def m(arg : A)\n"
				+"\n"
				+"type B\n"
				+"    def m(arg : B)\n"
				+"\n"
				);
		RawAST parsedResult = Phase1Parser.parse("Test", reader);
		Assert.assertEquals("{$I {$L type A {$I {$L def m (arg : A) $L} $I} $L} {$L type B {$I {$L def m (arg : B) $L} $I} $L} $I}",
				parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();

		TypedAST typedAST = parsedResult.accept(new BodyParser(), env);
		Assert.assertEquals("[[MutableTypeDeclaration(), MutableTypeDeclaration()]]", typedAST.toString());		
		
		Sequence s = (Sequence) typedAST;
		s.typecheck(env);
		
		DeclSequence ds = (DeclSequence) s.iterator().next();
		
		Iterator<Declaration> i = ds.getDeclIterator().iterator();
		TypeDeclaration tA = (TypeDeclaration) i.next();
		TypeDeclaration tB = (TypeDeclaration) i.next();
		
		TypeType tAt = (TypeType) tA.getType();
		TypeType tBt = (TypeType) tB.getType();
		
		Assert.assertTrue(tAt.subtype(tBt));
		Assert.assertTrue(tBt.subtype(tAt));
	}

	@Test
	public void testRecursiveSubtype2() {
		Reader reader = new StringReader("\n"
				+"type A\n"
				+"    def m() : A\n"
				+"\n"
				+"type B\n"
				+"    def m() : B\n"
				+"\n"
				);
		RawAST parsedResult = Phase1Parser.parse("Test", reader);
		Assert.assertEquals("{$I {$L type A {$I {$L def m () : A $L} $I} $L} {$L type B {$I {$L def m () : B $L} $I} $L} $I}",
				parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();

		TypedAST typedAST = parsedResult.accept(new BodyParser(), env);
		Assert.assertEquals("[[MutableTypeDeclaration(), MutableTypeDeclaration()]]", typedAST.toString());		
		
		Sequence s = (Sequence) typedAST;
		s.typecheck(env);
		
		DeclSequence ds = (DeclSequence) s.iterator().next();
		
		Iterator<Declaration> i = ds.getDeclIterator().iterator();
		TypeDeclaration tA = (TypeDeclaration) i.next();
		TypeDeclaration tB = (TypeDeclaration) i.next();
		
		TypeType tAt = (TypeType) tA.getType();
		TypeType tBt = (TypeType) tB.getType();
		
		Assert.assertTrue(tAt.subtype(tBt));
		Assert.assertTrue(tBt.subtype(tAt));
	}

	@Test
	public void testRecursiveSubtype3() {
		Reader reader = new StringReader("\n"
				+"type A\n"
				+"    def m(arg : B)\n"
				+"\n"
				+"type B\n"
				+"    def m(arg : A)\n"
				+"\n"
				);
		RawAST parsedResult = Phase1Parser.parse("Test", reader);
		Assert.assertEquals("{$I {$L type A {$I {$L def m (arg : B) $L} $I} $L} {$L type B {$I {$L def m (arg : A) $L} $I} $L} $I}",
				parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();

		TypedAST typedAST = parsedResult.accept(new BodyParser(), env);
		Assert.assertEquals("[[MutableTypeDeclaration(), MutableTypeDeclaration()]]", typedAST.toString());		
		
		Sequence s = (Sequence) typedAST;
		s.typecheck(env);
		
		DeclSequence ds = (DeclSequence) s.iterator().next();
		
		Iterator<Declaration> i = ds.getDeclIterator().iterator();
		TypeDeclaration tA = (TypeDeclaration) i.next();
		TypeDeclaration tB = (TypeDeclaration) i.next();
		
		TypeType tAt = (TypeType) tA.getType();
		TypeType tBt = (TypeType) tB.getType();
		
		Assert.assertTrue(tAt.subtype(tBt));
		Assert.assertTrue(tBt.subtype(tAt));
	}

	@Test
	public void testRecursiveSubtype4() {
		Reader reader = new StringReader("\n"
				+"type A\n"
				+"    def m() : B\n"
				+"\n"
				+"type B\n"
				+"    def m() : A\n"
				+"\n"
				);
		RawAST parsedResult = Phase1Parser.parse("Test", reader);
		Assert.assertEquals("{$I {$L type A {$I {$L def m () : B $L} $I} $L} {$L type B {$I {$L def m () : A $L} $I} $L} $I}",
				parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();

		TypedAST typedAST = parsedResult.accept(new BodyParser(), env);
		Assert.assertEquals("[[MutableTypeDeclaration(), MutableTypeDeclaration()]]", typedAST.toString());		
		
		Sequence s = (Sequence) typedAST;
		s.typecheck(env);
		
		DeclSequence ds = (DeclSequence) s.iterator().next();
		
		Iterator<Declaration> i = ds.getDeclIterator().iterator();
		TypeDeclaration tA = (TypeDeclaration) i.next();
		TypeDeclaration tB = (TypeDeclaration) i.next();
		
		TypeType tAt = (TypeType) tA.getType();
		TypeType tBt = (TypeType) tB.getType();
		
		Assert.assertTrue(tAt.subtype(tBt));
		Assert.assertTrue(tBt.subtype(tAt));
	}

	@Test
	public void testSubtypeRelation() {
		Reader reader = new StringReader("\n"
				+"type A\n"
				+"    def m() : Bool\n"
				+"\n"
				+"type B\n"
				+"    def m() : Unit\n"
				+"\n"
				+"type C\n"
				+"    def m() : Int\n"
				+"\n"
				+"type D\n"
				+"    def m() : Str\n"
				+"\n"
				);
		RawAST parsedResult = Phase1Parser.parse("Test", reader);
		Assert.assertEquals("{$I {$L type A {$I {$L def m () : Bool $L} $I} $L} {$L type B {$I {$L def m () : Unit $L} $I} $L} {$L type C {$I {$L def m () : Int $L} $I} $L} {$L type D {$I {$L def m () : Str $L} $I} $L} $I}",
				parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();

		TypedAST typedAST = parsedResult.accept(new BodyParser(), env);
		Assert.assertEquals("[[MutableTypeDeclaration(), MutableTypeDeclaration(), MutableTypeDeclaration(), MutableTypeDeclaration()]]", typedAST.toString());		
		
		Sequence s = (Sequence) typedAST;
		s.typecheck(env);
		
		DeclSequence ds = (DeclSequence) s.iterator().next();
		
		Iterator<Declaration> i = ds.getDeclIterator().iterator();
		TypeDeclaration tA = (TypeDeclaration) i.next();
		TypeDeclaration tB = (TypeDeclaration) i.next();
		TypeDeclaration tC = (TypeDeclaration) i.next();
		TypeDeclaration tD = (TypeDeclaration) i.next();
		
		TypeType tAt = (TypeType) tA.getType();
		TypeType tBt = (TypeType) tB.getType();
		TypeType tCt = (TypeType) tC.getType();
		TypeType tDt = (TypeType) tD.getType();

		HashSet<SubtypeRelation> subtypes = new HashSet<SubtypeRelation>();

		Assert.assertTrue(tAt.subtype(tAt, subtypes)); // S-Refl
		
		SubtypeRelation sr = new SubtypeRelation(tAt, tBt);
		subtypes.add(sr);
		
		Assert.assertTrue(tAt.subtype(tBt, subtypes)); // S-Assumption

		sr = new SubtypeRelation(tBt, tCt);
		subtypes.add(sr);
		
		Assert.assertTrue(tAt.subtype(tCt, subtypes)); // S-Trans
		
		subtypes.clear();
		subtypes.add(new SubtypeRelation(tCt, tAt));
		subtypes.add(new SubtypeRelation(tBt, tDt));
		
		Arrow a1 = new Arrow(tAt, tBt);
		Arrow a2 = new Arrow(tCt, tDt);
		Assert.assertTrue(a1.subtype(a2, subtypes)); // S-Arrow
		Assert.assertFalse(a1.subtype(tAt, subtypes)); // S-Arrow
		Assert.assertFalse(a2.subtype(a1, subtypes)); // S-Arrow
		
		subtypes.clear();
	}

	@Test
	public void testSimpleMultiArg() {
		Reader reader = new StringReader("\n"
				+"type Foo\n"
				+"    prop p : Int\n"
				+"    def m1(arg1 : Int, arg2 : Int)\n"
				+"    def m2(arg1 : Int, arg2 : Int, arg3 : Int) : Int\n"
				+"\n"
				+"type Bar\n"
				+"    def Foo1() : Foo\n"
				+"    def Foo2(arg : Int) : Foo\n"
				+"\n"
				+"class SomeClass\n"
				+"    implements Foo\n"
				+"\n"
				+"    var p : Int\n"
				+"\n"
				+"    def m1(arg1 : Int, arg2 : Int)\n"
				+"        this.m1(arg1, arg2)\n"
				+"\n"
				+"    def m2(arg1 : Int, arg2 : Int, arg3 : Int) : Int\n"
				+"        42\n"
				+"\n"
				+"    def m3() : Foo\n"
				+"        new\n"
				+"\n"
				+"    def m4(a : Int) : Foo\n"
				+"        new\n"
				);
		RawAST parsedResult = Phase1Parser.parse("Test", reader);
		Environment env = Globals.getStandardEnv();

		TypedAST typedAST = parsedResult.accept(new BodyParser(), env);
		Assert.assertEquals("[[MutableTypeDeclaration(), MutableTypeDeclaration(), MutableClassDeclaration()]]", typedAST.toString());		

		// FIXME: Type checking Declarations is different!!!
		if (typedAST instanceof Declaration) {
			((Declaration) typedAST).typecheckAll(env);
		} else {
			Type resultType = typedAST.typecheck(env);
			Assert.assertEquals(Unit.getInstance(), resultType);
		}
	}
}