package wyvern.tools.bytecode.tests;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import wyvern.targets.Common.WyvernIL.ExnFromAST;
import wyvern.targets.Common.WyvernIL.TLFromAST;
import wyvern.targets.Common.WyvernIL.Def.ValDef;
import wyvern.targets.Common.WyvernIL.Def.VarDef;
import wyvern.targets.Common.WyvernIL.Expr.BinOp;
import wyvern.targets.Common.WyvernIL.Expr.Immediate;
import wyvern.targets.Common.WyvernIL.Imm.IntValue;
import wyvern.targets.Common.WyvernIL.Imm.StringValue;
import wyvern.targets.Common.WyvernIL.Imm.VarRef;
import wyvern.targets.Common.WyvernIL.Stmt.Defn;
import wyvern.targets.Common.WyvernIL.Stmt.Statement;
import wyvern.tools.bytecode.core.Interperter;
import wyvern.tools.typedAST.core.expressions.New;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.TypedAST;

public class TestBC {
	
	private Interperter interperter;

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
		for (Statement s : list) {
			sb.append(loopDelim);
			sb.append(s);
			loopDelim = ",";
		}
		return sb.toString();
	}

	@Before
	public void setUp() throws Exception {
/*		ArrayList<String> strs = new ArrayList<>();
		strs.add("val x = 2"
				+ "\n"
				+ "val y = \"hello\""
				+ "\n" 
				+ "val n = 17");
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs,
				new ArrayList<DSL>());
		List<Statement> statements = getResult(pair);
		interperter = new Interperter(statements);			*/
		
		List<Statement> statements = new ArrayList<Statement>();
		Immediate imm = new Immediate(new IntValue(2));
		VarDef vardef = new VarDef("temp$0", imm);
		Defn two = new Defn(vardef);
		statements.add(two);
		Immediate imm2 = new Immediate(new VarRef("temp$0"));
		ValDef valdef2 = new ValDef("temp$2", imm2);
		Defn three = new Defn(valdef2);
		statements.add(three);
		BinOp bin = new BinOp(imm.getInner(),imm2.getInner(),"*");
		ValDef valdef3 = new ValDef("temp$3", bin);
		Defn op = new Defn(valdef3);
		statements.add(op);
		
		imm = new Immediate(new StringValue("Hello "));
		ValDef valdef = new ValDef("temp$0", imm);
		two = new Defn(valdef);
		statements.add(two);
		imm2 = new Immediate(new StringValue("World"));
		valdef2 = new ValDef("temp$2", imm2);
		three = new Defn(valdef2);
		statements.add(three);
		bin = new BinOp(imm.getInner(),imm2.getInner(),"+");
		valdef3 = new ValDef("temp$3", bin);
		op = new Defn(valdef3);
		statements.add(op);
		
		interperter = new Interperter(statements);
		for (Statement statement : statements) {
			System.out.println(statement.getClass().getSimpleName() + " : "
					+ statement.toString());
		}
	}

	@After
	public void tearDown() throws Exception {
	}

	
/*	@Test
	public void testSimple() {
		ArrayList<String> strs = new ArrayList<>();
		strs.add("val x = 1 + 2\nval y = x");
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs,
				new ArrayList<DSL>());
		List<Statement> statements = getResult(pair);
		for (Statement statement : statements) {
			System.out.println(statement.getClass().getSimpleName() + " : "
					+ statement.toString());
		}
	}*/
	
	
	@Test
	public void test() {
		interperter.execute();
		interperter.printContext();
	}

}
