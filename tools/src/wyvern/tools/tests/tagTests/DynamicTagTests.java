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
		System.out.println(program);
		TypedAST ast = TagTests.getAST(program);
		
		TagTests.evaluate(ast);
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
