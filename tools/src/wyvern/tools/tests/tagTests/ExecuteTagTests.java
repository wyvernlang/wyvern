package wyvern.tools.tests.tagTests;

import java.io.IOException;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import wyvern.stdlib.Globals;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.interfaces.TypedAST;

import static wyvern.tools.tests.tagTests.TestUtil.getAST;
import static wyvern.tools.tests.tagTests.TestUtil.evaluateExpecting;

public class ExecuteTagTests {
	
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
			"val i  = Dyn.create()                         \n" +
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
}
