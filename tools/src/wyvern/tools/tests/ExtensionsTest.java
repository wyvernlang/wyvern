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
import wyvern.tools.parsing.CoreParser;
import wyvern.tools.parsing.extensions.FnParser;
import wyvern.tools.parsing.extensions.ValParser;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.simpleParser.Phase1Parser;
import wyvern.tools.typedAST.Keyword;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.Value;
import wyvern.tools.typedAST.binding.KeywordNameBinding;
import wyvern.tools.typedAST.binding.TypeBinding;
import wyvern.tools.typedAST.binding.ValueBinding;
import wyvern.tools.typedAST.extensions.Executor;
import wyvern.tools.typedAST.extensions.ExternalFunction;
import wyvern.tools.typedAST.extensions.IntegerConstant;
import wyvern.tools.typedAST.extensions.UnitVal;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Bool;
import wyvern.tools.types.extensions.Int;
import wyvern.tools.types.extensions.Unit;

import static wyvern.tools.types.TypeUtils.*;

public class ExtensionsTest {
	@Rule
    public ExpectedException thrown= ExpectedException.none();

	@Test
	public void testSimpleBooleans() {
		Reader reader = new StringReader("val b = true && true && true\n"
										+"b || false && true");
		RawAST parsedResult = Phase1Parser.parse(reader);		
		Assert.assertEquals("{$I {$L val b = true && true && true $L} {$L b || false && true $L} $I}", parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();
		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);		
		Assert.assertEquals("ValDeclaration(\"b\", Invocation(Invocation(BooleanConstant(true), \"&&\", BooleanConstant(true)), \"&&\", BooleanConstant(true)), " +
				"Invocation(Variable(\"b\"), \"||\", Invocation(BooleanConstant(false), \"&&\", BooleanConstant(true))))",
				typedAST.toString());		
		Type resultType = typedAST.typecheck();
		Assert.assertEquals(Bool.getInstance(), resultType);
		Value resultValue = typedAST.evaluate(env);
		Assert.assertEquals("BooleanConstant(true)", resultValue.toString());
	}

	@Test(expected=StackOverflowError.class)
	public void testRecursiveMethod() {
		Reader reader = new StringReader("meth m(n:Int):Int = 1 + m(n)\n"
										+"m(5)");
		RawAST parsedResult = Phase1Parser.parse(reader);		
		Assert.assertEquals("{$I {$L meth m (n : Int) : Int = 1 + m (n) $L} {$L m (5) $L} $I}", parsedResult.toString());
		
		Environment env = Globals.getStandardEnv();
		TypedAST typedAST = parsedResult.accept(CoreParser.getInstance(), env);
		//Assert.assertEquals("ValDeclaration(\"x\", IntegerConstant(5), Variable(\"x\"))", typedAST.toString());		
		Type resultType = typedAST.typecheck();
		Assert.assertEquals(Int.getInstance(), resultType);
		Value resultValue = typedAST.evaluate(env);
	}
	
	@Test
	public void testInterpreter() throws IOException {
		InputStream is = LexingTest.class.getClassLoader().getResource("wyvern/tools/tests/samples/arithmetic-test.wyv").openStream();
		Reader reader = new InputStreamReader(is);
		
		Value resultValue = new Interpreter().interpret(reader);
		Assert.assertEquals("IntegerConstant(10)", resultValue.toString());
	}
}
