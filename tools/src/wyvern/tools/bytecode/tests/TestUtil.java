package wyvern.tools.bytecode.tests;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;

import wyvern.DSL.DSL;
import wyvern.targets.Common.WyvernIL.ExnFromAST;
import wyvern.targets.Common.WyvernIL.TLFromAST;
import wyvern.targets.Common.WyvernIL.Def.Def.Param;
import wyvern.targets.Common.WyvernIL.Stmt.Statement;
import wyvern.tools.bytecode.core.BytecodeContext;
import wyvern.tools.bytecode.core.EmptyContext;
import wyvern.tools.bytecode.core.Interperter;
import wyvern.tools.bytecode.values.BytecodeFunction;
import wyvern.tools.bytecode.values.BytecodeValue;
import wyvern.tools.typedAST.core.expressions.New;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.TypedAST;

/**
 * This class defines some helper functions for the tests as well as some
 * common variables and common setUp/tearDown for them
 * 
 * every test extends this class
 * @author Tal
 *
 */
public class TestUtil {

	protected boolean PRINTS_ON = false; 	// add PRINTS_ON = true at the
											// beginning of a test for some
											// instruction/context prints
	protected Interperter interperter;
	protected BytecodeValue func;

	@Before
	public void setUp() throws Exception {
		List<Param> params = new ArrayList<Param>();
		List<Statement> statements = new ArrayList<Statement>();
		func = new BytecodeFunction(params,statements,new EmptyContext(), "");
	}

	@After
	public void tearDown() throws Exception {
		if(PRINTS_ON) {
			System.out.println("\n ================================= \n");
			PRINTS_ON = false;
		}
	}
	
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
	
	public BytecodeValue runTest(String s) {
		ArrayList<String> strs = new ArrayList<>();
		strs.add(s);
		TypedAST pair = wyvern.stdlib.Compiler.compileSources("in1", strs,
				new ArrayList<DSL>());
		List<Statement> statements = getResult(pair);
		
		if(PRINTS_ON) {
			System.out.println("Instructions:");
			for (Statement statement : statements) {
				System.out.println(statement.getClass().getSimpleName() + " : "
						+ statement.toString());
			}
		}

		interperter = new Interperter(statements);
		BytecodeValue res = interperter.execute();
		
		if(PRINTS_ON) {
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
