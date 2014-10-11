package wyvern.tools.tests.tagTests;

import static wyvern.tools.tests.tagTests.TestUtil.getAST;

import java.io.IOException;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import wyvern.stdlib.Globals;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.interfaces.TypedAST;
import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;

public class TypeCheckMatch {

	private static final String PATH = "src/wyvern/tools/tests/tagTests/code/typecheck/match/";
	
	@Test
	public void matchOverTypeType() throws CopperParserException, IOException {
		String input = readFile("matchOverTypeType.wyv");
				
		TypedAST res = getAST(input);
		
		typeCheckfailWith(res, ErrorMessage.MATCH_OVER_TYPETYPE);
	}
	
	@Test
	public void duplicateCaseTestFail() throws CopperParserException, IOException {
		String input = readFile("duplicateCaseFail.wyv");
				
		TypedAST res = getAST(input);
		
		typeCheckfailWith(res, ErrorMessage.DUPLICATE_TAG);
	}
	
	@Test
	public void unknownCaseTest() throws CopperParserException, IOException {
		String input = 	readFile("unknownCase.wyv");
				
		TypedAST res = getAST(input);
		
		typeCheckfailWith(res, ErrorMessage.TYPE_NOT_DECLARED);
	}
	
	@Test
	public void untaggedCaseTest() throws CopperParserException, IOException {
		String input = 	readFile("untaggedCase.wyv");
				
		TypedAST ast = getAST(input);
		
		typeCheckfailWith(ast, ErrorMessage.TYPE_NOT_TAGGED);
	}
	
	@Test
	public void defaultNotLastTest() throws CopperParserException, IOException {
		String input = 	readFile("defaultNotLast.wyv");
				
		TypedAST res = getAST(input);
		
		typeCheckfailWith(res, ErrorMessage.DEFAULT_NOT_LAST);
	}
	
	@Test
	public void multipleDefaultsTest() throws CopperParserException, IOException {
		String input = 	readFile("multipleDefaults.wyv");
				
		TypedAST res = getAST(input);
		
		typeCheckfailWith(res, ErrorMessage.MULTIPLE_DEFAULTS);
	}
	
	@Test
	public void nonExhaustiveErrorTest() throws CopperParserException, IOException {
		String input = readFile("nonExhaustiveError.wyv");
		
		TypedAST res = getAST(input);
	
		typeCheckfailWith(res, ErrorMessage.DEFAULT_NOT_PRESENT);
	}
	
	@Test
	public void exhaustiveWithDefaultTest() throws CopperParserException, IOException {
		String input = readFile("exhaustiveWithDefault.wyv"); 
				
		TypedAST ast = getAST(input);
		typeCheckfailWith(ast, ErrorMessage.DEFAULT_PRESENT);
	}
	
	@Test
	public void defaultPresentFullComprisesTest() throws CopperParserException, IOException {
		//Checks that an error is caught when a default is included but all comprises tags are included
		String input = readFile("defaultPresentFullComprises.wyv");

		TypedAST res = getAST(input);
		
		typeCheckfailWith(res, ErrorMessage.DEFAULT_PRESENT);
	}
	
	@Test
	public void subtagAfterSupertagTest1() throws CopperParserException, IOException {
		String input = readFile("subtagAfterSupertag1.wyv");
		
		TypedAST ast = getAST(input);
		
		typeCheckfailWith(ast, ErrorMessage.SUPERTAG_PRECEEDS_SUBTAG);
	}
	
	@Test
	public void subtagAfterSupertagTest2() throws CopperParserException, IOException {
		String input = readFile("subtagAfterSupertag2.wyv");
		
		TypedAST ast = getAST(input);
		
		typeCheckfailWith(ast, ErrorMessage.SUPERTAG_PRECEEDS_SUBTAG);
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
			//toolError.printStackTrace();
			Assert.assertEquals(errorMessage, toolError.getTypecheckingErrorMessage());
			
			return;
		}
		
		Assert.fail("Should have failed with error: " + errorMessage);
	}
	
	/**
	 * Type checks the AST, ensuring it does so succesfully.
	 * 
	 * @param ast
	 */
	private static void typeCheckSucceed(TypedAST ast) {
		try {
			ast.typecheck(Globals.getStandardEnv(), Optional.empty());
		} catch (ToolError toolError) {
			Assert.fail("Should have succeeded");
		}
	}
	/**
	 * Helper method to simplify reading a Wyvern file from the code/parse directory.
	 * 
	 * @param filename
	 * @return
	 */
	private static String readFile(String filename) {
		return TestUtil.readFile(PATH + filename);
	}
}
