package wyvern.tools.bytecode.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wyvern.targets.Common.WyvernIL.Stmt.Label;
import wyvern.targets.Common.WyvernIL.Stmt.Statement;
import wyvern.tools.bytecode.values.BytecodeEmptyVal;
import wyvern.tools.bytecode.values.BytecodeTuple;
import wyvern.tools.bytecode.values.BytecodeValue;
import wyvern.tools.bytecode.visitors.BytecodeStatementVisitor;

/**
 * an intermediate level interpreter
 * @author Tal Man
 *
 */
public class Interpreter {

	private final BytecodeValue emptyVal;
	private BytecodeContext currentContext;
	private List<Statement> statements;
	private Map<Integer, Integer> labels; // Label id -> pc of next instruction
	private String finalName;
	private BytecodeValue finalValue;
	private boolean finalValuesSet;
	private int pc; 

	/**
	 * sets up the interpreter with a list of statements to execute and a
	 * starting context
	 * @param body
	 * 		a list of statements to be executed by the interpreter
	 * @param context
	 * 		the starting context at the time of execution
	 */
	public Interpreter(List<Statement> body, BytecodeContext context) {
		emptyVal = new BytecodeTuple(new ArrayList<BytecodeValue>());
		currentContext = context;
		statements = body;
		pc = 0;
		setUpLabels();
		finalValue = new BytecodeEmptyVal();
	}

	/**
	 * sets up the interpreter with a list of statements to execute and an
	 * empty starting context
	 * @param s
	 * 		a list of statements to be executed by the interpreter
	 */
	public Interpreter(List<Statement> s) {
		this(s, new BytecodeContextImpl());
	}

	/**
	 * runs over all the statements and creates a dictionary of all the
	 * labels created in them for future use
	 */
	private void setUpLabels() {
		labels = new HashMap<Integer, Integer>();
		int labelLocation = 0;
		for (Statement statement : statements) {
			labelLocation++;
			if (statement instanceof Label) {
				Label label = (Label) statement;
				labels.put(label.getIdx(), labelLocation);
			}
		}
	}

	/**
	 * runs the interpreter and returns the last value encountered
	 * @return
	 * 		a BytecodeValue object representing the last value encountered
	 */
/*	public BytecodeValue execute() {
			while (pc < statements.size()) {
				step();
			}
			return finalValue;
	}*/
	
	public BytecodeValue execute() {
		try {
			while (pc < statements.size()) {
				step();
			}
			return finalValue;
		} catch(RuntimeException e) {
			//System.err.println(e.getMessage());
			//System.err.println(statements.get(--pc));
			//System.err.println("~~~~~~~~");
			//System.err.println(currentContext);
			throw e;
		}
	}
	
	/**
	 * executes a single step in the interpreter
	 * @return
	 * 		the result of this single step
	 */
	public BytecodeValue step() {
		finalValuesSet = false;
		Statement statement = statements.get(pc++);
		BytecodeStatementVisitor visitor;
		visitor = new BytecodeStatementVisitor(currentContext, this);
		currentContext = statement.accept(visitor);
		setFinalVals(emptyVal,"unit"); // if it wasn't set to something else
		return finalValue;
	}

	/*
	 * for debugging purposes, prints the final result and the context
	 */
	public void printContext() {
		System.out.println("\nFinal result:");
		System.out.println(finalName + ": " + finalValue);
		System.out.println("\nSimplified context:");
		System.out.println(currentContext.toSimpleString());
	}

	/**
	 * sets the final values of the execution for this past statement, but
	 * this method will only set the values once per instruction
	 * @param val
	 * 		the final value to be set
	 * @param name
	 * 		the name associated with that final value
	 */
	public void setFinalVals(BytecodeValue val, String name) {
		if(!finalValuesSet) {
			finalValue = val;
			finalName = name;
			finalValuesSet = true;
		}
	}
	
	/*
	 * gets the current working context used only for tests
	 */
	public BytecodeContext getCurrentContext() {
		return currentContext;
	}

	/**
	 * returns the program counter for the instruction after a label
	 * @param labelID
	 * 		the id of the label in question
	 * @return
	 * 		the pc for the next instruction after it
	 */
	public int getLabelPC(int labelID) {
		return labels.get(labelID);
	}

	/**
	 * set the pc to a new value
	 * @param newPc
	 * 		the new value of the pc
	 */
	public void setProgramCounter(int newPc) {
		pc = newPc;
	}
	
	/*
	 *	causes the interpreter to finish executing, currently only used by
	 *	the Return case of the statement visitor which is unused in the current
	 *	IL implementation
	 */
	public void endExecution() {
		pc = statements.size() + 1;
	}

	/**
	 * gets the current pc
	 * @return
	 * 		the current pc
	 */
	public int getProgramCounter() {
		return pc;
	}
}
