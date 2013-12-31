package wyvern.tools.bytecode.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import wyvern.tools.bytecode.values.BytecodeValue;

public class TestClass extends TestUtil {
/*	
	@Test 
	public void defineSimpleClassAndInstance() {
							
		String s =	"class Hello									\n"
				+	"	class def make():Hello = new				\n"
				+	"	var testVal:Int = 5							\n"
				+	"	def setV(n : Int):Unit = this.testVal = n	\n"
				+	"	def getV():Int = this.testVal				\n"
				+ 	"val hello : Hello = Hello.make() 				\n"
				+ 	"hello.setV(13)									\n"
				+ 	"hello.getV()									\n";
		
		BytecodeValue res = runTest(s);
		
		String[] names = { "Hello" };
		BytecodeValue[] vals = { clasDef };
		assertTrue(isInContext(names,vals));
		assertEquals(res.toString(), "13");	
	}

	@Test
	public void defineSimpleClassAndInstance2() {

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
		assertEquals(res.toString(), "2");	
	}	
	*/
	@Test
	public void uniqueThisTest() {
		
		PRINTS_ON = true;

		String s = "class X                        	\n"
				+"   val t:Int                      \n"
				+"   class def create(i:Int) : X    \n"
				+"       new                        \n"
				+"         t = i                    \n"
				+"   def get():Int                  \n"
				+"        this.t                    \n"
				+"val a = X.create(1).get           \n"
				+"val b = X.create(2).get           \n"
				+"b() + a()                         \n";

		BytecodeValue res = runTest(s);
		
		String[] names = { "X", "a", "b" };
		BytecodeValue[] vals = { clasDef, clas, clas };
		assertTrue(isInContext(names,vals));
		assertEquals(res.toString(), "3");	
	}	
	


}
