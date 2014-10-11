package wyvern.tools.tests.tagTests;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import wyvern.tools.typedAST.interfaces.TypedAST;

public class DynamicTagTests {

	private static final String PATH = "src/wyvern/tools/tests/tagTests/code/dynamic/";
	
	@Test
	/**
	 * This test ensures the dynamic tag example works properly.
	 */
	public void dynamicTagTest() {
		String program = TestUtil.readFile(PATH + "DynamicTags.wyv");
		
		Assert.fail();
	}
	
	@Test
	/**
	 * Test test ensures the dynamic tag example works properly when stripped of actual
	 * dynamic tag features. This is to make sure non-dynamic tag code is working.
	 */
	public void nonDynamicTest() throws CopperParserException, IOException {
		String program = TestUtil.readFile(PATH + "NonDynamicDynamicTag.wyv");
		TypedAST ast = TestUtil.getAST(program);
		
		TestUtil.evaluateExpecting(ast, 2);
	}
	
	@Test
	public void jsonTest() throws CopperParserException, IOException {
		String input = TestUtil.readFile(PATH + "TaggedTypeTest.wyv");
			
		TypedAST ast = TestUtil.getAST(input);
			
		TestUtil.evaluateExpecting(ast, 25);
	}
}
