package wyvern.tools.bytecode.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import wyvern.tools.bytecode.values.BytecodeInt;
import wyvern.tools.bytecode.values.BytecodeValue;

public class TestClass extends TestUtil {

/*	@Test 
	public void defineSimpleClassAndInstance() {
		
		PRINTS_ON = false;
							
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
		
		PRINTS_ON = false;

		String s = 	" class X           	     \n"
				+	"   class def create() : X   \n"
				+	"     new                    \n"
				+	"   val x:Int = 2            \n"
				+	"   def getX():Int           \n"
				+	"     this.x                 \n"		
				+	" val tX : X = X.create()    \n"
				+	" tX.getX()                  \n";

		BytecodeValue res = runTest(s);
		
		String[] names = { "X", "tX" };
		BytecodeValue[] vals = { clasDef, clas };
		assertTrue(isInContext(names,vals));
		assertEquals(res.toString(), "2");	
	}	
	
	@Test
	public void uniqueThisTest() {
		
		PRINTS_ON = false;
				
		String s = 	"class X                      		\n"
			    +	"   val t:Int                      	\n"
			    +	"   class def create(i:Int) : X    	\n"
			    +	"       new                        	\n"
			    +	"         t = i                    	\n"
			    +	"   def get():Int                  	\n"
			    +	"        this.t                    	\n"
			    +	"val c : X = X.create(1)    		\n"
			    +	"val a:Unit->Int = c.get            \n"
			    +	"val b:Unit->Int = X.create(2).get  \n"
			    +	"b() + a()                         	\n";

		BytecodeValue res = runTest(s);
		
		String[] names = { "X", "c", "a", "b" };
		BytecodeValue[] vals = { clasDef, clas, func, func };
		assertTrue(isInContext(names,vals));
		assertEquals(res.toString(), "3");	
	}*/
	
	@Test
	public void simpleTest() {
		
		PRINTS_ON = true;
				
		String s = 	"val y:Int = 12 + 4          	\n"
				+	"class X                       	\n"
				+	"   val z:Int = y              	\n"
				+	"   def get():Int              	\n"
				+	"      this.z                  	\n"
				+	"   class def create() : X     	\n"
				+	"      new                     	\n"
				+	"X.create().get()              	\n";

		BytecodeValue res = runTest(s);
		
		String[] names = { "X", "y" };
		BytecodeValue[] vals = { clasDef, new BytecodeInt(16) };
		assertTrue(isInContext(names,vals));
		assertEquals(res.toString(), "16");	
	}
/*	
	@Test
	public void initTest() {
		
		PRINTS_ON = true;
				
		String s = 	"class X                       	\n"
				+	"   val z:Int = 4 * 2          	\n"
				+	"   def get():Int              	\n"
				+	"      this.z                  	\n"
				+	"   class def create() : X     	\n"
				+	"      new                     	\n"
				+	"X.create().get()              	\n";

		BytecodeValue res = runTest(s);
		
		String[] names = { "X" };
		BytecodeValue[] vals = { clasDef };
		assertTrue(isInContext(names,vals));
		assertEquals(res.toString(), "8");	
	}*/

}
