package wyvern.targets.Common.wyvernIL.IL.tests;

import org.junit.Assert;
import org.junit.Test;
import wyvern.targets.Common.wyvernIL.transformers.ExnFromAST;
import wyvern.targets.Common.wyvernIL.IL.Stmt.Statement;
import wyvern.targets.Common.wyvernIL.transformers.TLFromAST;
import wyvern.tools.typedAST.core.expressions.New;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.TypedAST;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ben Chung on 11/18/13.
 */
public class TestIL {
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
	private static String join(List<Statement> list) {

		StringBuilder sb = new StringBuilder();

		String loopDelim = "";

		for(Statement s : list) {

			sb.append(loopDelim);
			sb.append(s);

			loopDelim = ",";
		}

		return sb.toString();
	}
	@Test
	public void testSimple() {
		ArrayList<String> strs = new ArrayList<>();
		strs.add("2+2");
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs);
		Assert.assertEquals(join(getResult(pair)),"val temp$0 = 2,val temp$1 = 2,val temp$2 = temp$0 + temp$1,temp$2");;
	}
	@Test
	public void testVal() {
		ArrayList<String> strs = new ArrayList<>();
		strs.add("val x : Int = 5\n"
				+"x");
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs);
		Assert.assertEquals(join(getResult(pair)),"val x = 5,x");;
	}
	@Test
	public void testLambdaCall() {
		ArrayList<String> strs = new ArrayList<>();
		strs.add("(fn x : Int => (x))(1)");
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs);
		Assert.assertEquals(join(getResult(pair)),"def 0$lambda(x : Int) {x},val temp$0 = 0$lambda,val temp$1 = 1,temp$0(temp$1)");;
	}
	@Test
	public void testLambdaCallWithAdd() {
		ArrayList<String> strs = new ArrayList<>();
		strs.add("(fn x : Int => (x + 1))(3)");
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs);
		Assert.assertEquals(join(getResult(pair)),"def 0$lambda(x : Int) {val temp$1 = x,val temp$2 = 1,val temp$3 = temp$1 + temp$2,temp$3},val temp$0 = 0$lambda,val temp$4 = 3,temp$0(temp$4)");;
	}
	@Test
	public void testArithmetic() {
		ArrayList<String> strs = new ArrayList<>();
		strs.add("3*4+5*6");
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs);
		Assert.assertEquals(join(getResult(pair)),"val temp$3 = 3,val temp$4 = 4,val temp$5 = temp$3 * temp$4,val temp$6 = temp$5,val temp$0 = 5,val temp$1 = 6,val temp$2 = temp$0 * temp$1,val temp$7 = temp$2,val temp$8 = temp$6 + temp$7,temp$8");
	}

	@Test
	public void testHigherOrderTypes() {
		ArrayList<String> strs = new ArrayList<>();
		strs.add("fn f : Int -> Int => (fn x : Int => (f(f(x))))");
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs);
		Assert.assertEquals(join(getResult(pair)),"def 1$lambda(f : Int -> Int) {def 0$lambda(x : Int) {val temp$0 = f,val temp$1 = f,val temp$2 = x,val temp$3 = temp$1(temp$2),temp$0(temp$3)},0$lambda},1$lambda");
	}
	@Test
	public void testTupleMethodCalls() {
		ArrayList<String> strs = new ArrayList<>();
		strs.add("def mult(n:Int,m:Int):Int = n+5*m\n"
				+"mult(3,2)\n");
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs);
		Assert.assertEquals(join(getResult(pair)),"def mult(n : Int,m : Int) {val temp$3 = n,val temp$0 = 5,val temp$1 = m,val temp$2 = temp$0 * temp$1,val temp$4 = temp$2,val temp$5 = temp$3 + temp$4,temp$5},val temp$6 = mult,val temp$7 = 3,val temp$8 = 2,val temp$9 = (temp$7,temp$8),temp$6(temp$9)");
	}


	@Test
	public void testClassAndField() {
		ArrayList<String> strs = new ArrayList<>();
		strs.add("class Hello\n"
				+"    class def make():Hello\n"
				+"    \tnew\n"
				+"    val hiString : Str = \"hello\"\n"
				+"\n"
				+"val h : Hello = Hello.make()\n" //hiString: \"hi\")\n"
				+"h.hiString");
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs);
		Assert.assertEquals(join(getResult(pair)),"class Hello { static {def make() {new }; def $init() {val hiString = \"hello\"}}; val hiString = \"hello\"},val temp$1 = Hello,val temp$0 = temp$1.make,val temp$3 = (),val h = temp$0(temp$3),val temp$4 = h,temp$4.hiString");
	}


	@Test
	public void testClassAndMethods2() {
		ArrayList<String> strs = new ArrayList<>();
		strs.add("class Hello\n"
				+"	class def make():Hello = new\n"
				+"	def get4():Int = 4\n"
				+"	def get5():Int = 5\n"
				+"	def getP():Int = this.get4()+this.get5()\n"
				+"\n"
				+"val h:Hello = Hello.make()\n"
				+"h.getP()");
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs);
		Assert.assertEquals(join(getResult(pair)),"class Hello { static {def make() {new }; def $init() {}}; def get4() {4}; def get5() {5}; def getP() {val temp$5 = this,val temp$4 = temp$5.get4,val temp$7 = (),val temp$8 = temp$4(temp$7),val temp$1 = this,val temp$0 = temp$1.get5,val temp$3 = (),val temp$9 = temp$0(temp$3),val temp$10 = temp$8 + temp$9,temp$10}},val temp$12 = Hello,val temp$11 = temp$12.make,val temp$14 = (),val h = temp$11(temp$14),val temp$16 = h,val temp$15 = temp$16.getP,val temp$18 = (),temp$15(temp$18)");
	}

	@Test
	public void testVarAssignment2() {
		ArrayList<String> strs = new ArrayList<>();
		strs.add("var x:Int = 1\nx=2\nvar y:Int = 3\ny=4\nx=y\nx");
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs);
		Assert.assertEquals(join(getResult(pair)),"var x = 1,x = 2,var y = 3,y = 4,x = y,x");
	}


	@Test
	public void testVarAssignmentInClass() {
		ArrayList<String> strs = new ArrayList<>();
		strs.add("class Hello\n"
				+"	class def make():Hello = new\n"
				+"	var testVal:Int = 5\n"
				+"	def setV(n : Int):Unit = this.testVal = n\n"
				+"	def getV():Int = this.testVal\n"
				+"val h:Hello = Hello.make()\n"
				+"h.setV(10)\n"
				+"h.getV()\n");
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs);
		Assert.assertEquals(join(getResult(pair)),"class Hello { static {def make() {new }; def $init() {var testVal = 5}}; var testVal = 5; def setV(n : Int) {val temp$0 = this,temp$0.testVal = n}; def getV() {val temp$2 = this,temp$2.testVal}},val temp$5 = Hello,val temp$4 = temp$5.make,val temp$7 = (),val h = temp$4(temp$7),val temp$9 = h,val temp$8 = temp$9.setV,val temp$11 = 10,temp$8(temp$11),val temp$13 = h,val temp$12 = temp$13.getV,val temp$15 = (),temp$12(temp$15)");
	}

	@Test
	public void testIf() {
		ArrayList<String> strs = new ArrayList<>();
		strs.add("if true\n" +
				"	then\n" +
				"		1\n" +
				"	else\n" +
				"		2\n");
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs);
		List<Statement> result = getResult(pair);
		Assert.assertEquals(join(result),"label 1,if (true) goto label 2,goto label 3,label 2,val ifRet$0 = 1,goto label 0,label 3,if (true) goto label 4,goto label 5,label 4,val ifRet$0 = 2,goto label 0,label 5,goto label 0,label 0,ifRet$0");
	}

	@Test
	public void testIf2() {
		ArrayList<String> strs = new ArrayList<>();
		strs.add("var x : Int = 0\n" +
				"if true\n" +
				"	then\n" +
				"		x = 1\n" +
				"	else\n" +
				"		x = 2\n" +
				"x");
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs);
		List<Statement> result = getResult(pair);
		Assert.assertEquals(join(result),"var x = 0,label 1,if (true) goto label 2,goto label 3,label 2,x = 1,val ifRet$0 = (),goto label 0,label 3,if (true) goto label 4,goto label 5,label 4,x = 2,val ifRet$0 = (),goto label 0,label 5,goto label 0,label 0,ifRet$0,x");
	}
	@Test
	public void testWhile() {
		ArrayList<String> strs = new ArrayList<>();
		strs.add("var x:Int = 5\n" +
				"var y:Int = 0\n" +
				"while x > 0\n" +
				"	x = x-1\n" +
				"	y = y+1\n" +
				"y");
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs);
		List<Statement> result = getResult(pair);
		Assert.assertEquals(join(result),"var x = 5,var y = 0,label 0,val temp$0 = x,val temp$1 = 0,val temp$2 = temp$0 > temp$1,if (temp$2) goto label 1,goto label 2,label 1,val temp$3 = x,val temp$4 = 1,val temp$5 = temp$3 - temp$4,x = temp$5,val temp$6 = y,val temp$7 = 1,val temp$8 = temp$6 + temp$7,y = temp$8,goto label 0,label 2,y");
	}

	//TODO: have new autogenerate a correct class to instanciate.
	@Test
	public void testGenericNew2() {
		ArrayList<String> strs = new ArrayList<>();
		strs.add("val test = new\n" +
				"	val x = 2\n" +
				"test\n");
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs);
		List<Statement> result = getResult(pair);
		Assert.assertEquals(join(result),"val test = new ,test");
	}
	@Test
	public void tIf() {
		ArrayList<String> strs = new ArrayList<>();
		strs.add("val x = if true \n"
				+  " then \n"
				+ "  1 \n"
				+ " else \n"
				+ "  2 \n");
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs);
		List<Statement> result = getResult(pair);
		Assert.assertEquals("label 1,if (true) goto label 2,goto label 3,label 2,val ifRet$0 = 1,goto label 0,label 3,if (true) goto label 4,goto label 5,label 4,val ifRet$0 = 2,goto label 0,label 5,goto label 0,label 0,val x = ifRet$0", join(result));
	}
	@Test
	public void tIf2() {
		ArrayList<String> strs = new ArrayList<>();
		strs.add("var x : Int = 0\n" +
				"if true \n"
				+ " then \n"
				+ "  x=1 \n"
				+ " else \n"
				+ "  x=2 \n");
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs);
		List<Statement> result = getResult(pair);
		Assert.assertEquals("var x = 0,label 1,if (true) goto label 2,goto label 3,label 2,x = 1,val ifRet$0 = (),goto label 0,label 3,if (true) goto label 4,goto label 5,label 4,x = 2,val ifRet$0 = (),goto label 0,label 5,goto label 0,label 0,ifRet$0", join(result));
	}
	@Test
	public void tVin() {
		ArrayList<String> strs = new ArrayList<>();
		strs.add("val x = 2\n" +
				"val y = x\n" +
				"y");
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs);
		List<Statement> result = getResult(pair);
		Assert.assertEquals("val x = 2,val y = x,y", join(result));
	}
	@Test
	public void tC() {
		ArrayList<String> strs = new ArrayList<>();
		strs.add("class X                         \n"
				+"	class def create(i:Int) : X    \n"
				+"		new                        \n"
				+"			val t = i                \n"
				+"	val t:Int                      \n"
				+"	def get():Int                  \n"
				+"		this.t                    \n"
				+"val c:X = X.create(1)    \n"
				+"val a:Unit->Int = c.get             \n"
				+"val b:Unit->Int = X.create(2).get           \n"
				+"b() + a()                         \n");
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs);
		List<Statement> result = getResult(pair);
		Assert.assertEquals("class X { static {def create(i : Int) {new }; def $init() {val t = null}}; val t = null; def get() {val temp$0 = this,temp$0.t}},val temp$3 = X,val temp$2 = temp$3.create,val temp$5 = 1,val c = temp$2(temp$5),val temp$6 = c,val a = temp$6.get,val temp$9 = X,val temp$8 = temp$9.create,val temp$11 = 2,val temp$12 = temp$8(temp$11),val b = temp$12.get,val temp$16 = b,val temp$17 = (),val temp$18 = temp$16(temp$17),val temp$14 = a,val temp$15 = (),val temp$19 = temp$14(temp$15),val temp$20 = temp$18 + temp$19,temp$20", join(result));
	}
	@Test
	public void tT() {
		ArrayList<String> strs = new ArrayList<>();
		strs.add("var a : Int = 4 \n"
				+  "if a == 4    \n"
				+  " then    \n"
				+ "  a = 2   \n"
				+ " else    \n"
				+ "  a = 3   \n");
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs);
		List<Statement> result = getResult(pair);
		Assert.assertEquals("var a = 4,label 1,val temp$0 = a,val temp$1 = 4,val temp$2 = temp$0 == temp$1,if (temp$2) goto label 2,goto label 3,label 2,a = 2,val ifRet$0 = (),goto label 0,label 3,if (true) goto label 4,goto label 5,label 4,a = 3,val ifRet$0 = (),goto label 0,label 5,goto label 0,label 0,ifRet$0", join(result));
	}
	@Test
	public void tP() {
		ArrayList<String> strs = new ArrayList<>();
		strs.add("val x = (1,2,3)\nx");
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs);
		List<Statement> result = getResult(pair);
		Assert.assertEquals("val temp$0 = 1,val temp$1 = 2,val temp$2 = 3,val x = (temp$0,temp$1,temp$2),x", join(result));
	}
}
