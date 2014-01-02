package wyvern.tools.bytecode.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import wyvern.tools.bytecode.values.BytecodeInt;
import wyvern.tools.bytecode.values.BytecodeValue;

public class TestOverall extends TestUtil {

	@Test
	public void SortFourTest() {

		PRINTS_ON = false;
		
		s =	"def sortFour(a:Int, b:Int, c:Int, d:Int) : Int*Int*Int*Int		\n"
		+ 	"	def max(x:Int, y:Int) : Int                             	\n"
		+ 	"		if x > y                                            	\n"
		+ 	"			then                                            	\n"
		+ 	"				x                                           	\n"
		+ 	"			else                                            	\n"
		+ 	"				y                                           	\n"
		+ 	"	def min(x:Int, y:Int) : Int                             	\n"
		+ 	"		if x < y                                            	\n"
		+ 	"			then                                            	\n"
		+ 	"				x                                           	\n"
		+ 	"			else                                            	\n"
		+ 	"				y                                         		\n"
		+ 	"	val x1 = min(min(a,b),min(c,d))                             \n"
		+ 	"	val x2 = min(min(max(a,b),max(c,d)),max(min(a,b),min(c,d))) \n"
		+ 	"	val x3 = max(min(max(a,b),max(c,d)),max(min(a,b),min(c,d))) \n"
		+	"	val x4 = max(max(a,b),max(c,d))                             \n"
		+ 	"	(x1,x2,x3,x4)												\n"
		+ 	"sortFour(60,5,23,41)											\n";

		BytecodeValue res = runTest(s);
		assertEquals(res.toString(), "(5,23,41,60)");

		String[] names = { "sortFour" };
		BytecodeValue[] vals = { func };
		assertTrue(isInContext(names, vals));
	}
	

}
