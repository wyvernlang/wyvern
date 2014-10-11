package wyvern.tools.tests.perfTests;

import java.io.IOException;

import org.junit.Test;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import wyvern.tools.tests.tagTests.TestUtil;

public class PerformanceTests {

	private static final String PATH = "src/wyvern/tools/tests/perfTests/code/";
	
	@Test
	public void parseManyTags() throws CopperParserException, IOException {
		int[] numTags = new int[]{1, 10, 50, 100, 350, 500, 1000, 1500, 2500, 3000, 4000, 5000};
		
		for (int i : numTags) {
			//String file = "manyTags" + i + ".wyv";
			String program = generateTaggedTypes(i);
			
			double timeBefore = System.currentTimeMillis();
			TestUtil.getAST(program);
			double timeAfter = System.currentTimeMillis();
			
			double dt = timeAfter - timeBefore;
			
			//System.out.println("size: " + i + ", dt: " + dt);
			System.out.println(i + ", " + dt);
		}
		
		for (int i : numTags) {
			//String file = "manyTags" + i + ".wyv";
			//String program = DynamicTagTests.readFile(PATH + file);
			
			String program = generateTaggedTypes(i);
			
			double timeBefore = System.currentTimeMillis();
			TestUtil.getAST(program);
			double timeAfter = System.currentTimeMillis();
			
			double dt = timeAfter - timeBefore;
			
			System.out.println("size: " + i + ", dt: " + dt);
		}
	}
	
	@Test
	public void parseManyHierarchicalTags() throws CopperParserException, IOException {
		
	}

	public static String generateTaggedTypes(int numTypes) {
		StringBuffer toReturn = new StringBuffer();
		
		for (int i = 1; i <= numTypes; i++) {
			toReturn.append("tagged type Type" + i + "\n");
			toReturn.append("    def getValue" + i + "() : Int\n\n");
		}
		
		return toReturn.toString();
	}
}
