package wyvern.tools.tests;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;

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
import wyvern.tools.types.extensions.TypeType;
import wyvern.tools.types.extensions.Unit;

public class ClassTypeCheckerTests {
	@Test
	public void testClassDeclaration() throws IOException {
		String testFileName;
		URL url;
		
		testFileName = "wyvern/tools/tests/samples/SimpleClass.wyv";
		url = ClassTypeCheckerTests.class.getClassLoader().getResource(testFileName);
		if (url == null) {
			Assert.fail("Unable to open " + testFileName + " file.");
			return;
		}
		InputStream is = url.openStream();
		Reader reader = new InputStreamReader(is);

		testFileName = "wyvern/tools/tests/samples/parsedSimpleClass.prsd";
		url = ClassTypeCheckerTests.class.getClassLoader().getResource(testFileName);
		Scanner s = new Scanner(url.openStream());
		String parsedTestFileAsString = s.nextLine();
		s.close();
		
		RawAST parsedResult = Phase1Parser.parse("Test", reader);
		Assert.assertEquals(parsedTestFileAsString, parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();

		TypedAST typedAST = parsedResult.accept(BodyParser.getInstance(), env);
		Assert.assertEquals("[[MutableTypeDeclaration(), MutableTypeDeclaration(), MutableTypeDeclaration(), MutableClassDeclaration(), MutableClassDeclaration(), MethDeclaration()]]", typedAST.toString());		

		typedAST.typecheck(env);
		
		//Value resultValue = typedAST.evaluate(env);
		//Assert.assertEquals("()", resultValue.toString());
	}
	
	@Test
	public void testImplements() {
		Reader reader = new StringReader("\n"
				+"type Stack\n"
				+"    prop top : Int?\n"
				+"    meth push(element : Int)\n"
				+"    meth pop() : Int?\n"
				+"\n"
				+"type StackFactory\n"
				+"    meth Stack() : Stack\n"
				+"    meth StackWithFirst(firstElement : Int) : Stack\n"
				+"\n"
				+"class StackImpl\n"
				+"    implements Stack\n"
				+"    var top : Int?\n"
				+"\n"
				+"    meth push(element : Int)\n"
				+"        print(\"test\")\n"
				+"\n"
				+"    meth pop() : Int?\n"
				+"        val result : Int = 42\n"
				+"        result\n"
				);
		RawAST parsedResult = Phase1Parser.parse("Test", reader);
		Assert.assertEquals("{$I {$L type Stack {$I {$L prop top : Int ? $L} {$L meth push (element : Int) $L} {$L meth pop () : Int ? $L} $I} $L} {$L type StackFactory {$I {$L meth Stack () : Stack $L} {$L meth StackWithFirst (firstElement : Int) : Stack $L} $I} $L} {$L class StackImpl {$I {$L implements Stack $L} {$L var top : Int ? $L} {$L meth push (element : Int) {$I {$L print (\"test\") $L} $I} $L} {$L meth pop () : Int ? {$I {$L val result : Int = 42 $L} {$L result $L} $I} $L} $I} $L} $I}",
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
	public void testImplementsFail() {
		Reader reader = new StringReader("\n"
				+"type Stack\n"
				+"    prop top : Int?\n"
				+"    meth push(element : Int)\n"
				+"    meth pop() : Int?\n"
				+"\n"
				+"type StackFactory\n"
				+"    meth Stack() : Stack\n"
				+"    meth StackWithFirst(firstElement : Int) : Stack\n"
				+"\n"
				+"class StackImpl\n"
				+"    implements Stack\n"
				+"    var top : Int?\n"
				+"\n"
				+"    meth pop() : Int\n"
				+"        val result:Int = 42\n"
				+"        result\n"
				);
		RawAST parsedResult = Phase1Parser.parse("Test", reader);
		Assert.assertEquals("{$I {$L type Stack {$I {$L prop top : Int ? $L} {$L meth push (element : Int) $L} {$L meth pop () : Int ? $L} $I} $L} {$L type StackFactory {$I {$L meth Stack () : Stack $L} {$L meth StackWithFirst (firstElement : Int) : Stack $L} $I} $L} {$L class StackImpl {$I {$L implements Stack $L} {$L var top : Int ? $L} {$L meth pop () : Int {$I {$L val result : Int = 42 $L} {$L result $L} $I} $L} $I} $L} $I}",
				parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();

		TypedAST typedAST = parsedResult.accept(BodyParser.getInstance(), env);
		Assert.assertEquals("[[MutableTypeDeclaration(), MutableTypeDeclaration(), MutableClassDeclaration()]]", typedAST.toString());		

		try {
			typedAST.typecheck(env);
		} catch (ToolError e) {
			Assert.assertEquals("wyvern.tools.errors.ToolError: StackImpl is not a subtype of Stack on line number Test:11,15", e.toString());
			return;
		}
		
		Assert.fail("Expected Wyvern compiler to detect error!");
	}
	
	@Test
	public void testImplementsFailByName() {
		Reader reader = new StringReader("\n"
				+"type Stack\n"
				+"    prop top : Int?\n"
				+"    meth push(element : Int)\n"
				+"    meth pop() : Int?\n"
				+"\n"
				+"type StackFactory\n"
				+"    meth Stack() : Stack\n"
				+"    meth StackWithFirst(firstElement : Int) : Stack\n"
				+"\n"
				+"class StackImpl\n"
				+"    implements StackBar\n"
				+"    var top : Int?\n"
				);
		RawAST parsedResult = Phase1Parser.parse("Test", reader);
		Assert.assertEquals("{$I {$L type Stack {$I {$L prop top : Int ? $L} {$L meth push (element : Int) $L} {$L meth pop () : Int ? $L} $I} $L} {$L type StackFactory {$I {$L meth Stack () : Stack $L} {$L meth StackWithFirst (firstElement : Int) : Stack $L} $I} $L} {$L class StackImpl {$I {$L implements StackBar $L} {$L var top : Int ? $L} $I} $L} $I}",
				parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();

		TypedAST typedAST = parsedResult.accept(BodyParser.getInstance(), env);
		Assert.assertEquals("[[MutableTypeDeclaration(), MutableTypeDeclaration(), MutableClassDeclaration()]]", typedAST.toString());		

		try {
			typedAST.typecheck(env);
		} catch (ToolError e) {
			Assert.assertEquals("wyvern.tools.errors.ToolError: Type StackBar has no declaration in the context on line number Test:11,15", e.toString());
			return;
		}
		Assert.fail("Expected Wyvern compiler to detect error!");
	}
	
	@Test
	public void testClassImplements() {
		Reader reader = new StringReader("\n"
				+"type Stack\n"
				+"    prop top : Int?\n"
				+"    meth push(element : Int)\n"
				+"    meth pop() : Int?\n"
				+"\n"
				+"type StackFactory\n"
				+"    meth Stack() : Stack\n"
				+"    meth StackWithFirst(firstElement : Int) : Stack\n"
				+"\n"
				+"class Link\n"
				+"    val data : Int\n"
				+"    val next : Link?\n"
				+"\n"
				+"    class meth Link(d:Int, n:Link?) : Link\n"
				+"        new\n"
				+"            data=d\n"
				+"            next=n\n"
				+"\n"
				+"class StackImpl\n"
				+"    implements Stack\n"
				+"    class implements StackFactory\n"
				+"    var top : Int?\n"
				+"    var list : Link?\n"
				+"\n"
				+"    class meth Stack() : Stack = new\n"
				+"\n"
				+"    class meth StackWithFirst(firstElement : Int) : Stack\n"
				+"        new\n"
				+"            list = Link.Link(firstElement, null)\n" +
				"    meth push(el : Int) : Unit =\n" +
				"        this.list = Link.Link(el, (this.list))\n" +
				"    meth pop() : Int =\n" +
				"        val old : Link = this.list\n" +
				"        this.list = this.list.next\n" +
				"        old.data"
				);
		RawAST parsedResult = Phase1Parser.parse("Test", reader);
		Assert.assertEquals("{$I {$L type Stack {$I {$L prop top : Int ? $L} {$L meth push (element : Int) $L} {$L meth pop () : Int ? $L} $I} $L} {$L type StackFactory {$I {$L meth Stack () : Stack $L} {$L meth StackWithFirst (firstElement : Int) : Stack $L} $I} $L} {$L class Link {$I {$L val data : Int $L} {$L val next : Link ? $L} {$L class meth Link (d : Int , n : Link ?) : Link {$I {$L new {$I {$L data = d $L} {$L next = n $L} $I} $L} $I} $L} $I} $L} {$L class StackImpl {$I {$L implements Stack $L} {$L class implements StackFactory $L} {$L var top : Int ? $L} {$L var list : Link ? $L} {$L class meth Stack () : Stack = new $L} {$L class meth StackWithFirst (firstElement : Int) : Stack {$I {$L new {$I {$L list = Link . Link (firstElement , null) $L} $I} $L} $I} $L} {$L meth push (el : Int) : Unit = {$I {$L this . list = Link . Link (el , (this . list)) $L} $I} $L} {$L meth pop () : Int = {$I {$L val old : Link = this . list $L} {$L this . list = this . list . next $L} {$L old . data $L} $I} $L} $I} $L} $I}",
				parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();

		TypedAST typedAST = parsedResult.accept(BodyParser.getInstance(), env);
		Assert.assertEquals("[[MutableTypeDeclaration(), MutableTypeDeclaration(), MutableClassDeclaration(), MutableClassDeclaration()]]", typedAST.toString());		

		Type resultType = typedAST.typecheck(env);
		Assert.assertEquals(Unit.getInstance(), resultType);
	}
	
	@Test
	public void testClassImplementsFail() {
		Reader reader = new StringReader("\n"
				+"type Stack\n"
				+"    prop top : Int?\n"
				+"    meth push(element : Int)\n"
				+"    meth pop() : Int?\n"
				+"\n"
				+"type StackFactory\n"
				+"    meth Stack() : Stack\n"
				+"    meth StackWithFirst(firstElement : Int) : Stack\n"
				+"\n"
				+"class StackImpl\n"
				+"    class implements StackFactory\n"
				+"    var top : Int?\n"
				+"\n"
				+"    class meth Stack() : StackImpl = new\n"
				);
		RawAST parsedResult = Phase1Parser.parse("Test", reader);
		Assert.assertEquals("{$I {$L type Stack {$I {$L prop top : Int ? $L} {$L meth push (element : Int) $L} {$L meth pop () : Int ? $L} $I} $L} {$L type StackFactory {$I {$L meth Stack () : Stack $L} {$L meth StackWithFirst (firstElement : Int) : Stack $L} $I} $L} {$L class StackImpl {$I {$L class implements StackFactory $L} {$L var top : Int ? $L} {$L class meth Stack () : StackImpl = new $L} $I} $L} $I}",
				parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();

		TypedAST typedAST = parsedResult.accept(BodyParser.getInstance(), env);
		Assert.assertEquals("[[MutableTypeDeclaration(), MutableTypeDeclaration(), MutableClassDeclaration()]]", typedAST.toString());		

		try {
			typedAST.typecheck(env);
		} catch (ToolError e) {
			Assert.assertEquals("wyvern.tools.errors.ToolError: StackImpl is not a subtype of StackFactory on line number Test:11,15", e.toString());
			return;
		}
		Assert.fail("Expected Wyvern compiler to detect error!");
	}
	
	@Test
	public void testClassImplementsFailByName() {
		Reader reader = new StringReader("\n"
				+"type Stack\n"
				+"    prop top : Int?\n"
				+"    meth push(element : Int)\n"
				+"    meth pop() : Int?\n"
				+"\n"
				+"type StackFactory\n"
				+"    meth Stack() : Stack\n"
				+"    meth StackWithFirst(firstElement : Int) : Stack\n"
				+"\n"
				+"class StackImpl\n"
				+"    class implements StackFactoryFoo\n"
				+"    var top : Int?\n"
				);
		RawAST parsedResult = Phase1Parser.parse("Test", reader);
		Assert.assertEquals("{$I {$L type Stack {$I {$L prop top : Int ? $L} {$L meth push (element : Int) $L} {$L meth pop () : Int ? $L} $I} $L} {$L type StackFactory {$I {$L meth Stack () : Stack $L} {$L meth StackWithFirst (firstElement : Int) : Stack $L} $I} $L} {$L class StackImpl {$I {$L class implements StackFactoryFoo $L} {$L var top : Int ? $L} $I} $L} $I}",
				parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();

		TypedAST typedAST = parsedResult.accept(BodyParser.getInstance(), env);
		Assert.assertEquals("[[MutableTypeDeclaration(), MutableTypeDeclaration(), MutableClassDeclaration()]]", typedAST.toString());		

		
		try {
			typedAST.typecheck(env);
		} catch (ToolError e) {
			Assert.assertEquals("wyvern.tools.errors.ToolError: Type StackFactoryFoo has no declaration in the context on line number Test:11,15", e.toString());
			return;
		}
	}
	
	@Test
	public void testFunctionApplicationTypeCheck() {
		Reader reader = new StringReader("\n"
				+"type Stack\n"
				+"    prop top : Int?\n"
				+"    meth push(element : Int)\n"
				+"    meth pop() : Int?\n"
				+"\n"
				+"class StackImpl\n"
				+"    implements Stack\n"
				+"\n"
				+"    class meth Stack() : Stack = new\n"
				+"\n"
				+"    var top : Int?\n"
				+"\n"
				+"    meth push(element : Int)\n"
				+"        this.top = element\n"
				+"\n"
				+"    meth pop() : Int?\n"
				+"        this.top\n"
				+"\n"
				+"meth doIt()\n"
				+"    val s : Stack = StackImpl.Stack()\n"
				+"    s.push(42)\n"
				+"    s.push(\"wrong type\")\n"
				);
		RawAST parsedResult = Phase1Parser.parse("Test", reader);
		Assert.assertEquals("{$I {$L type Stack {$I {$L prop top : Int ? $L} {$L meth push (element : Int) $L} {$L meth pop () : Int ? $L} $I} $L} {$L class StackImpl {$I {$L implements Stack $L} {$L class meth Stack () : Stack = new $L} {$L var top : Int ? $L} {$L meth push (element : Int) {$I {$L this . top = element $L} $I} $L} {$L meth pop () : Int ? {$I {$L this . top $L} $I} $L} $I} $L} {$L meth doIt () {$I {$L val s : Stack = StackImpl . Stack () $L} {$L s . push (42) $L} {$L s . push (\"wrong type\") $L} $I} $L} $I}",
				parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();

		TypedAST typedAST = parsedResult.accept(BodyParser.getInstance(), env);
		Assert.assertEquals("[[MutableTypeDeclaration(), MutableClassDeclaration(), MethDeclaration()]]", typedAST.toString());		

		try {
			typedAST.typecheck(env);
		} catch (ToolError e) {
			Assert.assertEquals("wyvern.tools.errors.ToolError: Actual argument type Str does not match formal argument type Int on line number Unknown:-1,-1", e.toString());
			return;
		}
		Assert.fail("Expected Wyvern compiler to detect error!");
	}
	
	@Test
	public void testMultilineMethods() {
		Reader reader = new StringReader("\n"
				+"type Stack\n"
				+"    prop top : Int?\n"
				+"    meth push(element : Str)\n"
				+"    meth pop() : Int?\n"
				+"\n"
				+"class StackImpl\n"
				+"    implements Stack\n"
				+"\n"
				+"    class meth Stack() : Stack = new\n"
				+"\n"
				+"    var top : Int?\n"
				+"\n"
				+"    meth push(element : Str)\n"
				+"        this.top = element\n"
				+"\n"
				+"    meth pop() : Int?\n"
				+"        this.top\n"
				+"\n"
				+"meth doIt()\n"
				+"    val s:Stack = StackImpl.Stack()\n"
				+"    s.push(\"42\")\n"
				+"    s.push(\"42\")\n"
				+"    s.push(\"42\")\n"
				+"    s.push(\"42\")\n"
				+"    s.push(\"42\")\n"
				+"    s.push(\"42\")\n"
				+"    s.push(\"42\")\n"
				+"    s.push(\"42\")\n"
				+"    s.push(\"42\")\n"
				+"    s.push(\"42\")\n"
				);
		RawAST parsedResult = Phase1Parser.parse("Test", reader);
		Assert.assertEquals("{$I {$L type Stack {$I {$L prop top : Int ? $L} {$L meth push (element : Str) $L} {$L meth pop () : Int ? $L} $I} $L} {$L class StackImpl {$I {$L implements Stack $L} {$L class meth Stack () : Stack = new $L} {$L var top : Int ? $L} {$L meth push (element : Str) {$I {$L this . top = element $L} $I} $L} {$L meth pop () : Int ? {$I {$L this . top $L} $I} $L} $I} $L} {$L meth doIt () {$I {$L val s : Stack = StackImpl . Stack () $L} {$L s . push (\"42\") $L} {$L s . push (\"42\") $L} {$L s . push (\"42\") $L} {$L s . push (\"42\") $L} {$L s . push (\"42\") $L} {$L s . push (\"42\") $L} {$L s . push (\"42\") $L} {$L s . push (\"42\") $L} {$L s . push (\"42\") $L} {$L s . push (\"42\") $L} $I} $L} $I}",
				parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();

		TypedAST typedAST = parsedResult.accept(BodyParser.getInstance(), env);
		Assert.assertEquals("[[MutableTypeDeclaration(), MutableClassDeclaration(), MethDeclaration()]]", typedAST.toString());		

		typedAST.typecheck(env);
	}
	
	@Test
	public void testNameConflict1() {
		Reader reader = new StringReader("\n"
				+"type A\n"
				+"    prop a : Int\n"
				+"    meth b() : Int\n"
				+"\n"
				+"type B\n"
				+"    meth a() : Int\n"
				+"    prop b : Int\n"
				+"\n"
				+"class AImpl\n"
				+"    implements A\n"
				+"\n"
				+"    class meth make() : A\n"
				+"        new\n"
				+"\n"
				+"    var a : Int\n"
				+"\n"
				+"    meth b() : Int\n"
				+"        this.a\n"
				+"\n"
				+"meth doIt() : Unit\n"
//				+"    val a1:A = AImpl.make()\n"
//				+"    val a2:B = AImpl.make()\n"
//				+"    val checkMe1:Int = a1.a\n"
//				+"    val checkMe2:Unit -> Int = a2.a\n"
//				+"    val checkMe3:Int = a2.a()\n"
				
				// What Would You Do?
//				+"    val a3:B = a1\n"
//				+"    val checkMe4:Int = a1.a\n"
//				+"    val checkMe5:Unit -> Int = a3.a\n"
//				+"    val checkMe6:Unit -> Int = a1.b\n"
//				+"    val checkMe7:Int = a3.b\n"
                
				+"    null\n"
				);
		RawAST parsedResult = Phase1Parser.parse("Test", reader);
		Assert.assertEquals("{$I {$L type A {$I {$L prop a : Int $L} {$L meth b () : Int $L} $I} $L} {$L type B {$I {$L meth a () : Int $L} {$L prop b : Int $L} $I} $L} {$L class AImpl {$I {$L implements A $L} {$L class meth make () : A {$I {$L new $L} $I} $L} {$L var a : Int $L} {$L meth b () : Int {$I {$L this . a $L} $I} $L} $I} $L} {$L meth doIt () : Unit {$I {$L null $L} $I} $L} $I}",
		 		parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();

		TypedAST typedAST = parsedResult.accept(BodyParser.getInstance(), env);
		Assert.assertEquals("[[MutableTypeDeclaration(), MutableTypeDeclaration(), MutableClassDeclaration(), MethDeclaration()]]", typedAST.toString());		

		typedAST.typecheck(env);

		DeclSequence ds = (DeclSequence) ((Sequence) typedAST).iterator().next();
		
		Iterator<Declaration> i = ds.getDeclIterator().iterator();
		TypeDeclaration tA = (TypeDeclaration) i.next();
		TypeDeclaration tB = (TypeDeclaration) i.next();
		
		TypeType tAt = (TypeType) tA.getType();
		TypeType tBt = (TypeType) tB.getType();

		HashSet<SubtypeRelation> subtypes = new HashSet<SubtypeRelation>();

		Assert.assertFalse(tAt.subtype(tBt, subtypes));
		Assert.assertFalse(tBt.subtype(tAt, subtypes));
	}
	
	@Test
	public void testNameConflict2() {
		Reader reader = new StringReader("\n"
				+"type A\n"
				+"    meth a() : Int\n"
				+"    meth b() : Int\n"
				+"\n"
				+"type B\n"
				+"    meth a() : Int\n"
				+"    meth b() : Int\n"
				+"\n"
				+"class AImpl\n"
				+"    implements A\n"
				+"\n"
				+"    class meth make() : A\n"
				+"        new\n"
				+"\n"
				+"    var a : Int\n"
				+"\n"
				+"    meth b() : Int\n"
				+"        this.a\n"
				+"\n"
				+"meth doIt() : Unit\n"
				+"    val a1:A = AImpl.make()\n"
				+"    val a2:B = AImpl.make()\n"
				+"    val checkMe1:Unit -> Int = a1.a\n"
				+"    val checkMe2:Unit -> Int = a2.a\n"
				+"    val checkMe3:Int = a2.a()\n"
				
				// What Would You Do?
				+"    val a3:B = a1\n"
				+"    val checkMe4:Unit -> Int = a1.a\n"
				+"    val checkMe5:Unit -> Int = a3.a\n"
				+"    val checkMe6:Unit -> Int = a1.b\n"
				+"    val checkMe7:Unit -> Int = a3.b\n"
                
				+"    null\n"
				);
		RawAST parsedResult = Phase1Parser.parse("Test", reader);
		Assert.assertEquals("{$I {$L type A {$I {$L meth a () : Int $L} {$L meth b () : Int $L} $I} $L} {$L type B {$I {$L meth a () : Int $L} {$L meth b () : Int $L} $I} $L} {$L class AImpl {$I {$L implements A $L} {$L class meth make () : A {$I {$L new $L} $I} $L} {$L var a : Int $L} {$L meth b () : Int {$I {$L this . a $L} $I} $L} $I} $L} {$L meth doIt () : Unit {$I {$L val a1 : A = AImpl . make () $L} {$L val a2 : B = AImpl . make () $L} {$L val checkMe1 : Unit -> Int = a1 . a $L} {$L val checkMe2 : Unit -> Int = a2 . a $L} {$L val checkMe3 : Int = a2 . a () $L} {$L val a3 : B = a1 $L} {$L val checkMe4 : Unit -> Int = a1 . a $L} {$L val checkMe5 : Unit -> Int = a3 . a $L} {$L val checkMe6 : Unit -> Int = a1 . b $L} {$L val checkMe7 : Unit -> Int = a3 . b $L} {$L null $L} $I} $L} $I}",
		 		parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();

		TypedAST typedAST = parsedResult.accept(BodyParser.getInstance(), env);
		Assert.assertEquals("[[MutableTypeDeclaration(), MutableTypeDeclaration(), MutableClassDeclaration(), MethDeclaration()]]", typedAST.toString());		

		typedAST.typecheck(env);

		DeclSequence ds = (DeclSequence) ((Sequence) typedAST).iterator().next();
		
		Iterator<Declaration> i = ds.getDeclIterator().iterator();
		TypeDeclaration tA = (TypeDeclaration) i.next();
		TypeDeclaration tB = (TypeDeclaration) i.next();
		
		TypeType tAt = (TypeType) tA.getType();
		TypeType tBt = (TypeType) tB.getType();

		HashSet<SubtypeRelation> subtypes = new HashSet<SubtypeRelation>();

		Assert.assertTrue(tAt.subtype(tBt, subtypes));
		Assert.assertTrue(tBt.subtype(tAt, subtypes));
	}

	@Test
	public void testNameConflict3() {
		Reader reader = new StringReader("\n"
				+"type A\n"
				+"    meth a() : Int\n"
				+"    prop b : Unit -> Int\n"
				+"\n"
				+"type B\n"
				+"    prop a : Unit -> Int\n"
				+"    meth b() : Int\n"
				+"\n"
				+"class AImpl\n"
				+"    implements A\n"
				+"\n"
				+"    class meth make() : A\n"
				+"        new\n"
				+"\n"
				+"    meth a() : Int\n"
				+"\n"
				+"    var b : Unit -> Int\n"
				+"\n"
				+"meth doIt() : Unit\n"
				+"    val a1:A = AImpl.make()\n"
				+"    val a2:B = AImpl.make()\n"
				+"    val checkMe1:Unit -> Int = a1.a\n"
				+"    val checkMe2:Unit -> Int = a2.a\n"
				+"    val checkMe3:Int = a2.a()\n"
				
				// What Would You Do?
				+"    val a3:B = a1\n"
				+"    val checkMe4:Unit -> Int = a1.a\n"
				+"    val checkMe5:Unit -> Int = a3.a\n"
				+"    val checkMe6:Unit -> Int = a1.b\n"
				+"    val checkMe7:Unit -> Int = a3.b\n"
                
				+"    null\n"
				);
		RawAST parsedResult = Phase1Parser.parse("Test", reader);
		
		Environment env = Globals.getStandardEnv();

		TypedAST typedAST = parsedResult.accept(BodyParser.getInstance(), env);
		Assert.assertEquals("[[MutableTypeDeclaration(), MutableTypeDeclaration(), MutableClassDeclaration(), MethDeclaration()]]", typedAST.toString());		

		typedAST.typecheck(env);

		DeclSequence ds = (DeclSequence) ((Sequence) typedAST).iterator().next();
		
		Iterator<Declaration> i = ds.getDeclIterator().iterator();
		TypeDeclaration tA = (TypeDeclaration) i.next();
		TypeDeclaration tB = (TypeDeclaration) i.next();
		
		TypeType tAt = (TypeType) tA.getType();
		TypeType tBt = (TypeType) tB.getType();

		HashSet<SubtypeRelation> subtypes = new HashSet<SubtypeRelation>();

		Assert.assertTrue(tAt.subtype(tBt, subtypes));
		Assert.assertTrue(tBt.subtype(tAt, subtypes));
	}
}