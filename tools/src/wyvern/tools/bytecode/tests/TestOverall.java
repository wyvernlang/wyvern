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

		String s = 	"var first : Int = 0												\n"
				+ 	"var second : Int = 0												\n"
				+ 	"var third : Int = 0												\n"
				+ 	"var fourth : Int = 0												\n"
				+ 	"def sortFour(a : Int, b : Int, c : Int, d : Int) : Unit        		\n"
				+ 	"	def max(x : Int, y : Int) : Int                                 \n"
				+ 	"		if x > y                                                    \n"
				+ 	"			then                                                    \n"
				+ 	"				x                                                   \n"
				+ 	"			else                                                    \n"
				+ 	"				y                                                   \n"
				+ 	"	def min(x : Int, y : Int) : Int                                 \n"
				+ 	"		if x < y                                                    \n"
				+ 	"			then                                                    \n"
				+ 	"				x                                                   \n"
				+ 	"			else                                                    \n"
				+ 	"				y                                                 	\n"
				+ 	"	first = min(min(a,b),min(c,d))                                	\n"
				+ 	"	second = min(min(max(a,b),max(c,d)),max(min(a,b),min(c,d)))   	\n"
				+ 	"	third = max(min(max(a,b),max(c,d)),max(min(a,b),min(c,d)))    	\n"
				+	"	fourth = max(max(a,b),max(c,d))                               	\n"
				+ 	"sortFour(60,5,23,41)												\n"
				+ 	"(first,second,third,fourth)										\n";

		BytecodeValue res = runTest(s);
		assertEquals(res.toString(), "(5,23,41,60)");

		String[] names = { "sortFour", "first", "second", "third", "fourth" };
		BytecodeValue[] vals = { func, new BytecodeInt(5), new BytecodeInt(23),
				new BytecodeInt(41), new BytecodeInt(60) };
		assertTrue(isInContext(names, vals));
	}
}
