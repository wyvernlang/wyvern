package wyvern.tools.tests;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.Wyvern;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;

public class TagTests {
	
	@Test
	/**
	 * Test the tagged keyword works with classes.
	 */
	public void taggedClassParseTest1() throws CopperParserException, IOException {		
		String input = 
				"tagged class X                 \n" +
				"    class def create() : X     \n" +
				"        new                    \n";
		
		new Wyvern().parse(new StringReader(input), "test input");
		//reaching here without a parse exception is a pass
	}
	
	@Test
	/**
	 * Test the tagged keyword works with types.
	 */
	public void taggedTypeParseTest1() throws CopperParserException, IOException {		
		String input = 	
				"val y = 12 + 1 \n" +
				"                               \n" +
				"tagged type IntWrapper\n" +
				"  def getValue() : Int\n" + 
			    "y";
		
		new Wyvern().parse(new StringReader(input), "test input");
		//reaching here without a parse exception is a pass
	}
	
	@Test
	/**
	 * Test the tagged keyword works with multiple types.
	 */
	public void taggedTypeParseTest2() throws CopperParserException, IOException {		
		String input = 	
				"val y = 5 + 1 \n" +
				"                               \n" +
				"tagged type Type1\n" +
				"  def getValue1() : Int\n" + 
				"                               \n" +
				"tagged type Type2\n" +
				"  def getValue2() : Int\n" + 
				"                               \n" +
			    "y";
		
		new Wyvern().parse(new StringReader(input), "test input");
		//reaching here without a parse exception is a pass
	}
	
	@Test
	public void matchParseTest1() throws CopperParserException, IOException {		
		String input = 
				"tagged class X                 \n" +
				"    class def create() : X     \n" +
				"        new                    \n" +
				"                               \n" +
				"val x = X.create()             \n" +
				"                               \n" +
				"match(x):                      \n" +
				"	X => 15                     \n" +
				"	default => 15               \n" +
				"                               \n";
				
		
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		res.typecheck(Environment.getEmptyEnvironment(), Optional.empty());
		
		res.evaluate(Environment.getEmptyEnvironment());
		
		//reaching here without a parse exception is a pass
	}
	
	@Test
	public void matchParseTest2() throws CopperParserException, IOException {		
		String input = 
				"tagged class X                 \n" +
				"    class def create() : X     \n" +
				"        new                    \n" +
				"                               \n" +
				"val x = X.create()             \n" +
				"                               \n" +
				"match(x):                      \n" +
				"	default => 15               \n";
				
		
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		res.typecheck(Environment.getEmptyEnvironment(), Optional.empty());
		
		res.evaluate(Environment.getEmptyEnvironment());
		
		Value v = res.evaluate(Environment.getEmptyEnvironment());
		Assert.assertEquals(v.toString(), "IntegerConstant(15)");
	}
	
	@Test
	public void matchParseTestMulti1() throws CopperParserException, IOException {		
		String input = 	
				"tagged class X                 \n" +
				"    class def create() : X     \n" +
				"        new                    \n" +
				"                               \n" +
				"tagged class Y                 \n" +
				"    class def create() : Y     \n" +
				"        new                    \n" +
				"                               \n" +
				"val x = X.create()             \n" +
				"                               \n" +
				"match(x):                      \n" +
				"	X => 15                     \n" +
				"	Y => 23                     \n" +
				"	default => 50                \n" +
				"                               \n";
				
		new Wyvern().parse(new StringReader(input), "test input");
		//reaching here without a parse exception is a pass
	}
	
	@Test
	public void matchParseTestMulti2() throws CopperParserException, IOException {		
		String input = 	
				"val y = 12 + 4           \n" +
				"                               \n" +
				"tagged class X                 \n" +
				"    class def create() : X     \n" +
				"        new                    \n" +
				"                               \n" +
				"tagged class Y                 \n" +
				"    class def create() : Y     \n" +
				"        new                    \n" +
				"                               \n" +
				"val x = X.create()             \n" +
				"                               \n" +
				"match(x):                      \n" +
				"	X => 15                     \n" +
				"	Y => 23                     \n" +
				"	default => 50                     \n" +
				"                               \n";
				
		new Wyvern().parse(new StringReader(input), "test input");		
		//reaching here without a parse exception is a pass
	}
	
	@Test
	public void duplicateCaseTest() throws CopperParserException, IOException {
		String input = 	
				"val y = 12 + 4           \n" +
				"                               \n" +
				"tagged class X                 \n" +
				"    class def create() : X     \n" +
				"        new                    \n" +
				"                               \n" +
				"tagged class Y                 \n" +
				"    class def create() : Y     \n" +
				"        new                    \n" +
				"                               \n" +
				"val x = X.create()             \n" +
				"                               \n" +
				"match(x):                      \n" +
				"	X => 15                     \n" +
				"	Y => 23                     \n" +
				"	Y => 34                     \n" +
				"	default => 50                     \n" +
				"                               \n";
				
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		
		try {
			res.typecheck(Environment.getEmptyEnvironment(), Optional.empty());
		} catch (ToolError toolError) {
			Assert.assertEquals(toolError.getTypecheckingErrorMessage(), ErrorMessage.DUPLICATE_TAG_ERROR);
			
			return;
		}
		
		Assert.fail("Should have failed with error: " + ErrorMessage.DUPLICATE_TAG_ERROR);
	}
}
