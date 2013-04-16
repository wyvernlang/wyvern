package wyvern.tools.tests;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import wyvern.stdlib.Globals;
import wyvern.tools.interpreter.Interpreter;
import wyvern.tools.parsing.BodyParser;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.simpleParser.Phase1Parser;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Bool;
import wyvern.tools.types.extensions.Int;
import wyvern.tools.types.extensions.Str;

public class ExtensionsTest {
	@Rule
    public ExpectedException thrown= ExpectedException.none();

	@Test
	public void testSimpleBooleans() {
		Reader reader = new StringReader("val b:Bool = true && true && true\n"
										+"b || false && true");
		RawAST parsedResult = Phase1Parser.parse("Test", reader);		
		Assert.assertEquals("{$I {$L val b : Bool = true && true && true $L} {$L b || false && true $L} $I}", parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();
		TypedAST typedAST = parsedResult.accept(BodyParser.getInstance(), env);		
		Assert.assertEquals("[[ValDeclaration(\"b\", Invocation(Invocation(BooleanConstant(true), \"&&\", BooleanConstant(true)), \"&&\", BooleanConstant(true)))], Invocation(Variable(\"b\"), \"||\", Invocation(BooleanConstant(false), \"&&\", BooleanConstant(true)))]",
				typedAST.toString());		
		Type resultType = typedAST.typecheck(env);
		Assert.assertEquals(Bool.getInstance(), resultType);
		Value resultValue = typedAST.evaluate(env);
		Assert.assertEquals("BooleanConstant(true)", resultValue.toString());
	}
	
	@Test
	public void testRelationalOps() {
		RawAST parsedResult;
		TypedAST typedAST;
		Type resultType;
		Value resultValue;
		Environment env = Globals.getStandardEnv();
		int first=3, second=4;

		String[] ops = {">", "<", "<=", ">=", "==", "!="};
		boolean[] results = {first>second, first<second, first<=second, first>=second, first==second, first!=second};
		for (int i=0; i<ops.length; i++) {
			Reader reader = new StringReader(first + ops[i] + second);
			parsedResult = Phase1Parser.parse("Test", reader);
			Assert.assertEquals("{$I {$L "+ first + " " + ops[i] +" "+ second +" $L} $I}", parsedResult.toString());
			typedAST = parsedResult.accept(BodyParser.getInstance(), env);
			Assert.assertEquals("Invocation(IntegerConstant(" + first + "), \"" + ops[i] + "\", IntegerConstant(" + second + "))", typedAST.toString());
			resultType = typedAST.typecheck(env);
			Assert.assertEquals(Bool.getInstance(), resultType);
			resultValue = typedAST.evaluate(env);
			Assert.assertEquals("BooleanConstant("+ results[i] +")", resultValue.toString());
		}
	}
	
	@Test 
	public void testIf() {
		// Reader reader = new StringReader("if true then 5 else 10");
		// phase 1 parser
		// create TypedAST
		// do a typecheck
		// do a evaluate
	}
	
	@Test
	public void testStrings() {
		Reader reader = new StringReader("100 + \" Hello \" + \"world!\" ");
		RawAST parsedResult = Phase1Parser.parse("Test", reader);
		Assert.assertEquals("{$I {$L 100 + \" Hello \" + \"world!\" $L} $I}", parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();
		TypedAST typedAST = parsedResult.accept(BodyParser.getInstance(), env);
		Assert.assertEquals("Invocation(Invocation(IntegerConstant(100), \"+\", StringConstant(\" Hello \")), \"+\", StringConstant(\"world!\"))", typedAST.toString());
		Type resultType = typedAST.typecheck(env);
		Assert.assertEquals(Str.getInstance(), resultType);
		Value resultValue = typedAST.evaluate(env);
		Assert.assertEquals("StringConstant(\"100 Hello world!\")", resultValue.toString());
	}
	
	@Test(expected=StackOverflowError.class)
	public void testRecursiveFunction() {
		Reader reader = new StringReader("meth m(n:Int):Int = 1 + m(n)\n"
										+"m(5)");
		RawAST parsedResult = Phase1Parser.parse("Test", reader);		
		Assert.assertEquals("{$I {$L meth m (n : Int) : Int = 1 + m (n) $L} {$L m (5) $L} $I}", parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();
		TypedAST typedAST = parsedResult.accept(BodyParser.getInstance(), env);
		Assert.assertEquals("[[MethDeclaration()], Application(Variable(\"m\"), IntegerConstant(5))]", typedAST.toString());		
		Type resultType = typedAST.typecheck(env);
		Assert.assertEquals(Int.getInstance(), resultType);
		
		typedAST.evaluate(env); // will result in stack overflow
	}
	
	@Test
	public void testInterpreter() throws IOException {
		InputStream is = LexingTest.class.getClassLoader().getResource("wyvern/tools/tests/samples/arithmetic-test.wyv").openStream();
		Reader reader = new InputStreamReader(is);
		
		Value resultValue = new Interpreter().interpret(reader);
		Assert.assertEquals("IntegerConstant(10)", resultValue.toString());
	}
}
