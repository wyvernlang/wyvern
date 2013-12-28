package wyvern.tools.bytecode.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import wyvern.tools.bytecode.values.BytecodeValue;

public class TestClass extends TestUtil {

	@Test
	public void defineSimpleClass() {
		
		PRINTS_ON = true;
				
/*		String s =	"class Hello\n"
				+	"	class def make():Hello = new\n"
				+	"	var testVal:Int = 5\n"
				+	"	def setV(n : Int):Unit = this.testVal = n\n"
				+	"	def getV():Int = this.testVal\n";*/
		
		String s = " class X                  \n"
				+"   class def create() : X   \n"
				+"     new                    \n"
				+"   val x:Int = 2            \n"
				+"   def getX():Int           \n"
				+"     this.x                 \n";
				//+" val tX : X = X.create()    \n"
				//+" tX.getX()                  \n";

		BytecodeValue res = runTest(s);
		
		String[] names = {  };
		BytecodeValue[] vals = {  };
		assertTrue(isInContext(names,vals));
		assertEquals(res.toString(), "No");	
	}	

}
