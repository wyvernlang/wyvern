package wyvern.tools.tests;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import wyvern.tools.parsing.Wyvern;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;

public class TagTests {
	
	@Test
	/**
	 * Test the tagged keyword works with classes.
	 */
	public void taggedClassTest1() throws CopperParserException, IOException {		
		String input = 	
				"val y = 12 + 4           \n" +
				"                               \n" +
				"tagged class X                 \n" +
				"    class def create() : X     \n" +
				"        new                    \n" +
				"y							       \n";
		
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		res.typecheck(Environment.getEmptyEnvironment(), Optional.empty());
		
		Value v = res.evaluate(Environment.getEmptyEnvironment());
		Assert.assertEquals(v.toString(), "IntegerConstant(16)");	
	}
	
	@Test
	/**
	 * Test the tagged keyword works with types.
	 */
	public void taggedTypeTest1() throws CopperParserException, IOException {		
		String input = 	
				"val y = 12 + 1 \n" +
				"                               \n" +
				"tagged type IntWrapper\n" +
				"  def getValue() : Int\n" + 
			    "y";
		
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		
		Value v = res.evaluate(Environment.getEmptyEnvironment());
		Assert.assertEquals(v.toString(), "IntegerConstant(13)");
	}
	
	@Test
	/**
	 * Test the tagged keyword works with multiple types.
	 */
	public void taggedTypeTest2() throws CopperParserException, IOException {		
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
		
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		
		Value v = res.evaluate(Environment.getEmptyEnvironment());
		Assert.assertEquals(v.toString(), "IntegerConstant(6)");
	}
	
	@Test
	public void matchTest1() throws CopperParserException, IOException {		
		String input = 	
				"val y = 12 + 4           \n" +
				"                               \n" +
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
	public void matchTest2() throws CopperParserException, IOException {		
		String input = 	
				"val y = 12 + 4           \n" +
				"                               \n" +
				"tagged class X                 \n" +
				"    class def create() : X     \n" +
				"        new                    \n" +
				"                               \n" +
				"val x = X.create()             \n" +
				"                               \n" +
				"match(x):                      \n" +
				"	default => 15               \n" +
				"                               \n";
				
		
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		res.typecheck(Environment.getEmptyEnvironment(), Optional.empty());
		
		res.evaluate(Environment.getEmptyEnvironment());
		
		//reaching here without a parse exception is a pass
	}
	
	@Test
	public void matchTestMulti1() throws CopperParserException, IOException {		
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
				"	default => 50                \n" +
				"                               \n";
				
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		res.typecheck(Environment.getEmptyEnvironment(), Optional.empty());
		
		res.evaluate(Environment.getEmptyEnvironment());
		
		//reaching here without a parse exception is a pass
	}
	
	@Test
	public void matchTestMulti2() throws CopperParserException, IOException {		
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
				
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		res.typecheck(Environment.getEmptyEnvironment(), Optional.empty());
		
		res.evaluate(Environment.getEmptyEnvironment());
		
		
		//reaching here without a parse exception is a pass
	}
}
