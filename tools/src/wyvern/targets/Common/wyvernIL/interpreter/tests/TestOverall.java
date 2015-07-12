package wyvern.targets.Common.wyvernIL.interpreter.tests;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import wyvern.targets.Common.wyvernIL.interpreter.values.BytecodeValue;
import wyvern.tools.tests.suites.RegressionTests;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
	
	@Test
	public void linkedList() {

		PRINTS_ON = false;
		
		s =	List 
		+"val l : IntList = IntList.create()								\n"
		+"l.insert(3,0)														\n"
		+"l.insert(5,1)														\n"
		+"l.insert(1,2)														\n"
		+"l.insert(0,3)														\n"
		+"l.insert(9,4)														\n"
		+"l.insert(2,2)														\n"
		+"l.insert(6,0)														\n"
		+"l.remove(3)														\n"
		+"l.insert(13,0)													\n"
		+"l.remove(0)														\n"
		+"(l.get(0),l.get(1),l.get(2),l.get(3),l.get(4),l.get(5))			\n";

		BytecodeValue res = runTest(s);
		assertEquals(res.toString(), "(6,3,5,1,0,9)");

		String[] names = { "Node", "DummyNode", "IntList", "l" };
		BytecodeValue[] vals = { clasDef, clasDef, clasDef, clas };
		assertTrue(isInContext(names, vals));
	}
	
	@Test
	public void bubbleSortList() {

		PRINTS_ON = false;
		
		s =	List 
		+"def bubbleSortList(list : IntList) : Unit        	\n"
		+"	var size : Int = list.getSize()                	\n"
		+"	var lastIndex : Int = size                     	\n"
		+"	var i : Int = 0                                	\n"
		+"	while(i < size)                                	\n"
		+"		var j : Int = 0 		                   	\n"
		+"		i = i + 1                                  	\n"
		+"		lastIndex = lastIndex - 1                  	\n"
		+"		while(j < lastIndex)                       	\n"
		+"			if list.get(j) > list.get(j + 1)       	\n"
		+"				then                               	\n"
		+"					var value : Int = list.get(j)  	\n"
		+"					list.remove(j)                 	\n"
		+"					list.insert(value,j+1)         	\n"
		+"				else                               	\n"
		+"					true                           	\n"
		+"			j = j + 1                              	\n"
		+"													\n"
		+"val l : IntList = IntList.create()				\n"
		+"l.insert(6,0)										\n"
		+"l.insert(2,1)										\n"
		+"l.insert(7,2)										\n"
		+"l.insert(0,3)										\n"
		+"l.insert(1,4)										\n"
		+"bubbleSortList(l)									\n"
		+"(l.get(0),l.get(1),l.get(2),l.get(3),l.get(4))	\n";

		BytecodeValue res = runTest(s);
		assertEquals(res.toString(), "(0,1,2,6,7)");

		String[] names = { "Node", "DummyNode", "IntList", "l", "bubbleSortList" };
		BytecodeValue[] vals = { clasDef, clasDef, clasDef, clas, func };
		assertTrue(isInContext(names, vals));
	}
	
	@Test
	public void reverseList() {

		PRINTS_ON = false;
		
		s =	List 
		+"def reverseList(list : IntList) =									\n"
		+"	var last : Int = list.getSize() - 1								\n"
		+"	var first : Int = 0												\n"
		+"	while (last > first) 											\n"
		+"		var firstVal : Int = list.get(first)						\n"
		+"		var lastVal : Int = list.get(last)							\n"
		+"		list.remove(first)											\n"
		+"		list.remove(last - 1)										\n"
		+"		list.insert(lastVal,first)									\n"
		+"		list.insert(firstVal,last)									\n"
		+"		last = last - 1												\n"
		+"		first = first + 1											\n"
		+"																	\n"	
		+"val l : IntList = IntList.create()								\n"
		+"l.insert(3,0)														\n"
		+"l.insert(5,1)														\n"
		+"l.insert(1,2)														\n"
		+"l.insert(0,3)														\n"
		+"l.insert(9,4)														\n"
		+"l.insert(6,5)														\n"
		+"l.insert(8,6)														\n"
		+"reverseList(l)													\n"
		+"(l.get(0),l.get(1),l.get(2),l.get(3),l.get(4),l.get(5),l.get(6))	\n";
		
		BytecodeValue res = runTest(s);
		assertEquals(res.toString(), "(8,6,9,0,1,5,3)");

		String[] names = { "l", "reverseList" };
		BytecodeValue[] vals = { clas, func };
		assertTrue(isInContext(names, vals));
	}
	
	@Category(RegressionTests.class)
	@Test 
	public void modulus() {

		PRINTS_ON = false;
		
		s =	"def mod(num : Int, over : Int)	: Int		\n"
		+ 	"	num - (over * (num / over))				\n"
		+ 	"val x1 = mod(17,3)							\n"
		+ 	"val x2 = mod(12,2)							\n"
		+ 	"val x3 = mod(5,15)							\n"
		+ 	"val x4 = mod(21,6)							\n"
		+ 	"val x5 = mod(12,10)						\n"
		+ 	"val x6 = mod(123,10)						\n"
		+ 	"(x1,x2,x3,x4,x5,x6)						\n";
		
		BytecodeValue res = runTest(s);
		assertEquals(res.toString(), "(2,0,5,3,2,3)");

		String[] names = { "mod" };
		BytecodeValue[] vals = { func };
		assertTrue(isInContext(names, vals));
	}

	@Test
	public void reverser() {

		PRINTS_ON = false;
		
		s =	Mod 
		+ 	"class Reverser										\n"
		+ 	"	class def create() : Reverser 					\n"
		+ 	"		val reverser : Reverser = new				\n"
		+ 	"		reverser									\n"
		+ 	"	def reverseInt(number : Int) : Int				\n"
		+ 	"		var i : Int = 1								\n"
		+ 	"		var oldNum : Int = number					\n"
		+ 	"		var newNum : Int = 0						\n"
		+ 	"		while(oldNum > 0)							\n"
		+ 	"			i = i * 10								\n"
		+ 	"			oldNum = oldNum / 10					\n"
		+ 	"		i = i / 10									\n"
		+ 	"		oldNum = number								\n"
		+ 	"		while(oldNum > 0)							\n"
		+ 	"			newNum = newNum + i * mod(oldNum,10)	\n"
		+ 	"			oldNum = oldNum / 10					\n"
		+ 	"			i = i / 10								\n"
		+ 	"		newNum										\n"
		+ 	"													\n"
		+ 	"val r : Reverser = Reverser.create()				\n"
		+ 	"r.reverseInt(123456)								\n";

		BytecodeValue res = runTest(s);
		assertEquals(res.toString(), "654321");

		String[] names = { "Reverser", "r" };
		BytecodeValue[] vals = { clasDef, clas };
		assertTrue(isInContext(names, vals));
	}
	

	@Category(RegressionTests.class)
	@Test
	public void higherOrder() {

		PRINTS_ON = false;
		
		s =	"val applyTwice : (Int->Int)->(Int->Int) = fn f : Int -> Int => fn x : Int => f(f(x))	\n"
		+	"val addOne : (Int->Int) = fn x : Int => x + 1                                  		\n"
		+	"applyTwice(addOne)(1)                                                     				\n";

		BytecodeValue res = runTest(s);
		assertEquals(res.toString(), "3");

		String[] names = { "applyTwice", "addOne" };
		BytecodeValue[] vals = { func, func };
		assertTrue(isInContext(names, vals));
	}
	
	@Test
	public void mapTest() {
		
		PRINTS_ON = false;
		
		s =	List
		+	"def map(func : Int->Int, list : IntList)			\n"
		+ 	"	var i : Int = 0									\n"
		+ 	"	val size = list.getSize()						\n"
		+ 	"	while(i < size)									\n"
		+ 	"		val value : Int = list.get(i)				\n"
		+ 	"		list.remove(i)								\n"
		+ 	"		list.insert(func(value),i)					\n"
		+ 	"		i = i + 1									\n"
		+ 	"													\n"
		+ 	"val l : IntList = IntList.create()					\n"
		+ 	"l.insert(1,0)										\n"
		+ 	"l.insert(2,1)										\n"
		+ 	"l.insert(3,2)										\n"
		+ 	"l.insert(4,3)										\n"
		+ 	"def square(x : Int) : Int = x * x					\n"
		+ 	"map(square, l)										\n"
		+ 	"(l.get(0),l.get(1),l.get(2),l.get(3))				\n";

		BytecodeValue res = runTest(s);
		assertEquals(res.toString(), "(1,4,9,16)");

		String[] names = { "map", "l", "square" };
		BytecodeValue[] vals = { func, clas, func };
		assertTrue(isInContext(names, vals));		
	}
}
