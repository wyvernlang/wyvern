package wyvern.tools.tests.tagTests;

import java.io.IOException;

import org.junit.Test;

import wyvern.tools.typedAST.interfaces.TypedAST;
import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;

public class DynamicTagTests {

	private static final String PATH = "src/wyvern/tools/tests/tagTests/dynamicCode/";
	
	@Test
	/**
	 * This test ensures the dynamic tag example works properly.
	 */
	public void dynamicTagTest1() throws CopperParserException, IOException {
		String program = TagTestUtil.readFile(PATH + "DynamicTags1.wyv");
		
		TypedAST ast = TagTestUtil.getAST(program);
		
		TagTestUtil.evaluateExpecting(ast, 12);
	}
	
	@Test
	public void dynamicTagTest2() throws CopperParserException, IOException {
		String program = TagTestUtil.readFile(PATH + "DynamicTags2.wyv");
		
		TypedAST ast = TagTestUtil.getAST(program);
		
		TagTestUtil.evaluateExpecting(ast, 10);
	}
	
	@Test
	public void dynamicTagTest3() throws CopperParserException, IOException {
		String program = TagTestUtil.readFile(PATH + "DynamicTags3.wyv");
		
		TypedAST ast = TagTestUtil.getAST(program);
		
		TagTestUtil.evaluateExpecting(ast, 11);
	}
	
	@Test
	public void dynamicTagTestWindow() throws CopperParserException, IOException {
		String program = TagTestUtil.readFile(PATH + "DynamicTagsWindow.wyv");
		
		TypedAST ast = TagTestUtil.getAST(program);
		
		TagTestUtil.evaluateExpecting(ast, "bordered");
	}
	
	@Test
	public void dynamicTagTestWindowPaper() throws CopperParserException, IOException {
		String program = TagTestUtil.readFile(PATH + "DynamicTagsWindowPaper.wyv");
		
		TypedAST ast = TagTestUtil.getAST(program);
		
		TagTestUtil.evaluateExpecting(ast, "big");
	}
	
	@Test
	public void dynamicTagTestWindowSimple() throws CopperParserException, IOException {
		String program = TagTestUtil.readFile(PATH + "DynamicTagsWindowSimple.wyv");
		
		TypedAST ast = TagTestUtil.getAST(program);
		
		TagTestUtil.evaluateExpecting(ast, "bordered");
	}
	
	@Test
	/**
	 * Test test ensures the dynamic tag example works properly when stripped of actual
	 * dynamic tag features. This is to make sure non-dynamic tag code is working.
	 */
	public void nonDynamicTest() throws CopperParserException, IOException {
		String program = TagTestUtil.readFile(PATH + "NonDynamicDynamicTag.wyv");
		TypedAST ast = TagTestUtil.getAST(program);
		
		TagTestUtil.evaluateExpecting(ast, 2);
	}

	@Test
	public void taggedTypeTest1() throws CopperParserException, IOException {		
		String input = 
				"tagged type X                      \n" +
				"    def foo() : Int                \n" +
				"                                   \n" +
				"def create() : X = new             \n" +
				"    def foo() : Int = 15           \n" +
				"                                   \n" +
				"val x = create()                   \n" +
				"                                   \n" +
				"match(x):                          \n" + 
				"	X => 15                         \n" +
				"	default => 30                   \n";
				
		TypedAST ast = TagTestUtil.getAST(input);
		TagTestUtil.evaluateExpecting(ast, 15);
	}
	
	@Test
	public void taggedTypeTest2() throws CopperParserException, IOException {		
		String input = 
				"tagged type X                      \n" +
				"    def foo() : Int                \n" +
				"                                   \n" +
				"tagged type Y                      \n" +
				"    def foo() : Int                \n" +
				"                                   \n" +
				"def create() : Y = new             \n" +
				"    def foo() : Int = 15           \n" +
				"                                   \n" +
				"val x = create()                   \n" +
				"                                   \n" +
				"match(x):                          \n" + 
				"	Y => 20                         \n" +
				"	X => 15                         \n" +
				"	default => 30                   \n";
				
		TypedAST ast = TagTestUtil.getAST(input);
		TagTestUtil.evaluateExpecting(ast, 20);
	}
	
	@Test
	public void taggedTypeTest3() throws CopperParserException, IOException {		
		String input = 
				"tagged type X [comprises Y, Z]     \n" +
				"    def foo() : Int                \n" +
				"                                   \n" +
				"tagged type Y [case of X]          \n" +
				"    def foo() : Int                \n" +
				"                                   \n" +
				"tagged type Z [case of X]          \n" +
				"    def foo() : Int                \n" +
				"                                   \n" +
				"def create() : X = new             \n" +
				"    def foo() : Int = 15           \n" +
				"                                   \n" +
				"val x : X = create()               \n" +
				"                                   \n" +
				"match(x):                          \n" + 
				"	Y => 20                         \n" +
				"	Z => 15                         \n" +
				"	X => 30                         \n";
				
		TypedAST ast = TagTestUtil.getAST(input);
		TagTestUtil.evaluateExpecting(ast, 30);
	}
	
	@Test
	public void taggedTypeTest4() throws CopperParserException, IOException {		
		String input = 
				"tagged type X [comprises Y, Z]     \n" +
				"    def foo() : Int                \n" +
				"                                   \n" +
				"tagged type Y [case of X]          \n" +
				"    def foo() : Int                \n" +
				"                                   \n" +
				"tagged type Z [case of X]          \n" +
				"    def foo() : Int                \n" +
				"                                   \n" +
				"def create() : Y = new             \n" +
				"    def foo() : Int = 15           \n" +
				"                                   \n" +
				"val x = create()                   \n" +
				"                                   \n" +
				"match(x):                          \n" + 
				"	Y => 20                         \n" +
				"	X => 30                         \n" +
				"	default => 30                         \n";
				
		TypedAST ast = TagTestUtil.getAST(input);
		TagTestUtil.evaluateExpecting(ast, 20);
	}
}
