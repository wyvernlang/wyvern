package wyvern.targets.Common.wyvernIL.interpreter.tests;

import org.junit.Test;
import wyvern.targets.Common.wyvernIL.interpreter.values.BytecodeBoolean;
import wyvern.targets.Common.wyvernIL.interpreter.values.BytecodeInt;
import wyvern.targets.Common.wyvernIL.interpreter.values.BytecodeString;
import wyvern.targets.Common.wyvernIL.interpreter.values.BytecodeValue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * this is a test for known bugs in the COMPILER (not the interpreter)
 * @author Tal Man
 *
 */
public class TestKnownBugs extends TestUtil {

	/*
	 * the compiler currently doesn't support negative numbers
	 */
	@Test
	public void negativeNumbersTest() {
		
		PRINTS_ON = false;
				
		s = "var x : Int = -20 	\n"
		+ 	"x					\n";
		
		BytecodeValue res = runTest(s);	
		assertEquals(res.toString(), "-20");

		String[] names = { "x" };
		BytecodeValue[] vals = { new BytecodeInt(3), new BytecodeInt(-20) };
		assertTrue(isInContext(names,vals));
	}
	
	/*	 
	 * 	This test fails due to a known bug in the compiler, basically when
	 *  create() sets x = 4 it does not set the var x to be 4 but instead
	 *  creates a new val x = 4 which then can't be changed and crashes on 
	 *  setX() when we try to change it.
	 */
	@Test 
	public void newInstantiationsTest() {
			
		PRINTS_ON = false;
								
		String s =	"class X					\n"
				+ 	"	var x : Int				\n"
				+ 	"	class def create() : X	\n"
				+ 	"		new 				\n"
				+ 	"			x = 4			\n"
				+ 	"	def setX() : Unit		\n"
				+ 	"		this.x = 2			\n"
				+ 	"	def getX() : Int		\n"
				+ 	"		this.x				\n"
				+ 	"val y : X = X.create()		\n"
				+ 	"val xBefore = y.getX()		\n"
				+ 	"y.setX()					\n"
				+ 	"val xAfter = y.getX()		\n";
			
		BytecodeValue res = runTest(s);
		assertEquals(res.toString(), "()");
		
		String[] names = { "Hello", "h", "a", "aBefore", "aAfter" };
		BytecodeValue[] vals = { clasDef, clas, new BytecodeInt(2), 
				new BytecodeInt(4), new BytecodeInt(2) };
		assertTrue(isInContext(names,vals));	
	}
	
	/*
	 * when i put another closing parenthesis on the tuple it goes into an
	 * infinite loop
	 */
	@Test
	public void tupleParenthesis() {
		
		PRINTS_ON = false;
				
		s = "val x = (1,2,3))";
		
		BytecodeValue res = runTest(s);	
		assertEquals(res.toString(), "-20");

		String[] names = { "x" };
		BytecodeValue[] vals = { new BytecodeInt(3), new BytecodeInt(-20) };
		assertTrue(isInContext(names,vals));
	}
	
	/*
	 * when instantiating it seems that the type used to instantiate wins over
	 * the specified type, ie, i can write var x : Int = false and it will be 
	 * instantiated as a Bool value and not as an Int, but if i'll do something
	 * like var x : Int = 3, x = false, then it will fail - ie this is only in
	 * instantiation
	 * this test succeeds when it shouldn't ever compile
	 */
	@Test
	public void badTypeInInstantiation() {
		
		PRINTS_ON = false;
		
		s =	"val a : Bool = 8			\n"
		+ 	"var b : Bool = 6			\n"
		+ 	"var c : Str = false		\n"
		+ 	"var d : Int = \"hello\"	\n"
		+ 	"val e : Int -> Str = 3		\n";

		BytecodeValue res = runTest(s);
		assertEquals(res.toString(), "()");

		String[] names = { "a", "b", "c", "d", "e" };
		BytecodeValue[] vals = { new BytecodeInt(8), new BytecodeInt(6),
				new BytecodeBoolean(false), new BytecodeString("hello"), 
				new BytecodeInt(3)};
		assertTrue(isInContext(names, vals));		
	}
	
	/*
	 * this compiles and crashes in the interpreter, when really it shouldn't
	 * compile at all (i'm assigning a new value into a val)
	 */
	@Test
	public void assignmentIntoVal() {
		
		PRINTS_ON = false;
		
		s =	"val a : Int = 3		\n"
		+ 	"a = 5					\n"
		+ 	"a						\n";

		BytecodeValue res = runTest(s);
		assertEquals(res.toString(), "5");

		String[] names = { };
		BytecodeValue[] vals = { };
		assertTrue(isInContext(names, vals));		
	}
}
