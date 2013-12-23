package wyvern.tools.bytecode.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import wyvern.DSL.DSL;
import wyvern.targets.Common.WyvernIL.ExnFromAST;
import wyvern.targets.Common.WyvernIL.TLFromAST;
import wyvern.targets.Common.WyvernIL.Def.Def.Param;
import wyvern.targets.Common.WyvernIL.Stmt.Statement;
import wyvern.tools.bytecode.core.BytecodeContext;
import wyvern.tools.bytecode.core.EmptyContext;
import wyvern.tools.bytecode.core.Interperter;
import wyvern.tools.bytecode.values.BytecodeFunction;
import wyvern.tools.bytecode.values.BytecodeInt;
import wyvern.tools.bytecode.values.BytecodeString;
import wyvern.tools.bytecode.values.BytecodeTuple;
import wyvern.tools.bytecode.values.BytecodeValue;
import wyvern.tools.typedAST.core.expressions.New;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.TypedAST;

public class TestBC {

	// control whether or not the tests also print everything
	private boolean PRINTS_ON = false;
	private Interperter interperter;
	private BytecodeValue func;

	private List<Statement> getResult(TypedAST input) {
		if (!(input instanceof CoreAST))
			throw new RuntimeException();
		CoreAST cast = (CoreAST) input;
		TLFromAST.flushInts();
		New.resetGenNum();
		ExnFromAST visitor = new ExnFromAST();
		cast.accept(visitor);
		return visitor.getStatments();
	}

	@Before
	public void setUp() throws Exception {
		List<Param> params = new ArrayList<Param>();
		List<Statement> statements = new ArrayList<Statement>();
		func = new BytecodeFunction(params,statements,new EmptyContext());
	}

	@After
	public void tearDown() throws Exception {
		if(PRINTS_ON) {
			System.out.println("\n ================================= \n");
		}
	}

	@Test
	public void assignTest() {
				
		String s = 	"var x : Int = 1 \n"
				+ 	"x = 3 \n"
				+ 	"val y = 5";
		
		BytecodeValue res = runTest(s);	

		String[] names = { "x", "y" };
		BytecodeValue[] vals = { new BytecodeInt(3), new BytecodeInt(5) };
		assertTrue(isInContext(names,vals));
		assertEquals(res.toString(), "[]");
	}
	
	@Test
	public void defnTest() {

		String s = 	"val x : Int = 2 + 2 \n"
				+ 	"val y : Int = 2 * x \n"
				+ 	"val z : Str = \"Hello \" + \"World\"\n"
				+	"val w = (x,y,z)";

		BytecodeValue res = runTest(s);	

		String[] names = { "x", "y", "z", "w" };
		List<BytecodeValue> tuple = new ArrayList<BytecodeValue>();
		tuple.add(new BytecodeInt(4));
		tuple.add(new BytecodeInt(8));
		tuple.add(new BytecodeString("Hello World"));
		BytecodeValue[] vals = { new BytecodeInt(4), new BytecodeInt(8),
							     new BytecodeString("Hello World"), 
								 new BytecodeTuple(tuple)};
		assertTrue(isInContext(names,vals));
		assertEquals(res.toString(), "[]");	
	}

	@Test
	public void gotoLabelIfstmtTest() {
		
		String s =	"var x:Int = 5 \n" 
				+	"var y:Int = 0 \n" 
				+ 	"while x > 0 \n" 
				+ 	"	x = x-1 \n"
				+	"	y = y+1 \n";
		
		BytecodeValue res = runTest(s);	

		String[] names = { "x", "y" };
		BytecodeValue[] vals = { new BytecodeInt(0), new BytecodeInt(5) };
		assertTrue(isInContext(names,vals));
		assertEquals(res.toString(), "[]");	
	}
	
	@Test
	public void gotoLabelIfstmtTest2() {
				
		String s =	"if true \n" 
				+ 	"	then \n" 
				+	"		1 \n" 
				+	"	else \n" 
				+	"		2 \n";

		BytecodeValue res = runTest(s);
		
		String[] names = {  };
		BytecodeValue[] vals = {  };
		assertTrue(isInContext(names,vals));
		assertEquals(res.toString(), "[]");	 
	}
	
	@Test
	public void defineFunction() {
		
		String s =	"val fun1 = fn x : Int => x + 1 \n"
				+ 	"val fun2 = fn f : Int -> Int => fn x : Int => f(f(x))\n"
				+ 	"def fun3(z : Int) : Int\n"
				+	"  z+1";

		BytecodeValue res = runTest(s);	

		String[] names = { "fun1", "fun2", "fun3" };
		BytecodeValue[] vals = { func, func, func };
		assertTrue(isInContext(names,vals));
		assertEquals(res.toString(), "[]");	 
	}
	
	@Test
	public void invokeFunction() {
		
		String s =	"def mult(n:Int,m:Int):Int = n+5*m \n"
				+	"val x = mult(3,2) \n"
				+ 	"val y = mult(4,5) \n"
				+ 	"val z = mult(1,1)";

		BytecodeValue res = runTest(s);	

		String[] names = { "x", "y", "z", "mult" };
		BytecodeValue[] vals = { new BytecodeInt(13), new BytecodeInt(29),
								 new BytecodeInt(6), func };
		assertTrue(isInContext(names,vals));
		assertEquals(res.toString(), "[]");		
	}
	
	@Test
	public void invokeFunction2() {
		
		String s =	"val x = (fn x : Int => x + 1)(3)";

		BytecodeValue res = runTest(s);	

		String[] names = { "x" };
		BytecodeValue[] vals = { new BytecodeInt(4) };
		assertTrue(isInContext(names,vals));
		assertEquals(res.toString(), "[]");	
	}
	
	@Test
	public void invokeFunction3() {
		
		String s =	"var x : Int = 3 \n"
				+ 	"def mult(n:Int,m:Int):Int = (n+m)*x \n"
				+ 	"mult(1,2)";

		BytecodeValue res = runTest(s);	

		String[] names = { "x", "mult" };
		BytecodeValue[] vals = { new BytecodeInt(3) , func };
		assertTrue(isInContext(names,vals));
		assertEquals(res.toString(), "9");
	}
	
	@Test
	public void invokeFunction4() {
		
		String s =	"var x : Int = 3 \n"
				+ 	"def mult(n:Int,m:Int):Int \n"
				+ 	" x = x + 1 \n"
				+ 	" n * m \n"
				+ 	"mult(1,2)";

		BytecodeValue res = runTest(s);	

		String[] names = { "x", "mult" };
		BytecodeValue[] vals = { new BytecodeInt(4) , func };
		assertTrue(isInContext(names,vals));
		assertEquals(res.toString(), "2");
	}
	
	@Test
	public void invokeFunction5() {
		
		String s =	"var x : Int = 3 \n"
				+ 	"def mult(n:Int,m:Int):Int \n"
				+ 	" val y = 4 \n"
				+ 	" n * m \n"
				+ 	"mult(1,2)";

		BytecodeValue res = runTest(s);	

		String[] names = { "x", "mult" };
		BytecodeValue[] vals = { new BytecodeInt(3) , func };
		assertTrue(isInContext(names,vals));
		assertEquals(res.toString(), "2");
	}
	
	@Test
	public void functionCallsFunction() {
		
		String s =	"def add(x:Int,y:Int):Int \n"
				+ 	"	x + y \n"
				+ 	"def mult(n:Int,m:Int):Int \n"
				+ 	"	var z : Int = m \n"
				+ 	"	var sum : Int = 0 \n"
				+ 	"	while z > 0 \n"
				+ 	"		sum = add(n,sum) \n"
				+ 	"		z = z - 1 \n"
				+ 	"	sum \n"
				+ 	"mult(5,5)";

		BytecodeValue res = runTest(s);	

		String[] names = { "add", "mult" };
		BytecodeValue[] vals = { func , func };
		assertTrue(isInContext(names,vals));
		assertEquals(res.toString(), "25");
	}
	
	
	@Test
	public void nestedFunction() {
		
		String s =	"def mult(n:Int,m:Int):Int \n"
				+ 	"	def add(x:Int,y:Int):Int \n"
				+ 	"		x + y \n"
				+ 	"	var z : Int = m \n"
				+ 	"	var sum : Int = 0 \n"
				+ 	"	while z > 0 \n"
				+ 	"		sum = add(n,sum) \n"
				+ 	"		z = z - 1 \n"
				+ 	"	sum \n"
				+ 	"mult(3,6)";

		BytecodeValue res = runTest(s);	

		String[] names = { "mult" };
		BytecodeValue[] vals = { func };
		assertTrue(isInContext(names,vals));
		assertEquals(res.toString(), "18");

	}
	
/*	
	@Test
	public void recursiveFunction() {
				
		String s =	"def factorial(n:Int):Int \n" 
				+	" if n==1 \n"
				+	"  then \n"
				+	"   n \n"
				+	"  else \n"
				+	"   n * factorial(n-1)";

		BytecodeValue res = runTest(s);	

		//String[] names = { "x", "mult" };
		//BytecodeValue[] vals = { new BytecodeInt(3) , func };
		//assertTrue(isInContext(names,vals));
		//assertEquals(res.toString(), "2");	
	}
*/
	
	private BytecodeValue runTest(String s) {
		ArrayList<String> strs = new ArrayList<>();
		strs.add(s);
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs,
				new ArrayList<DSL>());
		List<Statement> statements = getResult(pair);

		interperter = new Interperter(statements);
		BytecodeValue res = interperter.execute();
		if(PRINTS_ON) {
			System.out.println("Instructions:");
			for (Statement statement : statements) {
				System.out.println(statement.getClass().getSimpleName() + " : "
						+ statement.toString());
			}
			interperter.printContext();
			System.out.println("		DONE");	
		}
		return res;
	}
	
	public boolean isInContext(String[] names, BytecodeValue[] vals) {
		BytecodeContext context = interperter.getCurrentContext();
		for(int i = 0 ; i < names.length ; i++) {
			try {
				BytecodeValue val = context.getValue(names[i]).dereference();
				if(val instanceof BytecodeFunction) {
					continue;
				}
				if(!val.equals(vals[i])) {
					return false;
				}
			} catch(RuntimeException e) {
				return false;
			}
		}
		return true;
	}
	
}
