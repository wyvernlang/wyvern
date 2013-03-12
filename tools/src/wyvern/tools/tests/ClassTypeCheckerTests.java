package wyvern.tools.tests;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.Scanner;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.internal.runners.statements.Fail;

import wyvern.stdlib.Globals;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.CoreParser;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.simpleParser.Phase1Parser;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.Declaration;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
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
		Scanner s = new Scanner(new File(url.getFile()));
		String parsedTestFileAsString = s.nextLine();
		s.close();
		
		RawAST parsedResult = Phase1Parser.parse(reader);
		Assert.assertEquals(parsedTestFileAsString, parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();

		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		Assert.assertEquals("TypeDeclaration()", typedAST.toString());		

		// FIXME: Type checking Declarations is different!!!
		if (typedAST instanceof Declaration) {
			((Declaration) typedAST).typecheckAll(env);
		} else {
			Type resultType = typedAST.typecheck(env);
			Assert.assertEquals(Unit.getInstance(), resultType);
		}
		
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
				+"        val result = 42\n"
				+"        result\n"
				);
		RawAST parsedResult = Phase1Parser.parse(reader);
		Assert.assertEquals("{$I {$L type Stack {$I {$L prop top : Int ? $L} {$L meth push (element : Int) $L} {$L meth pop () : Int ? $L} $I} $L} {$L type StackFactory {$I {$L meth Stack () : Stack $L} {$L meth StackWithFirst (firstElement : Int) : Stack $L} $I} $L} {$L class StackImpl {$I {$L implements Stack $L} {$L var top : Int ? $L} {$L meth push (element : Int) {$I {$L print (\"test\") $L} $I} $L} {$L meth pop () : Int ? {$I {$L val result = 42 $L} {$L result $L} $I} $L} $I} $L} $I}",
				parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();

		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		Assert.assertEquals("TypeDeclaration()", typedAST.toString());		

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
				+"    meth pop()\n"
				+"        val result = 42\n"
				+"        result\n"
				);
		RawAST parsedResult = Phase1Parser.parse(reader);
		Assert.assertEquals("{$I {$L type Stack {$I {$L prop top : Int ? $L} {$L meth push (element : Int) $L} {$L meth pop () : Int ? $L} $I} $L} {$L type StackFactory {$I {$L meth Stack () : Stack $L} {$L meth StackWithFirst (firstElement : Int) : Stack $L} $I} $L} {$L class StackImpl {$I {$L implements Stack $L} {$L var top : Int ? $L} {$L meth pop () {$I {$L val result = 42 $L} {$L result $L} $I} $L} $I} $L} $I}",
				parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();

		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		Assert.assertEquals("TypeDeclaration()", typedAST.toString());		

		// FIXME: Type checking Declarations is different!!!
		if (typedAST instanceof Declaration) {
			try {
				((Declaration) typedAST).typecheckAll(env);
			} catch (ToolError e) {
				Assert.assertEquals("wyvern.tools.errors.ToolError: StackImpl is not a subtype of Stack on line number 11", e.toString());
				return;
			}
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
		RawAST parsedResult = Phase1Parser.parse(reader);
		Assert.assertEquals("{$I {$L type Stack {$I {$L prop top : Int ? $L} {$L meth push (element : Int) $L} {$L meth pop () : Int ? $L} $I} $L} {$L type StackFactory {$I {$L meth Stack () : Stack $L} {$L meth StackWithFirst (firstElement : Int) : Stack $L} $I} $L} {$L class StackImpl {$I {$L implements StackBar $L} {$L var top : Int ? $L} $I} $L} $I}",
				parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();

		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		Assert.assertEquals("TypeDeclaration()", typedAST.toString());		

		// FIXME: Type checking Declarations is different!!!
		if (typedAST instanceof Declaration) {
			try {
				((Declaration) typedAST).typecheckAll(env);
			} catch (ToolError e) {
				Assert.assertEquals("wyvern.tools.errors.ToolError: Type StackBar has no declaration in the context on line number 11", e.toString());
				return;
			}
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
				+"class StackImpl\n"
				+"    class implements StackFactory\n"
				+"    var top : Int?\n"
				+"\n"
				+"    class meth Stack() : Stack = new\n"
				+"\n"
				+"    class meth StackWithFirst(firstElement : Int) : Stack\n"
				+"        new\n"
				+"            list = Link(firstElement, null)\n"
				);
		RawAST parsedResult = Phase1Parser.parse(reader);
		Assert.assertEquals("{$I {$L type Stack {$I {$L prop top : Int ? $L} {$L meth push (element : Int) $L} {$L meth pop () : Int ? $L} $I} $L} {$L type StackFactory {$I {$L meth Stack () : Stack $L} {$L meth StackWithFirst (firstElement : Int) : Stack $L} $I} $L} {$L class StackImpl {$I {$L class implements StackFactory $L} {$L var top : Int ? $L} {$L class meth Stack () : Stack = new $L} {$L class meth StackWithFirst (firstElement : Int) : Stack {$I {$L new {$I {$L list = Link (firstElement , null) $L} $I} $L} $I} $L} $I} $L} $I}",
				parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();

		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		Assert.assertEquals("TypeDeclaration()", typedAST.toString());		

		// FIXME: Type checking Declarations is different!!!
		if (typedAST instanceof Declaration) {
			((Declaration) typedAST).typecheckAll(env);
		} else {
			Type resultType = typedAST.typecheck(env);
			Assert.assertEquals(Unit.getInstance(), resultType);
		}
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
				+"    class meth Stack() = new\n"
				);
		RawAST parsedResult = Phase1Parser.parse(reader);
		Assert.assertEquals("{$I {$L type Stack {$I {$L prop top : Int ? $L} {$L meth push (element : Int) $L} {$L meth pop () : Int ? $L} $I} $L} {$L type StackFactory {$I {$L meth Stack () : Stack $L} {$L meth StackWithFirst (firstElement : Int) : Stack $L} $I} $L} {$L class StackImpl {$I {$L class implements StackFactory $L} {$L var top : Int ? $L} {$L class meth Stack () = new $L} $I} $L} $I}",
				parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();

		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		Assert.assertEquals("TypeDeclaration()", typedAST.toString());		

		// FIXME: Type checking Declarations is different!!!
		if (typedAST instanceof Declaration) {
			try {
				((Declaration) typedAST).typecheckAll(env);
			} catch (ToolError e) {
				Assert.assertEquals("wyvern.tools.errors.ToolError: StackImpl is not a subtype of StackFactory on line number 11", e.toString());
				return;
			}
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
		RawAST parsedResult = Phase1Parser.parse(reader);
		Assert.assertEquals("{$I {$L type Stack {$I {$L prop top : Int ? $L} {$L meth push (element : Int) $L} {$L meth pop () : Int ? $L} $I} $L} {$L type StackFactory {$I {$L meth Stack () : Stack $L} {$L meth StackWithFirst (firstElement : Int) : Stack $L} $I} $L} {$L class StackImpl {$I {$L class implements StackFactoryFoo $L} {$L var top : Int ? $L} $I} $L} $I}",
				parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();

		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		Assert.assertEquals("TypeDeclaration()", typedAST.toString());		

		// FIXME: Type checking Declarations is different!!!
		if (typedAST instanceof Declaration) {
			try {
				((Declaration) typedAST).typecheckAll(env);
			} catch (ToolError e) {
				Assert.assertEquals("wyvern.tools.errors.ToolError: Type StackFactoryFoo has no declaration in the context on line number 11", e.toString());
				return;
			}
		}
		Assert.fail("Expected Wyvern compiler to detect error!");
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
				+"        top = element\n"
				+"\n"
				+"    meth pop() : Int?\n"
				+"        top\n"
				+"\n"
				+"meth doIt()\n"
				+"    val s = StackImpl.Stack()\n"
				+"    s.push(42)\n"
				+"    s.push(\"wrong type\")\n"
				);
		RawAST parsedResult = Phase1Parser.parse(reader);
		Assert.assertEquals("{$I {$L type Stack {$I {$L prop top : Int ? $L} {$L meth push (element : Int) $L} {$L meth pop () : Int ? $L} $I} $L} {$L class StackImpl {$I {$L implements Stack $L} {$L class meth Stack () : Stack = new $L} {$L var top : Int ? $L} {$L meth push (element : Int) {$I {$L top = element $L} $I} $L} {$L meth pop () : Int ? {$I {$L top $L} $I} $L} $I} $L} {$L meth doIt () {$I {$L val s = StackImpl . Stack () $L} {$L s . push (42) $L} {$L s . push (\"wrong type\") $L} $I} $L} $I}",
				parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();

		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		Assert.assertEquals("TypeDeclaration()", typedAST.toString());		

		// FIXME: Type checking Declarations is different!!!
		if (typedAST instanceof Declaration) {
			try {
				((Declaration) typedAST).typecheckAll(env);
			} catch (ToolError e) {
				Assert.assertEquals("wyvern.tools.errors.ToolError: Actual argument to function does not match function type on line number -1", e.toString());
				return;
			}
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
				+"        top = element\n"
				+"\n"
				+"    meth pop() : Int?\n"
				+"        top\n"
				+"\n"
				+"meth doIt()\n"
				+"    val s = StackImpl.Stack()\n"
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
		RawAST parsedResult = Phase1Parser.parse(reader);
		Assert.assertEquals("{$I {$L type Stack {$I {$L prop top : Int ? $L} {$L meth push (element : Str) $L} {$L meth pop () : Int ? $L} $I} $L} {$L class StackImpl {$I {$L implements Stack $L} {$L class meth Stack () : Stack = new $L} {$L var top : Int ? $L} {$L meth push (element : Str) {$I {$L top = element $L} $I} $L} {$L meth pop () : Int ? {$I {$L top $L} $I} $L} $I} $L} {$L meth doIt () {$I {$L val s = StackImpl . Stack () $L} {$L s . push (\"42\") $L} {$L s . push (\"42\") $L} {$L s . push (\"42\") $L} {$L s . push (\"42\") $L} {$L s . push (\"42\") $L} {$L s . push (\"42\") $L} {$L s . push (\"42\") $L} {$L s . push (\"42\") $L} {$L s . push (\"42\") $L} {$L s . push (\"42\") $L} $I} $L} $I}",
				parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();

		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		Assert.assertEquals("TypeDeclaration()", typedAST.toString());		

		// FIXME: Type checking Declarations is different!!!
		if (typedAST instanceof Declaration) {
			((Declaration) typedAST).typecheckAll(env);
		} else {
			Type resultType = typedAST.typecheck(env);
			Assert.assertEquals(Unit.getInstance(), resultType);
		}
	}
}