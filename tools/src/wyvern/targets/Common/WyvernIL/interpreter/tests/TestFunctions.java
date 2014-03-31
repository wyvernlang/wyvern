package wyvern.targets.Common.wyvernIL.interpreter.tests;

import org.junit.Test;
import wyvern.targets.Common.wyvernIL.interpreter.values.BytecodeInt;
import wyvern.targets.Common.wyvernIL.interpreter.values.BytecodeValue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * a test for the function related elements of the interpreter
 * see TestUtil for setup teardown and helper functions
 * @author Tal Man
 *
 */
public class TestFunctions extends TestUtil {
	
	@Test
	public void defineFunction() {
		
		PRINTS_ON = false;
		
		s =	"val fun1 = fn x : Int => x + 1 						\n"
		+ 	"val fun2 = fn f : Int -> Int => fn x : Int => f(f(x))	\n"
		+ 	"def fun3(z : Int) : Int								\n"
		+	"  z+1 													\n";

		BytecodeValue res = runTest(s);	
		assertEquals(res.toString(), "()");	

		String[] names = { "fun1", "fun2", "fun3" };
		BytecodeValue[] vals = { func, func, func };
		assertTrue(isInContext(names,vals)); 
	}
	
	@Test
	public void invokeFunction() {
		
		PRINTS_ON = false;
		
		s =	"def mult(n:Int,m:Int):Int = n+5*m 		\n"
		+	"val x = mult(3,2) 						\n"
		+ 	"val y = mult(4,5) 						\n"
		+ 	"val z = mult(1,1)						\n";

		BytecodeValue res = runTest(s);	
		assertEquals(res.toString(), "()");

		String[] names = { "x", "y", "z", "mult" };
		BytecodeValue[] vals = { new BytecodeInt(13), new BytecodeInt(29),
								 new BytecodeInt(6), func };
		assertTrue(isInContext(names,vals));
	}
	
	@Test
	public void invokeFunction2() {
		
		PRINTS_ON = false;
		
		s =	"val x = (fn x : Int => x + 1)(3)";

		BytecodeValue res = runTest(s);
		assertEquals(res.toString(), "()");	

		String[] names = { "x" };
		BytecodeValue[] vals = { new BytecodeInt(4) };
		assertTrue(isInContext(names,vals));
	}
	
	@Test
	public void invokeFunction3() {
		
		PRINTS_ON = false;
		
		s =	"var x : Int = 3 						\n"
		+ 	"def mult(n:Int,m:Int):Int = (n+m)*x 	\n"
		+ 	"mult(1,2)								\n";

		BytecodeValue res = runTest(s);	
		assertEquals(res.toString(), "9");

		String[] names = { "x", "mult" };
		BytecodeValue[] vals = { new BytecodeInt(3) , func };
		assertTrue(isInContext(names,vals));
	}
	
	@Test
	public void invokeFunction4() {
		
		PRINTS_ON = false;
		
		s =	"var x : Int = 3				\n"
		+ 	"def mult(n:Int,m:Int):Int		\n"
		+ 	" x = x + 1 					\n"
		+ 	" n * m 						\n"
		+ 	"mult(1,2)						\n";

		BytecodeValue res = runTest(s);	
		assertEquals(res.toString(), "2");

		String[] names = { "x", "mult" };
		BytecodeValue[] vals = { new BytecodeInt(4) , func };
		assertTrue(isInContext(names,vals));
	}
	
	@Test
	public void invokeFunction5() {
		
		PRINTS_ON = false;
		
		s =	"var x : Int = 3 				\n"
		+ 	"def mult(n:Int,m:Int):Int 		\n"
		+ 	" val y = 4 					\n"
		+ 	" n * m 						\n"
		+ 	"mult(1,2)						\n";

		BytecodeValue res = runTest(s);	
		assertEquals(res.toString(), "2");

		String[] names = { "x", "mult" };
		BytecodeValue[] vals = { new BytecodeInt(3) , func };
		assertTrue(isInContext(names,vals));
	}
	
	@Test
	public void functionCallsFunction() {
		
		PRINTS_ON = false;
		
		s =	"def add(x:Int,y:Int):Int 		\n"
		+ 	"	x + y 						\n"
		+ 	"def mult(n:Int,m:Int):Int 		\n"
		+ 	"	var z : Int = m 			\n"
		+ 	"	var sum : Int = 0 			\n"
		+ 	"	while z > 0 				\n"
		+ 	"		sum = add(n,sum) 		\n"
		+ 	"		z = z - 1 				\n"
		+ 	"	sum 						\n"
		+ 	"mult(5,5)						\n";

		BytecodeValue res = runTest(s);	
		assertEquals(res.toString(), "25");

		String[] names = { "add", "mult" };
		BytecodeValue[] vals = { func , func };
		assertTrue(isInContext(names,vals));
	}
	
	
	@Test
	public void nestedFunction() {
		
		PRINTS_ON = false;
		
		s =	"def mult(n:Int,m:Int):Int 		\n"
		+ 	"	def add(x:Int,y:Int):Int 	\n"
		+ 	"		x + y 					\n"
		+ 	"	var z : Int = m 			\n"
		+ 	"	var sum : Int = 0 			\n"
		+ 	"	while z > 0 				\n"
		+ 	"		sum = add(n,sum) 		\n"
		+ 	"		z = z - 1 				\n"
		+ 	"	sum 						\n"
		+ 	"mult(3,6)						\n";

		BytecodeValue res = runTest(s);	
		assertEquals(res.toString(), "18");

		String[] names = { "mult" };
		BytecodeValue[] vals = { func };
		assertTrue(isInContext(names,vals));
	}
	
	@Test
	public void recursiveFunction() {
		
		PRINTS_ON = false;
						
		s =	"def factorial(n:Int):Int 	\n" 
		+	" if n==1 					\n"
		+	"  then 					\n"
		+	"   n 						\n"
		+	"  else 					\n"
		+	"   n * factorial(n-1) 		\n"
		+ 	"factorial(5)				\n";

		BytecodeValue res = runTest(s);	
		assertEquals(res.toString(), "120");

		String[] names = { "factorial" };
		BytecodeValue[] vals = { func };
		assertTrue(isInContext(names,vals));
	}
	
	@Test
	public void funcReturnsTupleDef() {

		PRINTS_ON = false;

		s = "def func() : Int*Int*Int	\n"
		+ 	"	(1,2,3)					\n";

		BytecodeValue res = runTest(s);
		assertEquals(res.toString(), "()");

		String[] names = { "func" };
		BytecodeValue[] vals = { func };
		assertTrue(isInContext(names, vals));
	}
	
	@Test
	public void changeUnsetVar() {

		PRINTS_ON = false;

		s = "var x : Int					\n"
		+ 	"def setX(num : Int) : Unit		\n"
		+ 	"	x = num						\n";

		BytecodeValue res = runTest(s);
		assertEquals(res.toString(), "()");

		String[] names = { "x", "setX" };
		BytecodeValue[] vals = { empty, func };
		assertTrue(isInContext(names, vals));
	}
}
