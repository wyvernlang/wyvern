package wyvern.tools.tests.tagTests;

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
		
		getAST(input);
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
		
		getAST(input);
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
		
		getAST(input);
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
		
		getAST(input);
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
				
		TypedAST ast = getAST(input);
		evaluateExpecting(ast, 15);
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
				
		getAST(input);
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
				
		getAST(input);
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
				
		
		TypedAST ast = getAST(input);
		evaluateExpecting(ast, 15);
	}
	
	@Test
	public void matchInterpretTest2() throws CopperParserException, IOException {		
		String input = 
				"tagged class Z                 \n" +
				"    class def create() : Z     \n" +
				"        new                    \n" +
				"                               \n" +
				"tagged class Y                 \n" +
				"    class def create() : Y     \n" +
				"        new                    \n" +
				"                               \n" +
				"tagged class W                 \n" +
				"    class def create() : W     \n" +
				"        new                    \n" +
				"                               \n" +
				"tagged class V                 \n" +
				"    class def create() : V     \n" +
				"        new                    \n" +
				"                               \n" +
				"tagged class U                 \n" +
				"    class def create() : U     \n" +
				"        new                    \n" +
				"                               \n" +
				"tagged class X                 \n" +
				"    class def create() : X     \n" +
				"        new                    \n" +
				"                               \n" +
				"val x = X.create()             \n" +
				"                               \n" +
				"match(x):                      \n" +
				"	X => 1                     \n" +
				"	Y => 2                     \n" +
				"	Z => 3                     \n" +
				"	W => 4                     \n" +
				"	U => 5                     \n" +
				"	V => 6                     \n" +
				"	default => 15               \n";
		
		TypedAST ast = getAST(input);
		evaluateExpecting(ast, 1);
	}
	
	@Test
	public void matchInterpretTest3() throws CopperParserException, IOException {
		String input = 
				"tagged class X                 \n" +
				"    class def create() : X     \n" +
				"        new                    \n" +
				"                               \n" +
				"tagged class Y                 \n" +
				"    class def create() : Y     \n" +
				"        new                    \n" +
				"                               \n" +
				"val y = Y.create()             \n" +
				"                               \n" +
				"match(y):                      \n" +
				"	X => 15                     \n" +
				"	Y => 23                     \n" +
				"	default => 50               \n";
		
		TypedAST ast = getAST(input);
		evaluateExpecting(ast, 23);
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
				
		TypedAST res = getAST(input);
		
		typeCheckfailWith(res, ErrorMessage.DUPLICATE_TAG);
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
				
		TypedAST res = getAST(input);
		
		typeCheckfailWith(res, ErrorMessage.UNKNOWN_TAG);
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
				"    class def create() : Z     \n" +
				"        new                    \n" +
				"                               \n" +
				"val x = X.create()             \n" +
				"                               \n" +
				"match(x):                      \n" +
				"	X => 15                     \n" +
				"	Y => 23                     \n" +
				"	Z => 34                     \n" +	// Z is declared but not a tagged type; error
				"	default => 50               \n";
				
		TypedAST ast = getAST(input);
		
		typeCheckfailWith(ast, ErrorMessage.NOT_TAGGED);
	}
	
	@Test
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
				"	default => 23               \n" +
				"	Y => 50                     \n";
				
		TypedAST res = getAST(input);
		
		typeCheckfailWith(res, ErrorMessage.DEFAULT_NOT_LAST);
	}
	
	@Test
	public void multipleDefaultsTest() throws CopperParserException, IOException {
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
				"	default => 23               \n" +
				"	Y => 50                     \n" +
				"	default => 23               \n";
				
		TypedAST res = getAST(input);
		
		typeCheckfailWith(res, ErrorMessage.MULTIPLE_DEFAULTS);
	}
	
	@Test
	public void nonExhaustiveErrorTest() throws CopperParserException, IOException {
		String input = 
			"tagged class Dyn [comprises DynInt, DynChar, DynByte]       \n" +
			"    class def create() : Dyn                                \n" +
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
			"val i = Dyn.create()                                        \n" +
			"                                                            \n" +
			"match(i):                                                   \n" +
			"	DynInt => 10                                             \n" +
			"	DynChar => 15                                            \n";
		// DynByte not specified; error
		
		TypedAST res = getAST(input);
	
		typeCheckfailWith(res, ErrorMessage.DEFAULT_NOT_PRESENT);
	}
	
	@Test
	public void exhaustiveWithDefaultTest() throws CopperParserException, IOException {
		String input = 
				"tagged class Dyn [comprises DynInt, DynChar, DynByte]       \n" +
				"    class def create() : Dyn                                \n" +
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
				"val i = Dyn.create()                                        \n" +
				"                                                            \n" +
				"match(i):                                                   \n" +
				"	DynInt => 10                                             \n" +
				"	DynChar => 15                                            \n" +
				"	DynByte => 20                                            \n" +
				"   Dyn     => 25                                            \n" +
				"	default => 30                                            \n";
			// default specified with exhaustive search; error
				
			TypedAST ast = getAST(input);
			typeCheckfailWith(ast, ErrorMessage.DEFAULT_PRESENT);
	}
	
	@Test
	public void caseOfParseTest() throws CopperParserException, IOException {
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
		
		getAST(input);
		//reaching here without a parse exception is a pass
	}
	
	@Test
	public void comprisesParseTest() throws CopperParserException, IOException {
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
			"	DynChar => 15                             \n" +
			"	default => 15                             \n";
		
		
		getAST(input);
		//reaching here without a parse exception is a pass
	}
	
	@Test
	public void defaultPresentFullComprisesTest() throws CopperParserException, IOException {
		//Checks that an error is caught when a default is included but all comprises tags are included
		
		String input = 
			"tagged class Dyn [comprises DynInt, DynChar] \n" +
			"    class def create() : Dyn                 \n" +
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
			"val i = Dyn.create()                         \n" +
			"                                             \n" +
			"match(i):                                    \n" +
			"	DynInt => 10                              \n" +
			"	DynChar => 15                             \n" +
			"	Dyn => 5                                  \n" +
			"	default => 20                             \n";
		
		
		TypedAST res = getAST(input);
		
		typeCheckfailWith(res, ErrorMessage.DEFAULT_PRESENT);
	}
	
	@Test
	public void comprisesExecTest() throws CopperParserException, IOException {
		String input = 
			"tagged class Dyn [comprises DynInt, DynChar] \n" +
			"    class def create() : Dyn                 \n" +
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
			"val i = Dyn.create()                         \n" +
			"                                             \n" +
			"match(i):                                    \n" +
			"	DynInt => 10                              \n" +
			"	DynChar => 15                             \n" +
			"	default => 15                             \n";
		
		TypedAST ast = getAST(input);
		
		// Should be 15 because D is a subclass of B and will match that
		evaluateExpecting(ast, 15);
	}
	
	@Test
	public void subtagAfterSupertagTest1() throws CopperParserException, IOException {
		String input = 
			"tagged class A                               \n" +
			"    class def create() : A                   \n" +
			"        new                                  \n" +
			"                                             \n" +
			"tagged class B [case of A]                   \n" +
			"    class def create() : B                   \n" +
			"        new                                  \n" +
			"                                             \n" +
			"tagged class C [case of A]                   \n" +
			"    class def create() : C                   \n" +
			"        new                                  \n" +
			"                                             \n" +
			"tagged class D [case of B]                   \n" +
			"    class def create() : D                   \n" +
			"        new                                  \n" +
			
			"val d = D.create()                           \n" +
			"                                             \n" +
			"match(d):                                    \n" +
			"	A => 15                                   \n" +	//Error, this will catch everything
			"	B => 15                                   \n" + //So anything after is pointless
			"	C => 25                                   \n" +
			"	default => 35                             \n";
		
		TypedAST ast = getAST(input);
		
		typeCheckfailWith(ast, ErrorMessage.SUPERTAG_PRECEEDS_SUBTAG);
	}
	
	@Test
	public void subtagAfterSupertagTest2() throws CopperParserException, IOException {
		String input = 
			"tagged class A [comprises B, C]              \n" +
			"    class def create() : A                   \n" +
			"        new                                  \n" +
			"                                             \n" +
			"tagged class B [case of A]                   \n" +
			"    class def create() : B                   \n" +
			"        new                                  \n" +
			"                                             \n" +
			"tagged class C [case of A]                   \n" +
			"    class def create() : C                   \n" +
			"        new                                  \n" +
			"                                             \n" +
			"tagged class D [case of B]                   \n" +
			"    class def create() : D                   \n" +
			"        new                                  \n" +
			
			"val d = A.create()                           \n" +
			"                                             \n" +
			"match(d):                                    \n" +
			"	C => 15                                   \n" +	
			"	B => 15                                   \n" + //Error, this will catch the D just below
			"	D => 25                                   \n" + 
			"	A => 25                                   \n";
		
		TypedAST ast = getAST(input);
		
		typeCheckfailWith(ast, ErrorMessage.SUPERTAG_PRECEEDS_SUBTAG);
	}
	
	@Test
	public void simpleHierarchicalExecTest() throws CopperParserException, IOException {
		String input = 
			"tagged class A                               \n" +
			"    class def create() : A                   \n" +
			"        new                                  \n" +
			"                                             \n" +
			"tagged class B [case of A]                   \n" +
			"    class def create() : B                   \n" +
			"        new                                  \n" +
			"                                             \n" +
			"tagged class C [case of A]                   \n" +
			"    class def create() : C                   \n" +
			"        new                                  \n" +
			"                                             \n" +
			"tagged class D [case of B]                   \n" +
			"    class def create() : D                   \n" +
			"        new                                  \n" +
			
			"val d = D.create()                           \n" +
			"                                             \n" +
			"match(d):                                    \n" +
			"	B => 15                                   \n" +		//matches B since D is a subtag of B
			"	C => 25                                   \n" +
			"	default => 35                             \n";
		
		TypedAST ast = getAST(input);
		
		// Should be 15 because D is a subclass of B and will match that
		evaluateExpecting(ast, 15);
	}
	
	@Test
	public void jsonTest() throws CopperParserException, IOException {
		//TODO, make this test do something useful!
		
		String input = 
				"tagged class JSON                            \n" +
				"    class def create() : JSON                \n" +
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
				"val json = IntJSON.create()                  \n" +
				"                                             \n" +
				"match(json):                                 \n" +
				"	IntJSON => 25                             \n" +
				"	NumJSON => 10                             \n" + 
				"	ObjJSON => 15                             \n" +
				"	StrJSON => 20                             \n" + 
				"	default => 30                             \n";
			
			TypedAST ast = getAST(input);
			
			evaluateExpecting(ast, 25);
	}
	
	/**
	 * Attempts to typecheck the given AST and catch the given ErrorMessage.
	 * This error being thrown indicates the test passed.
	 * 
	 * If the error isn't thrown, the test fails.
	 * 
	 * @param ast
	 * @param errorMessage
	 */
	public static void typeCheckfailWith(TypedAST ast, ErrorMessage errorMessage) {
		try {
			ast.typecheck(Environment.getEmptyEnvironment(), Optional.empty());
		} catch (ToolError toolError) {
			Assert.assertEquals(errorMessage, toolError.getTypecheckingErrorMessage());
			
			return;
		}
		
		Assert.fail("Should have failed with error: " + errorMessage);
	}
	
	/**
	 * First typechecks the AST, then executes it.
	 * 
	 * Any returned value is discarded, but anything printed to stdout will be visible.
	 * 
	 * @param ast
	 */
	public static void evaluate(TypedAST ast) {
		ast.typecheck(Environment.getEmptyEnvironment(), Optional.empty());
		ast.evaluate(Environment.getEmptyEnvironment());
	}
	
	/**
	 * Completely evaluates the given AST, and compares it to the given value.
	 * Does typechecking first, then evaluation.
	 * 
	 * @param ast
	 * @param value
	 */
	public static void evaluateExpecting(TypedAST ast, int value) {
		ast.typecheck(Environment.getEmptyEnvironment(), Optional.empty());
		Value v = ast.evaluate(Environment.getEmptyEnvironment());
		
		String expecting = "IntegerConstant(" + value + ")"; 
		
		Assert.assertEquals(expecting, v.toString());
	}
	
	/**
	 * Converts the given program into the AST representation.
	 * 
	 * @param program
	 * @return
	 * @throws IOException 
	 * @throws CopperParserException 
	 */
	public static TypedAST getAST(String program) throws CopperParserException, IOException {
		return (TypedAST)new Wyvern().parse(new StringReader(program), "test input");
	}
}
