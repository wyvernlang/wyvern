package wyvern.tools.tests.tagTests;

import java.io.IOException;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import wyvern.tools.tests.suites.RegressionTests;
import wyvern.tools.typedAST.interfaces.TypedAST;
import static wyvern.tools.tests.tagTests.TestUtil.getAST;
import static wyvern.tools.tests.tagTests.TestUtil.evaluateExpecting;

@Category(RegressionTests.class)
public class ExecuteTagTests {
	
	private static final String PATH = "src/wyvern/tools/tests/tagTests/code/execute/";

	@Test
	public void basic1() throws CopperParserException, IOException {
		String input = readFile("basic1.wyv");

		TypedAST ast = getAST(input);
		evaluateExpecting(ast, 15);
	}

	@Test
	public void basic2() throws CopperParserException, IOException {		
		String input = readFile("basic2.wyv");

		TypedAST ast = getAST(input);
		evaluateExpecting(ast, 25);
	}
	
	@Test
	public void basic3() throws CopperParserException, IOException {		
		String input = readFile("basic3.wyv");

		TypedAST ast = getAST(input);
		evaluateExpecting(ast, 15);
	}
	
	@Test
	public void basic4() throws CopperParserException, IOException {		
		String input = readFile("basic4.wyv");

		TypedAST ast = getAST(input);
		evaluateExpecting(ast, 15);
	}
	
	@Test
	public void basic5() throws CopperParserException, IOException {		
		String input = readFile("basic5.wyv");

		TypedAST ast = getAST(input);
		evaluateExpecting(ast, 25);
	}
	
	
	
	
	@Test
	public void match1() throws CopperParserException, IOException {		
		String input = readFile("match1.wyv");

		TypedAST ast = getAST(input);
		evaluateExpecting(ast, 15);
	}
	
	@Test
	public void match2() throws CopperParserException, IOException {		
		String input = readFile("match2.wyv");
		
		TypedAST ast = getAST(input);
		evaluateExpecting(ast, 1);
	}
	
	@Test
	public void match3() throws CopperParserException, IOException {
		String input = readFile("match3.wyv");
		
		TypedAST ast = getAST(input);
		evaluateExpecting(ast, 15);
	}
	
	@Test
	public void match4() throws CopperParserException, IOException {
		String input = readFile("match4.wyv");
		
		TypedAST ast = getAST(input);
		evaluateExpecting(ast, 15);
	}
	
	@Test
	public void match5() throws CopperParserException, IOException {
		String input = readFile("match5.wyv");
		
		TypedAST ast = getAST(input);
		evaluateExpecting(ast, 1);
	}
	
	
	@Test
	public void comprisesWithDefault1() throws CopperParserException, IOException {
		String input = readFile("comprisesWithDefault1.wyv");
		
		TypedAST ast = getAST(input);
		
		evaluateExpecting(ast, 25);
	}
	
	@Test
	public void comprisesWithDefault2() throws CopperParserException, IOException {
		String input = readFile("comprisesWithDefault2.wyv");
		
		TypedAST ast = getAST(input);
		
		evaluateExpecting(ast, 15);
	}
	
	@Test
	public void comprisesWithDefault3() throws CopperParserException, IOException {
		String input = readFile("comprisesWithDefault3.wyv");
		
		TypedAST ast = getAST(input);
		
		evaluateExpecting(ast, 10);
	}
	
	@Test
	public void comprisesWithDefaultComplex1() throws CopperParserException, IOException {
		String input = readFile("comprisesWithDefaultComplex1.wyv");
		
		TypedAST ast = getAST(input);
		
		evaluateExpecting(ast, 10);
	}
	
	@Test
	public void comprisesWithDefaultComplex2() throws CopperParserException, IOException {
		String input = readFile("comprisesWithDefaultComplex2.wyv");
		
		TypedAST ast = getAST(input);
		
		evaluateExpecting(ast, 10);
	}
	
	
	@Test
	public void comprisesWithDefaultComplex3() throws CopperParserException, IOException {
		String input = readFile("comprisesWithDefaultComplex3.wyv");
		
		TypedAST ast = getAST(input);
		
		evaluateExpecting(ast, 10);
	}
	
	
	@Test
	public void comprisesWithDefaultComplex4() throws CopperParserException, IOException {
		String input = readFile("comprisesWithDefaultComplex4.wyv");
		
		TypedAST ast = getAST(input);
		
		evaluateExpecting(ast, 15);
	}
	
	@Test
	public void comprisesWithDefaultComplex5() throws CopperParserException, IOException {
		String input = readFile("comprisesWithDefaultComplex5.wyv");
		
		TypedAST ast = getAST(input);
		
		evaluateExpecting(ast, 25);
	}
	
	
	
	//////////

	@Test
	public void comprisesWithoutDefault1() throws CopperParserException, IOException {
		String input = readFile("comprisesWithoutDefault1.wyv");
		
		TypedAST ast = getAST(input);
		
		evaluateExpecting(ast, 5);
	}
	
	@Test
	public void comprisesWithoutDefault2() throws CopperParserException, IOException {
		String input = readFile("comprisesWithoutDefault2.wyv");
		
		TypedAST ast = getAST(input);
		
		evaluateExpecting(ast, 10);
	}
	
	@Test
	public void comprisesWithoutDefault3() throws CopperParserException, IOException {
		String input = readFile("comprisesWithoutDefault3.wyv");
		
		TypedAST ast = getAST(input);
		
		evaluateExpecting(ast, 15);
	}
	
	@Test
	public void comprisesWithoutDefaultComplex1() throws CopperParserException, IOException {
		String input = readFile("comprisesWithoutDefaultComplex1.wyv");
		
		TypedAST ast = getAST(input);
		
		evaluateExpecting(ast, 5);
	}
	
	@Test
	public void comprisesWithoutDefaultComplex2() throws CopperParserException, IOException {
		String input = readFile("comprisesWithoutDefaultComplex2.wyv");
		
		TypedAST ast = getAST(input);
		
		evaluateExpecting(ast, 10);
	}
	
	
	@Test
	public void comprisesWithoutDefaultComplex3() throws CopperParserException, IOException {
		String input = readFile("comprisesWithoutDefaultComplex3.wyv");
		
		TypedAST ast = getAST(input);
		
		evaluateExpecting(ast, 12);
	}
	
	
	@Test
	public void comprisesWithoutDefaultComplex4() throws CopperParserException, IOException {
		String input = readFile("comprisesWithoutDefaultComplex4.wyv");
		
		TypedAST ast = getAST(input);
		
		evaluateExpecting(ast, 12);
	}
	
	@Test
	public void comprisesWithoutDefaultComplex5() throws CopperParserException, IOException {
		String input = readFile("comprisesWithoutDefaultComplex5.wyv");
		
		TypedAST ast = getAST(input);
		
		evaluateExpecting(ast, 10);
	}
	
	
	
	@Test
	public void hierarchy1() throws CopperParserException, IOException {
		String input = readFile("hierarchy1.wyv");
		
		TypedAST ast = getAST(input);
		
		evaluateExpecting(ast, 15);
	}
	
	@Test
	public void hierarchy2() throws CopperParserException, IOException {
		String input = readFile("hierarchy2.wyv");
		
		TypedAST ast = getAST(input);
		
		evaluateExpecting(ast, 35);
	}
	
	@Test
	public void hierarchy3() throws CopperParserException, IOException {
		String input = readFile("hierarchy3.wyv");
		
		TypedAST ast = getAST(input);
		
		evaluateExpecting(ast, 10);
	}
	
	@Test
	public void hierarchy4() throws CopperParserException, IOException {
		String input = readFile("hierarchy4.wyv");
		
		TypedAST ast = getAST(input);
		
		evaluateExpecting(ast, 15);
	}
	
	@Test
	public void hierarchy5() throws CopperParserException, IOException {
		String input = readFile("hierarchy5.wyv");
		
		TypedAST ast = getAST(input);
		
		evaluateExpecting(ast, 25);
	}
	
	@Test
	public void hierarchy6() throws CopperParserException, IOException {
		String input = readFile("hierarchy6.wyv");
		
		TypedAST ast = getAST(input);
		
		evaluateExpecting(ast, 25);
	}
	
	@Test
	public void hierarchy7() throws CopperParserException, IOException {
		String input = readFile("hierarchy7.wyv");
		
		TypedAST ast = getAST(input);
		
		evaluateExpecting(ast, 25);
	}
	
	@Test
	public void hierarchy8() throws CopperParserException, IOException {
		String input = readFile("hierarchy8.wyv");
		
		TypedAST ast = getAST(input);
		
		evaluateExpecting(ast, 25);
	}
	
	@Test
	public void hierarchy9() throws CopperParserException, IOException {
		String input = readFile("hierarchy9.wyv");
		
		TypedAST ast = getAST(input);
		
		evaluateExpecting(ast, 15);
	}
	
	@Test
	public void hierarchy10() throws CopperParserException, IOException {
		String input = readFile("hierarchy10.wyv");
		
		TypedAST ast = getAST(input);
		
		evaluateExpecting(ast, 25);
	}
	
	@Test
	public void json1() throws CopperParserException, IOException {
		String input = readFile("json1.wyv");
			
			TypedAST ast = getAST(input);
			
			evaluateExpecting(ast, 15);
	}
	
	@Test
	public void json2() throws CopperParserException, IOException {
		String input = readFile("json2.wyv");
		
		TypedAST ast = getAST(input);
		
		evaluateExpecting(ast, 25);
	}
	
	@Test
	public void json3() throws CopperParserException, IOException {
		String input = readFile("json3.wyv");
		
		TypedAST ast = getAST(input);
		
		evaluateExpecting(ast, 10);
	}
	
	@Test
	public void json4() throws CopperParserException, IOException {
		String input = readFile("json4.wyv");
		
		TypedAST ast = getAST(input);
		
		evaluateExpecting(ast, 20);
	}
	
	@Test
	public void json5() throws CopperParserException, IOException {
		String input = readFile("json5.wyv");
		
		TypedAST ast = getAST(input);
		
		evaluateExpecting(ast, 30);
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
