package wyvern.tools.tests.tagTests;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.Optional;

import org.junit.Assert;

import wyvern.stdlib.Globals;
import wyvern.tools.parsing.Wyvern;
import wyvern.tools.typedAST.core.binding.TagBinding;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;

public class TestUtil {

	/**
	 * Converts the given program into the AST representation.
	 * 
	 * @param program
	 * @return
	 * @throws IOException 
	 * @throws CopperParserException 
	 */
	public static TypedAST getAST(String program) throws CopperParserException, IOException {
		TagBinding.resetGlobalData();
		return (TypedAST)new Wyvern().parse(new StringReader(program), "test input");
	}
	
	/**
	 * Completely evaluates the given AST, and compares it to the given value.
	 * Does typechecking first, then evaluation.
	 * 
	 * @param ast
	 * @param value
	 */
	public static void evaluateExpecting(TypedAST ast, int value) {
		ast.typecheck(Globals.getStandardEnv(), Optional.empty());
		Value v = ast.evaluate(Globals.getStandardEnv());
		
		String expecting = "IntegerConstant(" + value + ")"; 

		Assert.assertEquals(expecting, v.toString());
	}
	
	/**
	 * First typechecks the AST, then executes it.
	 * 
	 * Any returned value is discarded, but anything printed to stdout will be visible.
	 * 
	 * @param ast
	 */
	public static void evaluate(TypedAST ast) {
		ast.typecheck(Globals.getStandardEnv(), Optional.empty());
		ast.evaluate(Globals.getStandardEnv());
	}
	
	public static String readFile(String filename) {
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
