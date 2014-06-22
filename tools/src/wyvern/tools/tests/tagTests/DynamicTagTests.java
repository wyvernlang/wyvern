package wyvern.tools.tests.tagTests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Assert;
import org.junit.Test;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import wyvern.tools.typedAST.interfaces.TypedAST;

public class DynamicTagTests {

	private static final String PATH = "src/wyvern/tools/tests/tagTests/code/";
	
	@Test
	/**
	 * This test ensures the dynamic tag example works properly.
	 */
	public void dynamicTagTest() {
		String program = readFile(PATH + "DynamicTags.wyv");
		
		Assert.fail();
	}
	
	@Test
	/**
	 * Test test ensures the dynamic tag example works properly when stripped of actual
	 * dynamic tag features. This is to make sure non-dynamic tag code is working.
	 */
	public void nonDynamicTest() throws CopperParserException, IOException {
		String program = readFile(PATH + "NonDynamicDynamicTag.wyv");
		TypedAST ast = TagTests.getAST(program);
		
		TagTests.evaluateExpecting(ast, 2);
	}

	@Test
	public void taggedTypeTest1() throws CopperParserException, IOException {		
		String input = 
				"tagged type X                      \n" +
				"    def foo() : Int                \n" +
				"                                   \n" +
				"class C                            \n" +
				"    class def create() : X = new   \n" +
				"        def foo() : Int = 15       \n" +
				"                                   \n" +
				"val x = C.create()                 \n" +
				"                                   \n" +
				"match(x):                          \n" + 
				"	X => 15                   \n" +
				//"	StrJSON => 20                   \n" + 
				"	default => 30                   \n";
				
		TypedAST ast = TagTests.getAST(input);
		TagTests.evaluateExpecting(ast, 15);
	}
	
	@Test
	public void jsonTest() throws CopperParserException, IOException {
		String input = readFile(PATH + "TaggedTypeTest.wyv");
			
		TypedAST ast = TagTests.getAST(input);
			
		TagTests.evaluateExpecting(ast, 25);
	}
	
	private String readFile(String filename) {
		try {
			StringBuffer b = new StringBuffer();
			
			for (String s : Files.readAllLines(new File(filename).toPath())) {
				//Be sure to add the newline as well
				b.append(s).append("\n");
			}
			
			return b.toString();
		} catch (IOException e) {
			Assert.fail("Failed opening file: " + filename);
			throw new RuntimeException(e);
		}
	}
}
