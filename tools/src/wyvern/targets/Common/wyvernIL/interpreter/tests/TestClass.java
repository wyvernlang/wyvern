package wyvern.targets.Common.wyvernIL.interpreter.tests;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import wyvern.targets.Common.wyvernIL.interpreter.values.BytecodeInt;
import wyvern.targets.Common.wyvernIL.interpreter.values.BytecodeValue;
import wyvern.tools.tests.suites.RegressionTests;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestClass extends TestUtil {

	@Category(RegressionTests.class)
	@Test 
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
		assertEquals(res.toString(), "13");
		
		String[] names = { "Hello" };
		BytecodeValue[] vals = { clasDef };
		assertTrue(isInContext(names,vals));		
	}

	@Category(RegressionTests.class)
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
		assertEquals(res.toString(), "2");
		
		String[] names = { "X", "tX" };
		BytecodeValue[] vals = { clasDef, clas };
		assertTrue(isInContext(names,vals));
	}	
	
	@Test
	public void uniqueThisTest() {
		
		PRINTS_ON = false;
				
		String s = 	"class X                      		\n"
				+	"   class def create(i:Int) : X    	\n"
				+	"       new                        	\n"
				+	"         val t = i                    	\n"
			    +	"   val t:Int                      	\n"
			    +	"   def get():Int                  	\n"
			    +	"        this.t                    	\n"
			    +	"val c : X = X.create(1)    		\n"
			    +	"val a:Unit->Int = c.get            \n"
			    +	"val b:Unit->Int = X.create(2).get  \n"
			    +	"b() + a()                         	\n";

		BytecodeValue res = runTest(s);
		assertEquals(res.toString(), "3");	
		
		String[] names = { "X", "c", "a", "b" };
		BytecodeValue[] vals = { clasDef, clas, func, func };
		assertTrue(isInContext(names,vals));
	}
	
	@Category(RegressionTests.class)
	@Test
	public void simpleTest() {
		
		PRINTS_ON = false;
				
		String s = 	"val y:Int = 12 + 4          	\n"
				+	"class X                       	\n"
				+	"   class def create() : X     	\n"
				+	"      new                     	\n"
				+	"   val z:Int = y              	\n"
				+	"   def get():Int              	\n"
				+	"      this.z                  	\n"
				+	"X.create().get()              	\n";

		BytecodeValue res = runTest(s);
		assertEquals(res.toString(), "16");	
		
		String[] names = { "X", "y" };
		BytecodeValue[] vals = { clasDef, new BytecodeInt(16) };
		assertTrue(isInContext(names,vals));
	}
	
	@Test
	public void initTest() {
		
		PRINTS_ON = false;
				
		String s = 	"class X                       	\n"
				+	"   val z:Int = 4 * 2          	\n"
				+	"   def get():Int              	\n"
				+	"      this.z                  	\n"
				+	"   class def create() : X     	\n"
				+	"      new                     	\n"
				+	"X.create().get()              	\n";

		BytecodeValue res = runTest(s);
		assertEquals(res.toString(), "8");
		
		String[] names = { "X" };
		BytecodeValue[] vals = { clasDef };
		assertTrue(isInContext(names,vals));
	}
	
	@Test
	public void initAndThisTest() {
		
		PRINTS_ON = false;
				
		String s = 	"class X                       	\n"
				+	"   var z:Int = 4 * 2          	\n"
				+	"   def increase() : Int		\n"
				+ 	"      this.z = this.z + 1		\n"
				+ 	"      this.z					\n"
				+	"   def get():Int              	\n"
				+	"      this.z                  	\n"
				+	"   class def create() : X     	\n"
				+	"      new                     	\n"
				+	"val x1 : X = X.create()		\n"
				+ 	"var z1 : Int = x1.increase()	\n"
				+ 	"val x2 : X = X.create()		\n"
				+ 	"var z2 : Int = x2.increase()	\n"
				+ 	"val z3 : Int = x2.increase()	\n";

		BytecodeValue res = runTest(s);
		assertEquals(res.toString(), "()");	
		
		String[] names = { "X", "x1", "x2", "z1", "z2", "z3" };
		BytecodeInt nine = new BytecodeInt(9);
		BytecodeValue[] vals = { clasDef, clas, clas, nine, nine, new BytecodeInt(10) };
		assertTrue(isInContext(names,vals));
	}
	
	@Test
	public void mutualDependencyTest() {
		
		PRINTS_ON = false;
				
		String s = 	"class X                       	\n"
				+	"   var y:Y						\n"
				+ 	"   class def make() : X 		\n"
				+ 	"     new						\n"
				+ 	"class Y						\n"
				+ 	"   var x:X						\n"
				+ 	"   class def make() : Y		\n"
				+ 	"     new						\n"
				+ 	"var x1 : X = X.make()			\n"
				+ 	"var y1 : Y = Y.make()			\n";

		BytecodeValue res = runTest(s);
		assertEquals(res.toString(), "()");	
		
		String[] names = { "X", "Y", "x1", "y1" };
		BytecodeValue[] vals = { clasDef, clasDef, clas, clas };
		assertTrue(isInContext(names,vals));
	}
	
	@Category(RegressionTests.class)
	@Test
	public void classAndFieldTest() {
		
		PRINTS_ON = false;
		
		String s = 	"class Hello									\n"
				+	"	class def make():Hello = new				\n"
				+	"	def get4():Int = 4							\n"
				+	"	def get5():Int = 5							\n"
				+	"	def getP():Int = this.get4()+this.get5()	\n"
				+	"val h:Hello = Hello.make()						\n"
				+	"h.getP()										\n";

		BytecodeValue res = runTest(s);
		assertEquals(res.toString(), "9");
		
		String[] names = { "Hello", "h" };
		BytecodeValue[] vals = { clasDef, clas };
		assertTrue(isInContext(names,vals));
	}
	
	@Test 
	public void differntUseOfNew() {
		
		PRINTS_ON = false;
							
		String s =	"class X					\n"
				+ 	"	var x : Int				\n"
				+ 	"	class def create() : X	\n"
				+ 	"		val inst : X = 	new	\n"
				+ 	"		inst.x = 4			\n"
				+ 	"		inst				\n"
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
		
		String[] names = { "X", "xBefore", "xAfter" };
		BytecodeValue[] vals = { clasDef, new BytecodeInt(4), new BytecodeInt(2) };
		assertTrue(isInContext(names,vals));		
	}
	
	@Test
	public void classIfClosureTest() {
		
		PRINTS_ON = false;
		
		String s = 	"var a : Int = 4 								\n"
				+ 	"class Hello									\n"
				+	"	class def make():Hello = new				\n"
				+	"	def getA() : Int = a						\n"
				+ 	"	def setA()									\n"
				+ 	"		if a == 4 								\n"
				+ 	"			then								\n"
				+ 	"				a = 2							\n"
				+ 	"			else								\n"
				+ 	"				a = 4							\n"
				+ 	"val h : Hello = Hello.make()					\n"
				+ 	"val aBefore = h.getA()							\n"
				+ 	"h.setA()										\n"
				+ 	"val aAfter = h.getA()							\n";

		BytecodeValue res = runTest(s);
		assertEquals(res.toString(), "()");
		
		String[] names = { "Hello", "h", "a", "aBefore", "aAfter" };
		BytecodeValue[] vals = { clasDef, clas, new BytecodeInt(2), 
				new BytecodeInt(4), new BytecodeInt(2) };
		assertTrue(isInContext(names,vals));
	}
}
