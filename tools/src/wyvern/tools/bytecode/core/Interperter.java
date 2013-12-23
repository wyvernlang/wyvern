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

public class Interperter {

	private final BytecodeValue emptyVal;
	private BytecodeContext currentContext;
	private List<Statement> statements;
	// Label id -> pc of next instruction
	private Map<Integer, Integer> labels;
	private BytecodeValue finalValue;
	private boolean finalValuesSet;
	private String finalName;
	private int pc;

	public Interperter(List<Statement> s, BytecodeContext c) {
		emptyVal = new BytecodeTuple(new ArrayList<BytecodeValue>());
		currentContext = c;
		statements = s;
		pc = 0;
		setUpLabels();
		finalValue = new BytecodeEmptyVal();
	}

	public Interperter(List<Statement> s) {
		this(s, new EmptyContext());
	}

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

	public BytecodeValue execute() {
		while (pc < statements.size()) {
			finalValuesSet = false;
			Statement statement = statements.get(pc++);
			BytecodeStatementVisitor visitor;
			visitor = new BytecodeStatementVisitor(currentContext, this);
			currentContext = statement.accept(visitor);
			setFinalVals(emptyVal,"unit");
		}
		return finalValue;
	}

	public void printContext() {
		System.out.println("\nFinal result:");
		System.out.println(finalName + ": " + finalValue);
		System.out.println("\nSimplified context:");
		System.out.println(currentContext.toSimpleString());
	}

	public void setFinalVals(BytecodeValue val, String name) {
		if(!finalValuesSet) {
			finalValue = val;
			finalName = name;
			finalValuesSet = true;
		}
	}
	
	public BytecodeContext getCurrentContext() {
		return currentContext;
	}

	public int getLabelPC(int labelID) {
		return labels.get(labelID);
	}

	public void setProgramCounter(int newPc) {
		pc = newPc;
	}
	
	public void endExecution() {
		pc = statements.size() + 1;
	}

	public int getProgramCounter() {
		return pc;
	}
}
