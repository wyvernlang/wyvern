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
	public void boundedExhaustiveWithDefault1() throws CopperParserException, IOException {
		String input = readFile("boundedExhaustiveWithDefault1.wyv");
				
		TypedAST res = getAST(input);
		
		typeCheckfailWith(res, ErrorMessage.BOUNDED_EXHAUSTIVE_WITH_DEFAULT);
	}
	
	@Test
	public void boundedExhaustiveWithDefault2() throws CopperParserException, IOException {
		String input = readFile("boundedExhaustiveWithDefault2.wyv");
				
		TypedAST res = getAST(input);
		
		typeCheckfailWith(res, ErrorMessage.BOUNDED_EXHAUSTIVE_WITH_DEFAULT);
	}
	
	@Test
	public void boundedExhaustiveWithDefault3() throws CopperParserException, IOException {
		String input = readFile("boundedExhaustiveWithDefault3.wyv");
				
		TypedAST res = getAST(input);
		
		typeCheckfailWith(res, ErrorMessage.BOUNDED_EXHAUSTIVE_WITH_DEFAULT);
	}
	
	@Test
	public void boundedExhaustiveWithDefault4() throws CopperParserException, IOException {
		String input = readFile("boundedExhaustiveWithDefault4.wyv");
				
		TypedAST res = getAST(input);
		
		typeCheckfailWith(res, ErrorMessage.BOUNDED_EXHAUSTIVE_WITH_DEFAULT);
	}
	
	@Test
	public void boundedInexhaustiveWithoutDefault1() throws CopperParserException, IOException {
		String input = readFile("boundedInexhaustiveWithoutDefault1.wyv");
				
		TypedAST res = getAST(input);
		
		typeCheckfailWith(res, ErrorMessage.BOUNDED_INEXHAUSTIVE_WITHOUT_DEFAULT);
	}
	
	@Test
	public void boundedInexhaustiveWithoutDefault2() throws CopperParserException, IOException {
		String input = readFile("boundedInexhaustiveWithoutDefault2.wyv");
				
		TypedAST res = getAST(input);
		
		typeCheckfailWith(res, ErrorMessage.BOUNDED_INEXHAUSTIVE_WITHOUT_DEFAULT);
	}
	
	@Test
	public void boundedInexhaustiveWithoutDefault3() throws CopperParserException, IOException {
		String input = readFile("boundedInexhaustiveWithoutDefault3.wyv");
				
		TypedAST res = getAST(input);
		
		typeCheckfailWith(res, ErrorMessage.BOUNDED_INEXHAUSTIVE_WITHOUT_DEFAULT);
	}
	
	@Test
	public void defaultNotLastTest1() throws CopperParserException, IOException {
		String input = 	readFile("defaultNotLast1.wyv");
				
		TypedAST res = getAST(input);
		
		typeCheckfailWith(res, ErrorMessage.DEFAULT_NOT_LAST);
	}
	
	@Test
	public void defaultNotLastTest2() throws CopperParserException, IOException {
		String input = 	readFile("defaultNotLast2.wyv");
				
		TypedAST res = getAST(input);
		
		typeCheckfailWith(res, ErrorMessage.DEFAULT_NOT_LAST);
	}
	
	@Test
	public void duplicateCaseTestFail1() throws CopperParserException, IOException {
		String input = readFile("duplicateCase1.wyv");
				
		TypedAST res = getAST(input);
		
		typeCheckfailWith(res, ErrorMessage.DUPLICATE_TAG);
	}
	
	@Test
	public void duplicateCaseTestFail2() throws CopperParserException, IOException {
		String input = readFile("duplicateCase2.wyv");
				
		TypedAST res = getAST(input);
		
		typeCheckfailWith(res, ErrorMessage.DUPLICATE_TAG);
	}
	
	@Test
	public void duplicateCaseTestFail3() throws CopperParserException, IOException {
		String input = readFile("duplicateCase3.wyv");
				
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
	public void multipleDefaultsTest1() throws CopperParserException, IOException {
		String input = 	readFile("multipleDefaults1.wyv");
				
		TypedAST res = getAST(input);
		
		typeCheckfailWith(res, ErrorMessage.MULTIPLE_DEFAULTS);
	}
	
	@Test
	public void multipleDefaultsTest2() throws CopperParserException, IOException {
		String input = 	readFile("multipleDefaults2.wyv");
				
		TypedAST res = getAST(input);
		
		typeCheckfailWith(res, ErrorMessage.MULTIPLE_DEFAULTS);
	}
	
	@Test
	public void multipleDefaultsTest3() throws CopperParserException, IOException {
		String input = 	readFile("multipleDefaults3.wyv");
				
		TypedAST res = getAST(input);
		
		typeCheckfailWith(res, ErrorMessage.MULTIPLE_DEFAULTS);
	}
	
	@Test
	public void boundedInexhaustiveWithoutDefaultTest() throws CopperParserException, IOException {
		String input = readFile("nonExhaustiveError.wyv");
		
		TypedAST res = getAST(input);
	
		typeCheckfailWith(res, ErrorMessage.BOUNDED_INEXHAUSTIVE_WITHOUT_DEFAULT);
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
	
	@Test
	public void subtagAfterSupertagTest3() throws CopperParserException, IOException {
		String input = readFile("subtagAfterSupertag3.wyv");
		
		TypedAST ast = getAST(input);
		
		typeCheckfailWith(ast, ErrorMessage.SUPERTAG_PRECEEDS_SUBTAG);
	}
	
	@Test
	public void unmatchableCase1() throws CopperParserException, IOException {
		String input = readFile("unmatchableCase1.wyv");
		
		TypedAST ast = getAST(input);
		
		typeCheckfailWith(ast, ErrorMessage.UNMATCHABLE_CASE);
	}
	

	@Test
	public void unmatchableCase2() throws CopperParserException, IOException {
		String input = readFile("unmatchableCase2.wyv");
		
		TypedAST ast = getAST(input);
		
		typeCheckfailWith(ast, ErrorMessage.UNMATCHABLE_CASE);
	}
	

	@Test
	public void unmatchableCase3() throws CopperParserException, IOException {
		String input = readFile("unmatchableCase3.wyv");
		
		TypedAST ast = getAST(input);
		
		typeCheckfailWith(ast, ErrorMessage.UNMATCHABLE_CASE);
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
			toolError.printStackTrace();
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
