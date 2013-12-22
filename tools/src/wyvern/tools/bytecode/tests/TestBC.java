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
		System.out.println("\n ================================= \n");
	}

	@Test
	public void assignTest() {
				
		String s = 	"var x : Int = 1 \n"
				+ 	"x = 3 \n"
				+ 	"val y = 5";
		
		runTest(s);
		
	}
	
	@Test
	public void defnTest() {

		String s = 	"val x : Int = 2 + 2 \n"
				+ 	"val y : Int = 2 * x \n"
				+ 	"val z : Str = \"Hello \" + \"World\"\n"
				+	"val w = (1,2,3,4,5,6)";

		runTest(s);
		
	}

	@Test
	public void gotoLabelIfstmtTest() {
		
		String s =	"var x:Int = 5 \n" 
				+	"var y:Int = 0 \n" 
				+ 	"while x > 0 \n" 
				+ 	"	x = x-1 \n"
				+	"	y = y+1 \n";
		
		runTest(s);
		
	}
	
	@Test
	public void gotoLabelIfstmtTest2() {
		
		String s =	"if true \n" 
				+ 	"	then \n" 
				+	"		1 \n" 
				+	"	else \n" 
				+	"		2 \n";

		runTest(s);
		
	}
	
	private void runTest(String s) {
		ArrayList<String> strs = new ArrayList<>();
		strs.add(s);
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs,
				new ArrayList<DSL>());
		List<Statement> statements = getResult(pair);;
		System.out.println("Instructions:");
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
