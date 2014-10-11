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

public class TypeCheckTagTests {

	private static final String PATH = "src/wyvern/tools/tests/tagTests/code/typecheck/tagged/";
	
	@Test
	public void taggedTypeSingle() throws CopperParserException, IOException {
		String input = readFile("taggedTypeSingle.wyv");
		
		TypedAST res = getAST(input);
		typeCheckSucceed(res);
	}
	
	@Test
	public void taggedTypeMulti1() throws CopperParserException, IOException {
		String input = readFile("taggedTypeMulti1.wyv");
		
		TypedAST res = getAST(input);
		typeCheckSucceed(res);
	}
	
	@Test
	public void taggedTypeMulti2() throws CopperParserException, IOException {
		String input = readFile("taggedTypeMulti2.wyv");
		
		TypedAST res = getAST(input);
		typeCheckSucceed(res);
	}

	@Test
	public void taggedTypeMulti3() throws CopperParserException, IOException {
		String input = readFile("taggedTypeMulti3.wyv");
		
		TypedAST res = getAST(input);
		typeCheckSucceed(res);
	}

	@Test
	public void taggedTypeCaseOf1() throws CopperParserException, IOException {
		String input = readFile("taggedTypeCaseOf1.wyv");
		
		TypedAST res = getAST(input);
		typeCheckSucceed(res);
	}
	
	@Test
	public void taggedTypeCaseOf2() throws CopperParserException, IOException {
		String input = readFile("taggedTypeCaseOf2.wyv");
		
		TypedAST res = getAST(input);
		typeCheckSucceed(res);
	}
	
	@Test
	public void taggedTypeCaseOf3() throws CopperParserException, IOException {
		String input = readFile("taggedTypeCaseOf3.wyv");
		
		TypedAST res = getAST(input);
		typeCheckSucceed(res);
	}

	@Test
	public void taggedTypeCaseOfNotTagged1() throws CopperParserException, IOException {
		String input = readFile("taggedTypeCaseOfNotTagged1.wyv");
		
		TypedAST res = getAST(input);
		typeCheckfailWith(res, ErrorMessage.TYPE_NOT_TAGGED);
	}

	@Test
	public void taggedTypeCaseOfNotTagged2() throws CopperParserException, IOException {
		String input = readFile("taggedTypeCaseOfNotTagged2.wyv");
		
		TypedAST res = getAST(input);
		typeCheckfailWith(res, ErrorMessage.TYPE_NOT_TAGGED);
	}

	@Test
	public void taggedTypeUnknownCaseOf1() throws CopperParserException, IOException {
		String input = readFile("taggedTypeUnknownCaseOf1.wyv");
		
		TypedAST res = getAST(input);
		typeCheckfailWith(res, ErrorMessage.TYPE_NOT_TAGGED);
	}

	@Test
	public void taggedTypeUnknownCaseOf2() throws CopperParserException, IOException {
		String input = readFile("taggedTypeUnknownCaseOf2.wyv");
		
		TypedAST res = getAST(input);
		typeCheckfailWith(res, ErrorMessage.TYPE_NOT_TAGGED);
	}

	@Test
	public void taggedTypeCaseOfCircular1() throws CopperParserException, IOException {	
		String input = readFile("taggedTypeCircular1.wyv");
		
		TypedAST res = getAST(input);
		typeCheckfailWith(res, ErrorMessage.CIRCULAR_TAGGED_RELATION);
	}

	@Test
	public void taggedTypeCaseOfCircular2() throws CopperParserException, IOException {
		String input = readFile("taggedTypeCircular2.wyv");
		
		TypedAST res = getAST(input);
		typeCheckfailWith(res, ErrorMessage.CIRCULAR_TAGGED_RELATION);
	}
	
	@Test
	public void taggedTypeCaseOfCircular3() throws CopperParserException, IOException {	
		String input = readFile("taggedTypeCircular3.wyv");
		
		TypedAST res = getAST(input);
		typeCheckfailWith(res, ErrorMessage.CIRCULAR_TAGGED_RELATION);
	}
	
	@Test
	public void taggedTypeComprises() throws CopperParserException, IOException {
		String input = readFile("taggedTypeComprises.wyv");
		
		TypedAST res = getAST(input);
		typeCheckSucceed(res);
	}
	
	@Test
	public void taggedTypeComprisesJSON() throws CopperParserException, IOException {
		String input = readFile("taggedTypeComprisesJSON.wyv");
		
		TypedAST res = getAST(input);
		typeCheckSucceed(res);
	}
	
	@Test
	public void taggedTypeComprisesAnimals() throws CopperParserException, IOException {
		String input = readFile("taggedTypeComprisesAnimals.wyv");
		
		TypedAST res = getAST(input);
		typeCheckSucceed(res);
	}
	
	@Test
	public void taggedTypeComprisesProduce() throws CopperParserException, IOException {
		String input = readFile("taggedTypeComprisesProduce.wyv");
		
		TypedAST res = getAST(input);
		typeCheckSucceed(res);
	}
	
	@Test
	public void taggedTypeComprisesNotReciprocated1() throws CopperParserException, IOException {
		String input = readFile("taggedTypeComprisesNonReciprocated1.wyv");
		
		TypedAST res = getAST(input);
		typeCheckfailWith(res, ErrorMessage.COMPRISES_RELATION_NOT_RECIPROCATED);
	}
	
	@Test
	public void taggedTypeComprisesNotReciprocated2() throws CopperParserException, IOException {
		//attempts to create a circular tagged hierarchy
		
		String input = readFile("taggedTypeComprisesNonReciprocated2.wyv");
		
		TypedAST res = getAST(input);
		typeCheckfailWith(res, ErrorMessage.COMPRISES_RELATION_NOT_RECIPROCATED);
	}
	
	@Test
	public void taggedTypeComprisesExcludes1() throws CopperParserException, IOException {
		//attempts to create a circular tagged hierarchy
		
		String input = readFile("taggedTypeComprisesExcludes1.wyv");
		
		TypedAST res = getAST(input);
		typeCheckfailWith(res, ErrorMessage.COMPRISES_EXCLUDES_TAG);
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
			//toolError.printStackTrace();
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
