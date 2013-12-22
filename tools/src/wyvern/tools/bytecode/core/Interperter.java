package wyvern.tools.bytecode.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wyvern.targets.Common.WyvernIL.Stmt.Label;
import wyvern.targets.Common.WyvernIL.Stmt.Statement;
import wyvern.tools.bytecode.visitors.BytecodeStatementVisitor;

public class Interperter {

	private BytecodeContext currentContext;
	private List<Statement> statements;
	// Label id -> pc of next instruction
	private Map<Integer,Integer> labels;
	private int pc;

	public Interperter(List<Statement> s) {
		currentContext = new EmptyContext();
		statements = s;
		pc = 0;
		setUpLabels();
	}
	
	private void setUpLabels() {
		labels = new HashMap<Integer,Integer>();
		int labelLocation = 0;
		for(Statement statement : statements) {
			labelLocation++;
			if(statement instanceof Label) {
				Label label = (Label) statement;
				labels.put(label.getIdx(),labelLocation);
			}
		}
	}

	public void execute() {
		while (pc < statements.size()) {
			Statement statement = statements.get(pc++);
			BytecodeStatementVisitor visitor;
			visitor = new BytecodeStatementVisitor(currentContext,this);
			currentContext = statement.accept(visitor);
		}
	}

	public void printContext() {
		System.out.println("\nFinal result: \n"
				+ currentContext.getLastEnteredName() + ": "
				+ currentContext.getLastEnteredValue().toString());
		System.out.println("\nEntire context:");
		System.out.println(currentContext.toString());
	}
	
	public int getLabelPC(int labelID) {
		return labels.get(labelID);
	}
	
	public void setProgramCounter(int newPc) {
		pc = newPc;
	}
	
	public int getProgramCounter() {
		return pc;
	}
}
