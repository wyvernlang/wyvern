package wyvern.tools.tests.perfTests;

import java.io.IOException;
import java.util.Optional;

import org.junit.Ignore;
import org.junit.Test;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import wyvern.stdlib.Globals;
import wyvern.tools.tests.tagTests.TestUtil;
import wyvern.tools.typedAST.interfaces.TypedAST;

public class PerformanceTests {

	@Ignore
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

	@Ignore
	@Test
	public void parseManyClassTags() throws CopperParserException, IOException {
		int[] numTags = new int[]{1, 10, 50, 100, 350, 500, 1000, 1500, 2500, 3000, 4000, 5000};
		
		for (int i : numTags) {
			//String file = "manyTags" + i + ".wyv";
			String program = generateTaggedClass(i);
			
			double timeBefore = System.currentTimeMillis();
			TestUtil.getAST(program);
			double timeAfter = System.currentTimeMillis();
			
			double dt = timeAfter - timeBefore;
			
			//System.out.println("size: " + i + ", dt: " + dt);
			//System.out.println(i + ", " + dt);
		}
		
		for (int i : numTags) {
			//String file = "manyTags" + i + ".wyv";
			//String program = DynamicTagTests.readFile(PATH + file);
			
			String program = generateClass(i);
			
			double timeBefore = System.currentTimeMillis();
			TestUtil.getAST(program);
			double timeAfter = System.currentTimeMillis();
			
			double dt = timeAfter - timeBefore;
			
			System.out.println(i + ", " + dt);
		}
		
		for (int i : numTags) {
			//String file = "manyTags" + i + ".wyv";
			String program = generateTaggedClass(i);
			
			double timeBefore = System.currentTimeMillis();
			TestUtil.getAST(program);
			double timeAfter = System.currentTimeMillis();
			
			double dt = timeAfter - timeBefore;
			
			//System.out.println("size: " + i + ", dt: " + dt);
			System.out.println(i + ", " + dt);
		}
	}

	@Ignore
	@Test
	public void typecheckManyClassTags() throws CopperParserException, IOException {
		int[] numTags = new int[]{1, 10, 50, 150, 200, 250, 300, 350, 400, 450, 500};
		
		for (int i : numTags) {
			//String file = "manyTags" + i + ".wyv";
			String program = generateTaggedClass(i);
			
			TypedAST ast = TestUtil.getAST(program);
			double timeBefore = System.currentTimeMillis();
			ast.typecheck(Globals.getStandardEnv(), Optional.empty());
			double timeAfter = System.currentTimeMillis();
			
			double dt = timeAfter - timeBefore;
			
			//System.out.println("size: " + i + ", dt: " + dt);
			//System.out.println(i + ", " + dt);
		}
		
		for (int i : numTags) {
			//String file = "manyTags" + i + ".wyv";
			//String program = DynamicTagTests.readFile(PATH + file);
			
			String program = generateClass(i);
			
			TypedAST ast = TestUtil.getAST(program);
			
			double timeBefore = System.currentTimeMillis();
			ast.typecheck(Globals.getStandardEnv(), Optional.empty());
			double timeAfter = System.currentTimeMillis();
			
			double dt = timeAfter - timeBefore;
			
			System.out.println(i + ", " + dt);
		}
		
		for (int i : numTags) {
			//String file = "manyTags" + i + ".wyv";
			String program = generateTaggedClass(i);
			
			TypedAST ast = TestUtil.getAST(program);
			
			double timeBefore = System.currentTimeMillis();
			ast.typecheck(Globals.getStandardEnv(), Optional.empty());
			double timeAfter = System.currentTimeMillis();
			
			double dt = timeAfter - timeBefore;
			
			//System.out.println("size: " + i + ", dt: " + dt);
			System.out.println(i + ", " + dt);
		}
	}


	@Ignore
	@Test
	public void timeEarlyMatch() throws CopperParserException, IOException {
		//int[] numTags = new int[]{1, 10, 50, 150, 200, 250, 300, 350, 400, 450, 500};
		//int[] numTags = new int[]{1, 10, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000};
		//int[] numTags = new int[]{1, 10, 50, 100, 350, 500, 1000, 1500, 2500, 3000, 4000, 5000};
		
		int[] numTags = new int[200];
		
		for (int i = 0; i < numTags.length; i++) {
			numTags[i] = i * 5;
		}
		
		numTags[0] = 1;
		
		for (int i : numTags) {
			//String file = "manyTags" + i + ".wyv";
			//String program = DynamicTagTests.readFile(PATH + file);
			
			String program = generateMatchProgram(i) + "\n";
			
			//System.out.println(program);
			
			
			TypedAST ast = TestUtil.getAST(program);
			ast.typecheck(Globals.getStandardEnv(), Optional.empty());

			double timeBefore = System.currentTimeMillis();
			TestUtil.evaluatePerf(ast);
			double timeAfter = System.currentTimeMillis();
			
			double dt = timeAfter - timeBefore;
			
			System.out.println(i + ", " + dt + ",");
		}
	}

	@Ignore
	@Test
	public void timeLateMatch() throws CopperParserException, IOException {
		//int[] numTags = new int[]{1, 10, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000};
		
		//int[] numTags = new int[]{1, 10, 50, 100, 150, 500, 1000, 1500, 2500, 3000, 4000, 5000};
		
		int[] numTags = new int[200];
		
		for (int i = 0; i < numTags.length; i++) {
			numTags[i] = i * 5;
		}
		
		numTags[0] = 1;
		
		
		for (int i : numTags) {
			//String file = "manyTags" + i + ".wyv";
			//String program = DynamicTagTests.readFile(PATH + file);
			
			String program = generateMatchProgramLate(i) + "\n";
			
			//System.out.println(program);
			
			
			TypedAST ast = TestUtil.getAST(program);
			ast.typecheck(Globals.getStandardEnv(), Optional.empty());

			double timeBefore = System.currentTimeMillis();
			TestUtil.evaluateExpectingPerf(ast, 0);
			double timeAfter = System.currentTimeMillis();
			
			double dt = timeAfter - timeBefore;
			
			System.out.println(i + ", " + dt + ",");
		}
	}


	@Ignore
	@Test
	public void parseManyHierarchicalTags() throws CopperParserException, IOException {
		
	}

	public static String generateTaggedTypes(int numTypes) {
		StringBuffer toReturn = new StringBuffer();
		
		for (int i = 1; i <= numTypes; i++) {
			toReturn.append("tagged class Type" + i + "\n");
			toReturn.append("    def getValue" + i + "() : Int\n\n");
		}
		
		//to make sure we have a valid program
		toReturn.append("15");
		
		return toReturn.toString();
	}
	

	public static String generateTaggedClass(int numTypes) {
		StringBuffer toReturn = new StringBuffer();
		
		for (int i = 1; i <= numTypes; i++) {
			String className = "Type" + i;
			toReturn.append("tagged class " + className + "\n");
			toReturn.append("    class def create() : " + className + "\n");
			toReturn.append("        new\n\n");
		}
		
		//to make sure we have a valid program
		toReturn.append("15");
		
		return toReturn.toString();
	}
	
	public static String generateMatchProgram(int numTypes) {
		StringBuffer toReturn = new StringBuffer();
		
		toReturn.append("tagged class Type \n");
		toReturn.append("    class def create() : Type \n");
		toReturn.append("        new\n\n");
		
		for (int i = 1; i <= numTypes; i++) {
			String className = "Type" + i;
			toReturn.append("tagged class " + className + " [case of Type]\n");
			toReturn.append("    class def create() : " + className + "\n");
			toReturn.append("        new\n\n");
		}
		
		toReturn.append("val obj : Type = Type"+ numTypes + ".create() \n\n");
		
		toReturn.append("match (obj): \n");
		
		for (int i = 1; i <= numTypes; i++) {
			String className = "Type" + i;
			toReturn.append("    " + className + " => " + i + " \n");
		}
		
		toReturn.append("    default => 0");
		
		return toReturn.toString();
	}
	

	public static String generateMatchProgramLate(int numTypes) {
		StringBuffer toReturn = new StringBuffer();
		
		toReturn.append("tagged class Type \n");
		toReturn.append("    class def create() : Type \n");
		toReturn.append("        new\n\n");
		
		for (int i = 1; i <= numTypes; i++) {
			String className = "Type" + i;
			toReturn.append("tagged class " + className + " [case of Type]\n");
			toReturn.append("    class def create() : " + className + "\n");
			toReturn.append("        new\n\n");
		}
		
		toReturn.append("val obj : Type = Type.create() \n\n");
		
		toReturn.append("match (obj): \n");
		
		for (int i = 1; i <= numTypes; i++) {
			String className = "Type" + i;
			toReturn.append("    " + className + " => " + i + " \n");
		}
		
		toReturn.append("    default => 0");
		
		return toReturn.toString();
	}
	

	public static String generateClass(int numTypes) {
		StringBuffer toReturn = new StringBuffer();
		
		for (int i = 1; i <= numTypes; i++) {
			String className = "Type" + i;
			toReturn.append("class " + className + "\n");
			toReturn.append("    class def create() : " + className + "\n");
			toReturn.append("        new\n\n");
		}
		
		//to make sure we have a valid program
		toReturn.append("15");
		
		return toReturn.toString();
	}
}
