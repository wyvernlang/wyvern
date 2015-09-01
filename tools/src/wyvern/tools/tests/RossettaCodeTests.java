package wyvern.tools.tests;

import static java.util.Optional.empty;
import static wyvern.stdlib.Globals.getStandardEnv;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Optional;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import wyvern.stdlib.Globals;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.parsing.coreparser.TokenManager;
import wyvern.tools.parsing.coreparser.WyvernParser;
import wyvern.tools.parsing.coreparser.WyvernTokenManager;
import wyvern.tools.tests.suites.RegressionTests;
import wyvern.tools.tests.tagTests.TestUtil;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Int;

@Category(RegressionTests.class)
public class RossettaCodeTests {
    @BeforeClass public static void setupResolver() {
    	TestUtil.setPaths();
		WyvernResolver.getInstance().addPath(PATH);
    }

	@Test
	public void testHello() throws ParseException {
		String program = TestUtil.readFile(PATH + "hello.wyv");
		
		/*
		String input = "requires stdout\n\n"				// or stdio, or io, or stdres, but these are not least privilege
				     + "stdout.print(\"Hello, World\")\n";
		String altInput = "requires ffi\n"					// more explicit version; but resources can be available w/o explicit ffi
	                    + "instantiate stdout(ffi)\n\n"
			            + "stdout.print(\"Hello, World\")\n";
		// a standard bag of resources is a map from type to resource module impl
		String stdoutInput = "module stdout.java : stdout\n\n"	// the java version
				           + "requires ffi.java as ffi\n\n"		// this is the version for the Java platform; others linked in transparently
	                       + "instantiate java(ffi)\n\n"		// a particular FFI that needs the FFI meta-permission
	                       + "import java:java.lang.System\n\n"
                           + "java.lang.System.out\n";			// result of the stdout module - of course could also be wrapped
		String stdoutSig = "type stdout\n"						// type sig for stdout; part of std prelude, so this is always in scope
                         + "    def print(String text):void";
        */
		TypedAST ast = TestUtil.getNewAST(program);
		//Type resultType = ast.typecheck(Globals.getStandardEnv(), Optional.<Type>empty());
		TestUtil.evaluateNew(ast);
		//Assert.assertEquals(resultType, new Int());
		//Value out = testAST.evaluate(Globals.getStandardEvalEnv());
		//int finalRes = ((IntegerConstant)out).getValue();
		//Assert.assertEquals(3, finalRes);
	}
	
	private static final String BASE_PATH = TestUtil.BASE_PATH;
	private static final String PATH = BASE_PATH + "rosetta/";
	private static final String OLD_PATH = BASE_PATH + "rosetta-old/";
	
	@Test
	/**
	 * This test ensures that hello world works with the old parser
	 */
	public void testOldHello() throws CopperParserException, IOException {
		String program = TestUtil.readFile(OLD_PATH + "helloOld.wyv");
		TypedAST ast = TestUtil.getAST(program);
		
		TestUtil.evaluate(ast);
	}
}
