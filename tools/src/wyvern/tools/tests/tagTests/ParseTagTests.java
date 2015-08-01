package wyvern.tools.tests.tagTests;

import static wyvern.tools.tests.tagTests.TestUtil.getAST;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import wyvern.tools.tests.suites.RegressionTests;
import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;

@Category(RegressionTests.class)
public class ParseTagTests {

	private static final String PATH = "src/wyvern/tools/tests/tagTests/code/parse/";
	
	@Test
	/**
	 * Test the tagged keyword works with classes.
	 */
	public void taggedClassParseTest1() throws CopperParserException, IOException {		
		String input = readFile("taggedClass1.wyv");
		
		tryParseSucceed(input);
	}
	
	@Test
	/**
	 * Test the tagged keyword works with classes.
	 */
	public void taggedClassParseTest2() throws CopperParserException, IOException {		
		String input = readFile("taggedClass2.wyv");
		
		tryParseSucceed(input);
	}
	
	@Test
	/**
	 * Test the tagged keyword works with classes.
	 */
	public void taggedClassParseTest3() throws CopperParserException, IOException {		
		String input = readFile("taggedClass3.wyv");
		
		tryParseSucceed(input);
	}
	
	@Test
	/**
	 * Test the tagged keyword works with classes.
	 */
	public void taggedClassParseTest4() throws CopperParserException, IOException {		
		String input = readFile("taggedClass4.wyv");
		
		getAST(input);
	}
	
	@Test
	/**
	 * Test the tagged keyword works with classes.
	 */
	public void taggedClassParseTest5() throws CopperParserException, IOException {		
		String input = readFile("taggedClass5.wyv");
		
		getAST(input);
	}
	
	@Test
	/**
	 * Test a syntax error is caught with tagged classes.
	 */
	public void taggedInvalidClassParseTest1() throws CopperParserException, IOException {		
		String input = readFile("invalidTaggedClass1.wyv");
		
		tryParseFail(input);
	}
	
	@Test
	/**
	 * Test a syntax error is caught with tagged classes.
	 */
	public void taggedInvalidClassParseTest2() throws CopperParserException, IOException {		
		String input = readFile("invalidTaggedClass2.wyv");
		
		tryParseFail(input);
	}
	
	@Test
	/**
	 * Test a syntax error is caught with tagged classes.
	 */
	public void taggedInvalidClassParseTest3() throws CopperParserException, IOException {		
		String input = readFile("invalidTaggedClass3.wyv");
		
		tryParseFail(input);
	}
	
	@Test
	/**
	 * Test a syntax error is caught with tagged classes.
	 */
	public void taggedInvalidClassParseTest4() throws CopperParserException, IOException {		
		String input = readFile("invalidTaggedClass4.wyv");
		
		tryParseFail(input);
	}
	
	@Test
	/**
	 * Test a syntax error is caught with tagged classes.
	 */
	public void taggedInvalidClassParseTest5() throws CopperParserException, IOException {		
		String input = readFile("invalidTaggedClass5.wyv");
		
		tryParseFail(input);
	}
	

	
	
	@Test
	public void caseOfParseTest1() throws CopperParserException, IOException {
		String input = readFile("caseOf1.wyv");
			
		tryParseSucceed(input);
	}
	
	@Test
	public void caseOfParseTest2() throws CopperParserException, IOException {
		String input = readFile("caseOf2.wyv");
			
		tryParseSucceed(input);
	}
	
	@Test
	public void caseOfParseFailEmptyCase() throws CopperParserException, IOException {		
		String input = readFile("caseOfFailEmpty.wyv");
		
		tryParseFail(input);
	}
	
	@Test
	public void caseOfParseFailTooManyCases1() throws CopperParserException, IOException {		
		String input = readFile("caseOfFailTooManyCases1.wyv");
		
		tryParseFail(input);
	}
	
	@Test
	public void caseOfParseFailTooManyCases2() throws CopperParserException, IOException {		
		String input = readFile("caseOfFailTooManyCases2.wyv");
		
		tryParseFail(input);
	}
	
	@Test
	public void caseOfParseFailWrongBackets() throws CopperParserException, IOException {		
		String input = readFile("caseOfFailWrongBrackets.wyv");
		
		tryParseFail(input);
	}
	
	@Test
	public void caseOfParseFailWrongKeyword1() throws CopperParserException, IOException {		
		String input = readFile("caseOfFailWrongKeyword1.wyv");
		
		tryParseFail(input);
	}
	
	@Test
	public void caseOfParseFailWrongKeyword2() throws CopperParserException, IOException {		
		String input = readFile("caseOfFailWrongKeyword2.wyv");
		
		tryParseFail(input);
	}
	
	@Test
	public void comprisesParseTest1() throws CopperParserException, IOException {
		String input = readFile("comprises1Single.wyv");
		
		tryParseSucceed(input);
	}
	
	@Test
	public void comprisesParseTest2() throws CopperParserException, IOException {
		String input = readFile("comprises2Multi.wyv");
		
		tryParseSucceed(input);
	}
	
	@Test
	public void comprisesParseFailEmpty() throws CopperParserException, IOException {		
		String input = readFile("comprisesFailEmpty.wyv");
		
		tryParseFail(input);
	}
	
	@Test
	public void comprisesParseFailWrongBrackets() throws CopperParserException, IOException {		
		String input = readFile("comprisesFailWrongBrackets.wyv");
		
		tryParseFail(input);
	}
	
	@Test
	public void comprisesParseFailWrongKeyword() throws CopperParserException, IOException {		
		String input = readFile("comprisesFailWrongKeyword.wyv");
		
		tryParseFail(input);
	}
	
	@Test
	public void comprisesParseFailWrongSeperator() throws CopperParserException, IOException {		
		String input = readFile("comprisesFailWrongSeperator.wyv");
		
		tryParseFail(input);
	}
	
	@Test
	public void boundedParseTest() {
		String input = readFile("boundedHierarchy.wyv");
		
		tryParseSucceed(input);
	}
	
	@Test
	public void boundedParseTestFailComprisesCaseOfSwitched() {
		String input = readFile("boundedHierarchyFailComprisesCaseOfSwitched.wyv");
		
		tryParseFail(input);
	}
	
	
	@Test
	public void matchParseTest1() throws CopperParserException, IOException {		
		String input = readFile("matchParseTest1.wyv");
		
		tryParseSucceed(input);
	}
	
	@Test
	public void matchParseTest2() throws CopperParserException, IOException {		
		String input = readFile("matchParseTest2.wyv");
				
		tryParseSucceed(input);
	}
	
	@Test
	public void matchParseTestFailNoCases() throws CopperParserException, IOException {		
		String input = readFile("matchParseTestFailNoCases.wyv");
		
		tryParseFail(input);
	}
	
	@Test
	public void matchParseTestFailNoColon() throws CopperParserException, IOException {		
		String input = readFile("matchParseTestFailNoColon.wyv");
		
		tryParseFail(input);
	}
	
	@Test
	public void matchParseTestFailNoParens() throws CopperParserException, IOException {		
		String input = readFile("matchParseTestFailNoParens.wyv");
		
		tryParseFail(input);
	}
	
	@Test
	public void matchParseTestFailNoVar() throws CopperParserException, IOException {		
		String input = readFile("matchParseTestFailNoVar.wyv");
		
		tryParseFail(input);
	}
	
	@Test
	public void matchParseTestFailNoArrowCase() throws CopperParserException, IOException {		
		String input = readFile("matchParseTestFailNoArrowCase.wyv");
		
		tryParseFail(input);
	}
	
	@Test
	public void matchParseTestFailNoArrowDefault() throws CopperParserException, IOException {		
		String input = readFile("matchParseTestFailNoArrowDefault.wyv");
		
		tryParseFail(input);
	}
	
	@Test
	public void matchParseTestFailWrongArrow() throws CopperParserException, IOException {		
		String input = readFile("matchParseTestFailWrongArrow.wyv");
		
		tryParseFail(input);
	}
	
	@Test
	public void matchParseTestMulti1() throws CopperParserException, IOException {		
		String input = 	readFile("matchParseMulti1.wyv");
				
		tryParseSucceed(input);
	}
	
	@Test
	public void matchParseTestMulti2() throws CopperParserException, IOException {		
		String input = readFile("matchParseMulti2.wyv");
				
		tryParseSucceed(input);
	}

	/**
	 * Attempts to parse the given program.
	 * 
	 * This method causes a JUnit fail if the parse fails.
	 * 
	 * @param program the program source to parse
	 */
	public static void tryParseSucceed(String program) {
		try {
			getAST(program);
			//we passed			
		} catch (CopperParserException e) {
			Assert.fail();
		} catch (IOException e) {
			//shouldn't happen, just fail test if it does though
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	/**
	 * Attempts to fail parsing a given program.
	 * This method is intended to check there is a syntax error.
	 * 
	 * A JUnit fail happens if the parse succeeds.
	 * 
	 * @param program the program source
	 */
	public static void tryParseFail(String program) {
		try {
			getAST(program);
			Assert.fail();
		} catch (CopperParserException e) {
			//we passed
		} catch (IOException e) {
			//shouldn't happen, just fail test if it does though
			e.printStackTrace();
			Assert.fail();
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
