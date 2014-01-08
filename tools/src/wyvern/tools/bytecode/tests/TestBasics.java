package wyvern.tools.bytecode.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import wyvern.tools.bytecode.values.BytecodeInt;
import wyvern.tools.bytecode.values.BytecodeString;
import wyvern.tools.bytecode.values.BytecodeTuple;
import wyvern.tools.bytecode.values.BytecodeValue;

/**
 * a test for the basic elements of the interpreter
 * see TestUtil for setup teardown and helper functions
 * @author Tal Man
 *
 */
public class TestBasics extends TestUtil {

	@Test
	public void assignTest() {
		
		PRINTS_ON = false;
				
		s = "var x : Int = 1 	\n"
		+ 	"x = 3 				\n"
		+ 	"val y = 5			\n";
		
		BytecodeValue res = runTest(s);	
		assertEquals(res.toString(), "()");

		String[] names = { "x", "y" };
		BytecodeValue[] vals = { new BytecodeInt(3), new BytecodeInt(5) };
		assertTrue(isInContext(names,vals));
	}
	
	@Test
	public void divideTest() {
		
		PRINTS_ON = false;
				
		s = "(15 / 4 / 2, 12 / 5, 10/11)";
		
		BytecodeValue res = runTest(s);	
		assertEquals(res.toString(), "(1,2,0)");

		String[] names = { };
		BytecodeValue[] vals = { };
		assertTrue(isInContext(names,vals));
	}
	
	@Test
	public void defnTest() {
		
		PRINTS_ON = false;

		s = "val x : Int = 2 + 2 					\n"
		+ 	"val y : Int = 2 * x 					\n"
		+ 	"val z : Str = \"Hello \" + \"World\"	\n"
		+	"val w = (x,y,z)						\n";

		BytecodeValue res = runTest(s);	
		assertEquals(res.toString(), "()");	
		
		String[] names = { "x", "y", "z", "w" };
		List<BytecodeValue> tuple = new ArrayList<BytecodeValue>();
		tuple.add(new BytecodeInt(4));
		tuple.add(new BytecodeInt(8));
		tuple.add(new BytecodeString("Hello World"));
		BytecodeValue[] vals = { new BytecodeInt(4), new BytecodeInt(8),
							     new BytecodeString("Hello World"), 
								 new BytecodeTuple(tuple)};
		assertTrue(isInContext(names,vals));	
	}

	@Test
	public void gotoLabelIfstmtTest() {
		
		PRINTS_ON = false;
		
		s =	"var x:Int = 5 		\n" 
		+	"var y:Int = 0 		\n" 
		+ 	"while x > 0 		\n" 
		+ 	"	x = x-1 		\n"
		+	"	y = y+1 		\n";
		
		BytecodeValue res = runTest(s);	
		assertEquals(res.toString(), "()");	

		String[] names = { "x", "y" };
		BytecodeValue[] vals = { new BytecodeInt(0), new BytecodeInt(5) };
		assertTrue(isInContext(names,vals));	
	}
	
	@Test
	public void gotoLabelIfstmtTest2() {
		
		PRINTS_ON = false;
				
		s =	"val x = if true	\n" 
		+ 	"	then 			\n" 
		+	"		1 			\n" 
		+	"	else 			\n" 
		+	"		2 			\n";

		BytecodeValue res = runTest(s);
		assertEquals(res.toString(), "()");	
		
		String[] names = { "x" };
		BytecodeValue[] vals = { new BytecodeInt(1) };
		assertTrue(isInContext(names,vals));
	}	
	
	@Test
	public void gotoLabelIfstmtTest3() {
		
		PRINTS_ON = false;
				
		s =	"val y = 16 		\n"
		+ 	"val x = 43 		\n"
		+ 	"if x > y 			\n" 
		+ 	"	then 			\n" 
		+	"		\"Yes\" 	\n" 
		+	"	else 			\n" 
		+	"		\"No\" 		\n";

		BytecodeValue res = runTest(s);
		assertEquals(res.toString(), "Yes");
		
		String[] names = {  };
		BytecodeValue[] vals = {  };
		assertTrue(isInContext(names,vals));
	}	
	
	@Test
	public void gotoLabelIfstmtTest4() {
		
		PRINTS_ON = false;
				
		s =	"val y = 16 			\n"
		+ 	"val x = 43 			\n"
		+ 	"if x > y && x < y 		\n" 
		+ 	"	then 				\n" 
		+	"		\"Yes\" 		\n" 
		+	"	else 				\n" 
		+	"		\"No\" 			\n";

		BytecodeValue res = runTest(s);
		assertEquals(res.toString(), "No");	
		
		String[] names = {  };
		BytecodeValue[] vals = {  };
		assertTrue(isInContext(names,vals));
	}	
	
	@Test
	public void gotoLabelIfstmtTest5() {
		
		PRINTS_ON = false;
				
		s =	"if false || true 	\n" 
		+ 	"	then 			\n" 
		+	"		\"Yes\" 	\n" 
		+	"	else 			\n" 
		+	"		\"No\" 		\n";

		BytecodeValue res = runTest(s);
		assertEquals(res.toString(), "Yes");	
		
		String[] names = {  };
		BytecodeValue[] vals = {  };
		assertTrue(isInContext(names,vals));
	}
	
	@Test
	public void ifAssignmentTest() {
		
		PRINTS_ON = false;
		
		s =	"var a : Int = 4	\n"
		+ 	"if a == 4		 	\n" 
		+ 	"	then 			\n" 
		+	"		a = 2	 	\n" 
		+	"	else 			\n" 
		+	"		a = 3 		\n";

		BytecodeValue res = runTest(s);
		assertEquals(res.toString(), "()");	
		
		String[] names = { "a" };
		BytecodeValue[] vals = { new BytecodeInt(2) };
		assertTrue(isInContext(names,vals));
		
	}
}
