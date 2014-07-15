package wyvern.tools.tests.tagTests;

import java.io.IOException;

import org.junit.Test;

import wyvern.tools.typedAST.interfaces.TypedAST;
import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;

public class MiscTagTests {

	private static final String PATH = "src/wyvern/tools/tests/tagTests/miscTagCode/";
	
	@Test
	public void returnTest1() throws CopperParserException, IOException {
		String program = TagTestUtil.readFile(PATH + "Return1.wyv");
		
		TypedAST ast = TagTestUtil.getAST(program);
		
		TagTestUtil.evaluateExpecting(ast, 15);
	}
	
	@Test
	public void returnTest2() throws CopperParserException, IOException {
		String program = TagTestUtil.readFile(PATH + "Return2.wyv");
		
		TypedAST ast = TagTestUtil.getAST(program);
		
		TagTestUtil.evaluateExpecting(ast, 10);
	}
	
	@Test
	public void returnTest3() throws CopperParserException, IOException {
		String program = TagTestUtil.readFile(PATH + "Return3.wyv");
		
		TypedAST ast = TagTestUtil.getAST(program);
		
		TagTestUtil.evaluateExpecting(ast, "X-tag");
	}
	
	@Test
	public void tagParamTest1() throws CopperParserException, IOException {
		String program = TagTestUtil.readFile(PATH + "TagParamTest1.wyv");
		
		TypedAST ast = TagTestUtil.getAST(program);
		
		TagTestUtil.evaluateExpecting(ast, 15);
	}
	
	public void ifTest1() throws CopperParserException, IOException {
		String program = TagTestUtil.readFile(PATH + "IfTest1.wyv");
		
		TypedAST ast = TagTestUtil.getAST(program);
		
		TagTestUtil.evaluateExpecting(ast, 1);
	}
	
	@Test
	public void thisTest1() throws CopperParserException, IOException {
		String program = TagTestUtil.readFile(PATH + "ThisTest.wyv");
		
		TypedAST ast = TagTestUtil.getAST(program);
		
		TagTestUtil.evaluateExpecting(ast, 13);
	}
}
