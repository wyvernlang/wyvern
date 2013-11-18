package wyvern.targets.Common.WyvernIL.tests;

import junit.framework.Assert;
import org.junit.Test;
import wyvern.DSL.DSL;
import wyvern.stdlib.*;
import wyvern.targets.Common.WyvernIL.ExnFromAST;
import wyvern.targets.Common.WyvernIL.Stmt.Statement;
import wyvern.targets.Common.WyvernIL.TLFromAST;
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
				+"val h : Hello = Hello.make()\n"//hiString: \"hi\")\n"
				+"h.hiString");
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs, new ArrayList<DSL>());
		Assert.assertEquals(join(getResult(pair)),"class Hello {def make() {new Hello}; val hiString = null; def $init() {this.hiString = \"hello\"}},temp$1 = Hello,temp$0 = temp$1.make,temp$4 = (),val h = temp$0(temp$4),temp$5 = h,temp$5.hiString");
	}
}
