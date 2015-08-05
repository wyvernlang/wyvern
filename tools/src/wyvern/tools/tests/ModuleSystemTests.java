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
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.parsing.coreparser.TokenManager;
import wyvern.tools.parsing.coreparser.WyvernParser;
import wyvern.tools.parsing.coreparser.WyvernTokenManager;
import wyvern.tools.tests.suites.RegressionTests;
import wyvern.tools.tests.tagTests.TestUtil;
import wyvern.tools.typedAST.core.binding.evaluation.EvaluationBinding;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Int;

@Category(RegressionTests.class)
public class ModuleSystemTests {
	
	private static final String BASE_PATH = TestUtil.BASE_PATH;
	private static final String PATH = BASE_PATH + "shiyqw/";
	
    @BeforeClass public static void setupResolver() {
    	TestUtil.setPaths();
		WyvernResolver.getInstance().addPath(PATH);
    }

	@Test
	public void testResource() throws ParseException {
		String program = TestUtil.readFile(PATH + "testModule.wyv");
		TypedAST ast = TestUtil.getNewAST(program);
		WyvernResolver.getInstance().setNewParser(true);
		ast.typecheck(Globals.getStandardEnv(), Optional.empty());
		ast.evaluate(Globals.getStandardEvalEnv());
	}
    
	@Test
	public void testImport() throws ParseException {
		String program = TestUtil.readFile(PATH + "import.wyv");
		TypedAST ast = TestUtil.getNewAST(program);
		WyvernResolver.getInstance().setNewParser(true);
		typeCheckfailWith(ast, ErrorMessage.MODULE_TYPE_ERROR);
	}
	
	@Test
	public void testRequire() throws ParseException {
		String program = TestUtil.readFile(PATH + "require.wyv");
		TypedAST ast = TestUtil.getNewAST(program);
		WyvernResolver.getInstance().setNewParser(true);
		ast.typecheck(Globals.getStandardEnv(), Optional.empty());
		ast.evaluate(Globals.getStandardEvalEnv());
	}
	
	@Test
	public void testRsType() throws ParseException {
		String program = TestUtil.readFile(PATH + "rsType.wyv");
		TypedAST ast = TestUtil.getNewAST(program);
		WyvernResolver.getInstance().setNewParser(true);
		ast.typecheck(Globals.getStandardEnv(), Optional.empty());
		ast.evaluate(Globals.getStandardEvalEnv());
	}
	
	@Test
	public void testInst() throws ParseException {
		String program = TestUtil.readFile(PATH + "inst.wyv");
		TypedAST ast = TestUtil.getNewAST(program);
	}
	/**
	 * Attempts to typecheck the given AST and catch the given ErrorMessage.
	 * This error being thrown indicates the test passed.
	 * 
	 * If the error isn't thrown, the test fails.
	 * 
	 * @param ast
	 * @param errorMessage
	 */
	private static void typeCheckfailWith(TypedAST ast, ErrorMessage errorMessage) {
		try {
			ast.typecheck(Globals.getStandardEnv(), Optional.empty());
		} catch (ToolError toolError) {
			// toolError.printStackTrace(); // FIXME:
			System.out.println(errorMessage);
			Assert.assertEquals(errorMessage, toolError.getTypecheckingErrorMessage());
			
			return;
		}
		
		Assert.fail("Should have failed with error: " + errorMessage);
	}
}
