package wyvern.tools.tests;

import java.io.Reader;

import java.io.StringReader;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import wyvern.stdlib.Globals;
import wyvern.tools.parsing.CoreParser;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.simpleParser.Phase1Parser;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Bool;
import wyvern.tools.types.extensions.Str;
import wyvern.tools.types.extensions.Int;
import wyvern.tools.types.extensions.Unit;

import static wyvern.tools.types.TypeUtils.*;

public class ParsingTestPhase2 {
	@Rule
    public ExpectedException thrown= ExpectedException.none();

	@Test
	public void testValDecl() {
		Reader reader = new StringReader("val x = 5\n"
										+"x");
		RawAST parsedResult = Phase1Parser.parse(reader);		
		Assert.assertEquals("{$I {$L val x = 5 $L} {$L x $L} $I}", parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();
		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		Assert.assertEquals("ValDeclaration(\"x\", IntegerConstant(5), Variable(\"x\"))", typedAST.toString());		
		Type resultType = typedAST.typecheck();
		Assert.assertEquals(Int.getInstance(), resultType);
		Value resultValue = typedAST.evaluate(env);
		Assert.assertEquals("IntegerConstant(5)", resultValue.toString());
	}

	@Test
	public void testLambdaCall() {
		Reader reader = new StringReader("(fn x : Int => x)(1)");
		RawAST parsedResult = Phase1Parser.parse(reader);		
		Assert.assertEquals("{$I {$L (fn x : Int => x) (1) $L} $I}", parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();
		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		Assert.assertEquals("Application(Fn(NameBindingImpl(\"x\", Int()), Variable(\"x\")), IntegerConstant(1))", typedAST.toString());
		Type resultType = typedAST.typecheck();
		Assert.assertEquals(Int.getInstance(), resultType);
		Value resultValue = typedAST.evaluate(env);
		Assert.assertEquals("IntegerConstant(1)", resultValue.toString());
	}
	
	@Test
	public void testLambdaCallWithAdd() {
		Reader reader = new StringReader("(fn x : Int => x + 1)(3)");
		RawAST parsedResult = Phase1Parser.parse(reader);		
		Assert.assertEquals("{$I {$L (fn x : Int => x + 1) (3) $L} $I}", parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();
		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		Assert.assertEquals("Application(Fn(NameBindingImpl(\"x\", Int()), Invocation(Variable(\"x\"), \"+\", IntegerConstant(1))), IntegerConstant(3))", typedAST.toString());		
		Type resultType = typedAST.typecheck();
		Assert.assertEquals(Int.getInstance(), resultType);
		Value resultValue = typedAST.evaluate(env);
		Assert.assertEquals("IntegerConstant(4)", resultValue.toString());
	}
	
	@Test
	public void testArithmetic() {
		Reader reader = new StringReader("3*4+5*6");
		RawAST parsedResult = Phase1Parser.parse(reader);		
		Assert.assertEquals("{$I {$L 3 * 4 + 5 * 6 $L} $I}", parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();
		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		Assert.assertEquals("Invocation(Invocation(IntegerConstant(3), \"*\", IntegerConstant(4)), \"+\", Invocation(IntegerConstant(5), \"*\", IntegerConstant(6)))", typedAST.toString());		
		Type resultType = typedAST.typecheck();
		Assert.assertEquals(Int.getInstance(), resultType);
		Value resultValue = typedAST.evaluate(env);
		Assert.assertEquals("IntegerConstant(42)", resultValue.toString());
	}
	
	@Test
	public void testPrint() {
		Reader reader = new StringReader("print(5)");
		RawAST parsedResult = Phase1Parser.parse(reader);		
		Assert.assertEquals("{$I {$L print (5) $L} $I}", parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();
		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		Assert.assertEquals("Application(ExternalFunction(), IntegerConstant(5))", typedAST.toString());		
		Type resultType = typedAST.typecheck();
		Assert.assertEquals(Unit.getInstance(), resultType);
		Value resultValue = typedAST.evaluate(env);
		Assert.assertEquals("()", resultValue.toString());
	}
	
	@Test
	public void testHigherOrderTypes() {
		Reader reader = new StringReader("fn f : Int -> Int => fn x : Int => f(f(x))");
		RawAST parsedResult = Phase1Parser.parse(reader);		
		Assert.assertEquals("{$I {$L fn f : Int -> Int => fn x : Int => f (f (x)) $L} $I}", parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();
		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		Assert.assertEquals("Fn(NameBindingImpl(\"f\", Arrow(Int(), Int())), Fn(NameBindingImpl(\"x\", Int()), Application(Variable(\"f\"), Application(Variable(\"f\"), Variable(\"x\")))))", typedAST.toString());		
		Type resultType = typedAST.typecheck();
		Assert.assertEquals(arrow(arrow(integer, integer),arrow(integer,integer)), resultType);
		Value resultValue = typedAST.evaluate(env);
		Assert.assertEquals("Closure(Fn(NameBindingImpl(\"f\", Arrow(Int(), Int())), Fn(NameBindingImpl(\"x\", Int()), Application(Variable(\"f\"), Application(Variable(\"f\"), Variable(\"x\"))))), Environment())", resultValue.toString());
	}
	
	@Test
	public void testHigherOrderApplication() {
		Reader reader = new StringReader("val applyTwice = fn f : Int -> Int => fn x : Int => f(f(x))\n"
										+"val addOne = fn x : Int => x + 1\n"
										+"applyTwice(addOne)(1)");
		RawAST parsedResult = Phase1Parser.parse(reader);		
		
		Environment env = Globals.getStandardEnv();
		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		Assert.assertEquals("ValDeclaration(\"applyTwice\", Fn(NameBindingImpl(\"f\", Arrow(Int(), Int())), Fn(NameBindingImpl(\"x\", Int()), Application(Variable(\"f\"), Application(Variable(\"f\"), Variable(\"x\"))))), ValDeclaration(\"addOne\", Fn(NameBindingImpl(\"x\", Int()), Invocation(Variable(\"x\"), \"+\", IntegerConstant(1))), Application(Application(Variable(\"applyTwice\"), Variable(\"addOne\")), IntegerConstant(1))))", typedAST.toString());		
		Type resultType = typedAST.typecheck();
		Assert.assertEquals(integer, resultType);
		Value resultValue = typedAST.evaluate(env);
		Assert.assertEquals("IntegerConstant(3)", resultValue.toString());
	}
	
}
