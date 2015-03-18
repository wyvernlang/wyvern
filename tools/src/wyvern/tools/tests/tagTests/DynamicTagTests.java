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
	public void ecoop2015Artifact() throws CopperParserException, IOException {
		String program = TestUtil.readFile(PATH + "ECOOP2015Artifact.wyv");
		TypedAST ast = TestUtil.getAST(program);
		
		TestUtil.evaluateExpecting(ast, "big");
	}
	
	@Test
	/**
	 * This test ensures the dynamic tag example works properly.
	 */
	public void dynamicTagTest() throws CopperParserException, IOException {
		String program = TestUtil.readFile(PATH + "DynamicTags.wyv");
		TypedAST ast = TestUtil.getAST(program);
		
		TestUtil.evaluateExpecting(ast, 42);
	}
	
	@Test
	/**
	 * This test ensures the dynamic tag example works properly.
	 */
	public void dynamicTagTest1() throws CopperParserException, IOException {
		String program = TestUtil.readFile(PATH + "DynamicTags1.wyv");
		TypedAST ast = TestUtil.getAST(program);
		
		TestUtil.evaluateExpecting(ast, 42);
	}
	
	@Test
	/**
	 * This test ensures the dynamic tag example works properly.
	 */
	public void dynamicTagTest2() throws CopperParserException, IOException {
		String program = TestUtil.readFile(PATH + "DynamicTags2.wyv");
		TypedAST ast = TestUtil.getAST(program);
		
		TestUtil.evaluateExpecting(ast, 42);
	}
	
	@Test
	/**
	 * This test ensures the dynamic tag example works properly.
	 */
	public void dynamicTagTest3() throws CopperParserException, IOException {
		String program = TestUtil.readFile(PATH + "DynamicTags3.wyv");
		TypedAST ast = TestUtil.getAST(program);
		
		TestUtil.evaluateExpecting(ast, 42);
	}
	
	@Test
	/**
	 * This test ensures the dynamic tag example works properly.
	 */
	public void dynamicTagsWindowTest() throws CopperParserException, IOException {
		String program = TestUtil.readFile(PATH + "DynamicTagsWindow.wyv");
		TypedAST ast = TestUtil.getAST(program);

		TestUtil.evaluateExpecting(ast, "scrollable");
	}
	
	@Test
	/**
	 * This test ensures the dynamic tag example works properly.
	 */
	public void dynamicTagsWindowPaperTest() throws CopperParserException, IOException {
		String program = TestUtil.readFile(PATH + "DynamicTagsWindowPaper.wyv");
		TypedAST ast = TestUtil.getAST(program);
		
		TestUtil.evaluateExpecting(ast, "big");
	}
	
	@Test
	/**
	 * This test ensures the dynamic tag example works properly.
	 */
	public void dynamicTagsWindowSimpleTest() throws CopperParserException, IOException {
		String program = TestUtil.readFile(PATH + "DynamicTagsWindowSimple.wyv");
		TypedAST ast = TestUtil.getAST(program);

		TestUtil.evaluateExpecting(ast, "bordered");
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
	/**
	 * Test test ensures the dynamic tag example works properly when stripped of actual
	 * dynamic tag features. This is to make sure non-dynamic tag code is working.
	 */
	public void thisTest() throws CopperParserException, IOException {
		String program = TestUtil.readFile(PATH + "ThisTest.wyv");
		TypedAST ast = TestUtil.getAST(program);
		
		TestUtil.evaluateExpecting(ast, 13);
	}
}