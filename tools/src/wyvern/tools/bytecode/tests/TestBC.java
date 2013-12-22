package wyvern.tools.bytecode.tests;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import wyvern.DSL.DSL;
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

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		System.out.println("\n ================================ \n");
	}

	@Test
	public void assignTest() {
		ArrayList<String> strs = new ArrayList<>();
		strs.add("var x:Int = 1\nx = 3\nval y = 5");
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs,
				new ArrayList<DSL>());
		List<Statement> statements = getResult(pair);

		System.out.println("Instructions:\n");
		for (Statement statement : statements) {
			System.out.println(statement.getClass().getSimpleName() + " : "
					+ statement.toString());
		}

		interperter = new Interperter(statements);
		interperter.execute();
		interperter.printContext();
		
		System.out.println("		DONE");
	}

	@Test
	public void defTest() {

		List<Statement> statements = new ArrayList<Statement>();
		Immediate imm = new Immediate(new IntValue(2));
		VarDef vardef = new VarDef("temp$0", imm);
		Defn two = new Defn(vardef);
		statements.add(two);
		Immediate imm2 = new Immediate(new VarRef("temp$0"));
		ValDef valdef2 = new ValDef("temp$1", imm2);
		Defn three = new Defn(valdef2);
		statements.add(three);
		BinOp bin = new BinOp(imm.getInner(), imm2.getInner(), "*");
		ValDef valdef3 = new ValDef("temp$2", bin);
		Defn op = new Defn(valdef3);
		statements.add(op);

		imm = new Immediate(new StringValue("Hello "));
		ValDef valdef = new ValDef("temp$3", imm);
		two = new Defn(valdef);
		statements.add(two);
		imm2 = new Immediate(new StringValue("World"));
		valdef2 = new ValDef("temp$4", imm2);
		three = new Defn(valdef2);
		statements.add(three);
		bin = new BinOp(imm.getInner(), imm2.getInner(), "+");
		valdef3 = new ValDef("temp$5", bin);
		op = new Defn(valdef3);
		statements.add(op);

		System.out.println("Instructions:\n");
		for (Statement statement : statements) {
			System.out.println(statement.getClass().getSimpleName() + " : "
					+ statement.toString());
		}

		interperter = new Interperter(statements);
		interperter.execute();
		interperter.printContext();

		System.out.println("		DONE");
	}

}
