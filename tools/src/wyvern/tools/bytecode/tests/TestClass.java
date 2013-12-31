package wyvern.tools.bytecode.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import wyvern.tools.bytecode.values.BytecodeValue;

public class TestClass extends TestUtil {
	
	@Test 
	public void defineSimpleClass2() {
							
		String s =	"class Hello									\n"
				+	"	class def make():Hello = new				\n"
				+	"	var testVal:Int = 5							\n"
				+	"	def setV(n : Int):Unit = this.testVal = n	\n"
				+	"	def getV():Int = this.testVal				\n";
		
		BytecodeValue res = runTest(s);
		
		String[] names = { "Hello" };
		BytecodeValue[] vals = { clasDef };
		assertTrue(isInContext(names,vals));
		assertEquals(res.toString(), "()");	
	}
	
	@Test
	public void defineSimpleClassAndInstance() {
		
		PRINTS_ON = true;

		String s = " class X                  \n"
				+"   class def create() : X   \n"
				+"     new                    \n"
				+"   val x:Int = 2            \n"
				+"   def getX():Int           \n"
				+"     this.x                 \n"		
				+" val tX : X = X.create()    \n"
				+" tX.getX()                  \n";

		BytecodeValue res = runTest(s);
		
		String[] names = { "X", "tX" };
		BytecodeValue[] vals = { clasDef, clas };
		assertTrue(isInContext(names,vals));
		assertEquals(res.toString(), "()");	
	}	

}
