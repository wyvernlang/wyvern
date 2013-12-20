package wyvern.targets.Common.WyvernIL.tests;

import junit.framework.Assert;
import org.junit.Test;
import wyvern.DSL.DSL;
import wyvern.stdlib.*;
import wyvern.targets.Common.WyvernIL.ExnFromAST;
import wyvern.targets.Common.WyvernIL.Stmt.Statement;
import wyvern.targets.Common.WyvernIL.TLFromAST;
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
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs, new ArrayList<DSL>());
		Assert.assertEquals(join(getResult(pair)),"temp$0 = 2,temp$2 = 2,temp$3 = temp$0 + temp$2,temp$3");;
	}
	@Test
	public void testVal() {
		ArrayList<String> strs = new ArrayList<>();
		strs.add("val x : Int = 5\n"
				+"x");
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs, new ArrayList<DSL>());
		Assert.assertEquals(join(getResult(pair)),"val x = 5,x");;
	}
	@Test
	public void testLambdaCall() {
		ArrayList<String> strs = new ArrayList<>();
		strs.add("(fn x : Int => x)(1)");
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs, new ArrayList<DSL>());
		Assert.assertEquals(join(getResult(pair)),"def 0$lambda(x : Int) {x},temp$0 = 0$lambda,temp$1 = 1,temp$0(temp$1)");;
	}
	@Test
	public void testLambdaCallWithAdd() {
		ArrayList<String> strs = new ArrayList<>();
		strs.add("(fn x : Int => x + 1)(3)");
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs, new ArrayList<DSL>());
		Assert.assertEquals(join(getResult(pair)),"def 0$lambda(x : Int) {temp$1 = x,temp$3 = 1,temp$4 = temp$1 + temp$3,temp$4},temp$0 = 0$lambda,temp$5 = 3,temp$0(temp$5)");;
	}
	@Test
	public void testArithmetic() {
		ArrayList<String> strs = new ArrayList<>();
		strs.add("3*4+5*6");
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs, new ArrayList<DSL>());
		Assert.assertEquals(join(getResult(pair)),"temp$4 = 3,temp$6 = 4,temp$7 = temp$4 * temp$6,temp$8 = temp$7,temp$0 = 5,temp$2 = 6,temp$3 = temp$0 * temp$2,temp$10 = temp$3,temp$11 = temp$8 + temp$10,temp$11");
	}

	@Test
	public void testHigherOrderTypes() {
		ArrayList<String> strs = new ArrayList<>();
		strs.add("fn f : Int -> Int => fn x : Int => f(f(x))");
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs, new ArrayList<DSL>());
		Assert.assertEquals(join(getResult(pair)),"def 1$lambda(f : Int -> Int) {def 0$lambda(x : Int) {temp$0 = f,temp$1 = f,temp$2 = x,temp$3 = temp$1(temp$2),temp$0(temp$3)},0$lambda},1$lambda");
	}
	@Test
	public void testTupleMethodCalls() {
		ArrayList<String> strs = new ArrayList<>();
		strs.add("def mult(n:Int,m:Int):Int = n+5*m\n"
				+"mult(3,2)\n");
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs, new ArrayList<DSL>());
		Assert.assertEquals(join(getResult(pair)),"def mult(n : Int,m : Int) {temp$4 = n,temp$0 = 5,temp$2 = m,temp$3 = temp$0 * temp$2,temp$6 = temp$3,temp$7 = temp$4 + temp$6,temp$7},temp$8 = mult,temp$9 = 3,temp$10 = 2,temp$11 = (temp$9,temp$10),temp$8(temp$11)");
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
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs, new ArrayList<DSL>());
		Assert.assertEquals(join(getResult(pair)),"class Hello {def make() {new Hello}; val hiString = null; def $init() {this.hiString = \"hello\"}},temp$1 = Hello,temp$0 = temp$1.make,temp$4 = (),val h = temp$0(temp$4),temp$5 = h,temp$5.hiString");
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
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs, new ArrayList<DSL>());
		Assert.assertEquals(join(getResult(pair)),"class Hello {def make() {new Hello}; def get4() {4}; def get5() {5}; def getP() {temp$6 = this,temp$5 = temp$6.get4,temp$9 = (),temp$10 = temp$5(temp$9),temp$1 = this,temp$0 = temp$1.get5,temp$4 = (),temp$12 = temp$0(temp$4),temp$13 = temp$10 + temp$12,temp$13}; def $init() {}},temp$15 = Hello,temp$14 = temp$15.make,temp$18 = (),val h = temp$14(temp$18),temp$20 = h,temp$19 = temp$20.getP,temp$23 = (),temp$19(temp$23)");
	}

	@Test
	public void testVarAssignment2() {
		ArrayList<String> strs = new ArrayList<>();
		strs.add("var x:Int = 1\nx=2\nvar y:Int = 3\ny=4\nx=y\nx");
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs, new ArrayList<DSL>());
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
				+"h.getV()");
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs, new ArrayList<DSL>());
		Assert.assertEquals(join(getResult(pair)),"class Hello {def make() {new Hello}; var testVal = null; def setV(n : Int) {temp$0 = this,temp$0.testVal = n}; def getV() {temp$3 = this,temp$3.testVal}; def $init() {this.testVal = 5}},temp$7 = Hello,temp$6 = temp$7.make,temp$10 = (),val h = temp$6(temp$10),temp$12 = h,temp$11 = temp$12.setV,temp$15 = 10,temp$11(temp$15),temp$17 = h,temp$16 = temp$17.getV,temp$20 = (),temp$16(temp$20)");
	}

	@Test
	public void testIf() {
		ArrayList<String> strs = new ArrayList<>();
		strs.add("if true\n" +
				"	then\n" +
				"		1\n" +
				"	else\n" +
				"		2\n");
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs, new ArrayList<DSL>());
		List<Statement> result = getResult(pair);
		Assert.assertEquals(join(result),"label 1,if (true) goto label 2,goto label 3,label 2,ifRet$0 = 1,goto label 0,label 3,if (true) goto label 4,goto label 5,label 4,ifRet$0 = 2,goto label 0,label 5,goto label 0,label 0,");
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
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs, new ArrayList<DSL>());
		List<Statement> result = getResult(pair);
		Assert.assertEquals(join(result),"var x = 5,var y = 0,label 0,temp$0 = x,temp$2 = 0,temp$3 = temp$0 > temp$2,if (temp$3) goto label 1,goto label 2,label 1,temp$4 = x,temp$6 = 1,temp$7 = temp$4 - temp$6,x = temp$7,temp$8 = y,temp$10 = 1,temp$11 = temp$8 + temp$10,y = temp$11,goto label 0,label 2,y");
	}

	//TODO: have new autogenerate a correct class to instanciate.
	@Test
	public void testGenericNew2() {
		ArrayList<String> strs = new ArrayList<>();
		strs.add("val test = new\n" +
				"	x = 2\n");
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs, new ArrayList<DSL>());
		List<Statement> result = getResult(pair);
		Assert.assertEquals(join(result),"val test = new generic1");
	}
	@Test
	public void testInnerTypes() {
		ArrayList<String> strs = new ArrayList<>();
		strs.add("" +
				"class A\n" +
				"	class B\n" +
				"		val x : C.D\n" +
				"class C\n" +
				"	class D\n" +
				"		val y : A.B\n");
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs, new ArrayList<DSL>());
		List<Statement> result = getResult(pair);
		Assert.assertEquals(join(result),"class A {class B {val x = null; def $init() {}}; def $init() {}},class C {class D {val y = null; def $init() {}}; def $init() {}}");
	}
}
