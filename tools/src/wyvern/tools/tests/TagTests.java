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
				"tagged type IntWrapper    \n" +
				"  def getValue() : Int    \n";
		
		new Wyvern().parse(new StringReader(input), "test input");
		//reaching here without a parse exception is a pass
	}
	
	@Test
	/**
	 * Test the tagged keyword works with multiple types.
	 */
	public void taggedTypeParseTest2() throws CopperParserException, IOException {		
		String input = 
				"tagged type Type1          \n" +
				"  def getValue1() : Int    \n" + 
				"                           \n" +
				"tagged type Type2          \n" +
				"  def getValue2() : Int    \n";
		
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
				"	default => 15               \n";
		
		new Wyvern().parse(new StringReader(input), "test input");
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
				"	default => 50               \n";
				
		new Wyvern().parse(new StringReader(input), "test input");
		//reaching here without a parse exception is a pass
	}
	
	@Test
	public void matchParseTestMulti2() throws CopperParserException, IOException {		
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
				"	default => 50               \n";
				
		new Wyvern().parse(new StringReader(input), "test input");		
		//reaching here without a parse exception is a pass
	}
	
	@Test
	public void matchInterpretTest1() throws CopperParserException, IOException {		
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
	public void matchInterpretTest2() throws CopperParserException, IOException {		
		String input = 
				"tagged class X                 \n" +
				"    class def create() : X     \n" +
				"        new                    \n" +
				"                               \n" +
				"val x = X.create()             \n" +
				"                               \n" +
				"match(x):                      \n" +
				"	X => 25                     \n" +
				"	default => 15               \n";
				
		
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		res.typecheck(Environment.getEmptyEnvironment(), Optional.empty());
		
		res.evaluate(Environment.getEmptyEnvironment());
		
		Value v = res.evaluate(Environment.getEmptyEnvironment());
		Assert.assertEquals(v.toString(), "IntegerConstant(25)");
	}
	
	@Test
	public void duplicateCaseTest() throws CopperParserException, IOException {
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
				"	Y => 34                     \n" +	// Y given twice; error
				"	default => 50               \n";
				
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		
		try {
			res.typecheck(Environment.getEmptyEnvironment(), Optional.empty());
		} catch (ToolError toolError) {
			Assert.assertEquals(toolError.getTypecheckingErrorMessage(), ErrorMessage.DUPLICATE_TAG);
			
			return;
		}
		
		Assert.fail("Should have failed with error: " + ErrorMessage.DUPLICATE_TAG);
	}
	
	@Test
	public void unknownCaseTest() throws CopperParserException, IOException {
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
				"	Z => 34                     \n" +	// Z is not declared anywhere; error
				"	default => 50               \n";
				
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		
		try {
			res.typecheck(Environment.getEmptyEnvironment(), Optional.empty());
		} catch (ToolError toolError) {
			Assert.assertEquals(toolError.getTypecheckingErrorMessage(), ErrorMessage.UNKNOWN_TAG);
			
			return;
		}
		
		Assert.fail("Should have failed with error: " + ErrorMessage.UNKNOWN_TAG);
	}
	
	@Test
	public void untaggedCaseTest() throws CopperParserException, IOException {
		String input = 	
				"tagged class X                 \n" +
				"    class def create() : X     \n" +
				"        new                    \n" +
				"                               \n" +
				"tagged class Y                 \n" +
				"    class def create() : Y     \n" +
				"        new                    \n" +
				"                               \n" +
				"class Z                        \n" +
				"    class def create() : Y     \n" +
				"        new                    \n" +
				"                               \n" +
				"val x = X.create()             \n" +
				"                               \n" +
				"match(x):                      \n" +
				"	X => 15                     \n" +
				"	Y => 23                     \n" +
				"	Z => 34                     \n" +	// Z is not declared but not a tagged type; error
				"	default => 50               \n";
				
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		
		try {
			res.typecheck(Environment.getEmptyEnvironment(), Optional.empty());
		} catch (ToolError toolError) {
			//TODO: maybe this should be a different error message
			Assert.assertEquals(toolError.getTypecheckingErrorMessage(), ErrorMessage.UNKNOWN_TAG);
			
			return;
		}
		
		Assert.fail("Should have failed with error: " + ErrorMessage.UNKNOWN_TAG);
	}
	
	@Test
	//TODO: this test fails because it throws an exception during parsing, and not the defined error message.
	// It might be worth finding a way to bubble this parse exception up to the user in a more readable way...
	public void defaultNotLastTest() throws CopperParserException, IOException {
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
				"	default => 23                     \n" +
				"	Y => 50               \n";
				
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		
		try {
			res.typecheck(Environment.getEmptyEnvironment(), Optional.empty());
		} catch (ToolError toolError) {
			Assert.assertEquals(toolError.getTypecheckingErrorMessage(), ErrorMessage.DEFAULT_NOT_LAST);
			
			return;
		}
		
		Assert.fail("Should have failed with error: " + ErrorMessage.DEFAULT_NOT_LAST);
	}
	
	public void nonExhaustiveErrorTest() {
		String input = 
			"tagged class Dyn [comprises DynInt, DynChar, DynByte]       \n" +
			"    class def create() : X                                  \n" +
			"        new                                                 \n" +
			"                                                            \n" +
			"tagged class DynInt [case of Dyn]                           \n" +
			"    class def create() : DynInt                             \n" +
			"        new                                                 \n" +
			"                                                            \n" +
			"tagged class DynChar [case of Dyn]                          \n" +
			"    class def create() : DynChar                            \n" +
			"        new                                                 \n" +
			"                                                            \n" +
			"tagged class DynByte [case of Dyn]                          \n" +
			"    class def create() : DynByte                            \n" +
			"        new                                                 \n" +
			"                                                            \n" +
			"val i = DynInt.create()                                     \n" +
			"                                                            \n" +
			"match(i):                                                   \n" +
			"	DynInt => 10                                             \n" +
			"	DynChar => 15                                            \n";
		// DynByte not specified; error
		
		//ErrorMessage.DEFAULT_NOT_PRESENT
		
		Assert.fail("TODO");
	}
	
	public void ExhaustiveWithDefaultTest() {
		String input = 
				"tagged class Dyn [comprises DynInt, DynChar, DynByte]       \n" +
				"    class def create() : X                                  \n" +
				"        new                                                 \n" +
				"                                                            \n" +
				"tagged class DynInt [case of Dyn]                           \n" +
				"    class def create() : DynInt                             \n" +
				"        new                                                 \n" +
				"                                                            \n" +
				"tagged class DynChar [case of Dyn]                          \n" +
				"    class def create() : DynChar                            \n" +
				"        new                                                 \n" +
				"                                                            \n" +
				"tagged class DynByte [case of Dyn]                          \n" +
				"    class def create() : DynByte                            \n" +
				"        new                                                 \n" +
				"                                                            \n" +
				"val i = DynInt.create()                                     \n" +
				"                                                            \n" +
				"match(i):                                                   \n" +
				"	DynInt => 10                                             \n" +
				"	DynChar => 15                                            \n" +
				"	DynByte => 25                                            \n" +
				"	default => 15                                            \n";
			// default specified with exhaustive search; error
				
			//ErrorMessage.DEFAULT_PRESENT
				
			Assert.fail("TODO");
	}
	
	public void caseOfParseTest() {
		String input = 
			"tagged class Dyn                            \n" +
			"    class def create() : X                  \n" +
			"        new                                 \n" +
			"                                            \n" +
			"tagged class DynInt [case of Dyn]           \n" +
			"    class def create() : DynInt             \n" +
			"        new                                 \n" +
			"                                            \n" +
			"tagged class DynChar [case of Dyn]          \n" +
			"    class def create() : DynChar            \n" +
			"        new                                 \n" +
			"                                            \n" +
			"val i = DynInt.create()                     \n" +
			"                                            \n" +
			"match(i):                                   \n" +
			"	DynInt => 10                             \n" +
			"	DynChar => 15                            \n" +
			"	default => 23                            \n";
		
		Assert.fail("TODO");
	}
	
	public void comprisesParseTest() {
		String input = 
			"tagged class Dyn [comprises DynInt, DynChar] \n" +
			"    class def create() : X                   \n" +
			"        new                                  \n" +
			"                                             \n" +
			"tagged class DynInt [case of Dyn]            \n" +
			"    class def create() : DynInt              \n" +
			"        new                                  \n" +
			"                                             \n" +
			"tagged class DynChar [case of Dyn]           \n" +
			"    class def create() : DynChar             \n" +
			"        new                                  \n" +
			"                                             \n" +
			"val i = DynInt.create()                      \n" +
			"                                             \n" +
			"match(i):                                    \n" +
			"	DynInt => 10                              \n" +
			"	DynChar => 15                             \n";
		// Default not needed here because match is exhaustive
		
		Assert.fail("TODO");
	}
	
	public void jsonTest() {
		//TODO, make this test do something useful!
		
		String input = 
				"tagged class JSON                            \n" +
				"    class def create() : X                   \n" +
				"        new                                  \n" +
				"                                             \n" +
				"tagged class ValueJSON [case of JSON]        \n" +
				"    class def create() : ValueJSON           \n" +
				"        new                                  \n" +
				"                                             \n" +
				"tagged class ObjJSON [case of JSON]          \n" +
				"    class def create() : ObjJSON             \n" +
				"        new                                  \n" +
				"                                             \n" +
				"tagged class StrJSON [case of JSON]          \n" +
				"    class def create() : StrJSON             \n" +
				"        new                                  \n" +
				"                                             \n" +
				"tagged class NumJSON [case of JSON]          \n" +
				"    class def create() : NumJSON             \n" +
				"        new                                  \n" +
				"                                             \n" +
				"tagged class IntJSON [case of NumJSON]       \n" +
				"    class def create() : IntJSON             \n" +
				"        new                                  \n" +
				"                                             \n" +
				"tagged class DoubleJSON [case of NumJSON]    \n" +
				"    class def create() : DoubleJSON          \n" +
				"        new                                  \n" +
				"                                             \n" +
				"val json = DoubleJSON.create()               \n" +
				"                                             \n" +
				"match(i):                                    \n" +
				"	ObjJSON => 10                             \n" +
				"	StrJSON => 15                             \n" + 
				"	default => 15                             \n";
			
			Assert.fail("TODO");
	}
}
