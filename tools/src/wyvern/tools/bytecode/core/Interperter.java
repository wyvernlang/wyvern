package wyvern.tools.bytecode.core;

import java.util.List;

import wyvern.targets.Common.WyvernIL.Stmt.Statement;
import wyvern.tools.bytecode.visitors.BytecodeStatementVisitor;

public class Interperter {

	private BytecodeContext currentContext;
	private List<Statement> statements;
	private int pc;

	public Interperter(List<Statement> s) {
		currentContext = new EmptyContext();
		statements = s;
		pc = 0;
	}

	public void execute() {
		for (; pc < statements.size(); pc++) {
			Statement statement = statements.get(pc);
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
	
	public void setProgramCounter(int newPc) {
		pc = newPc;
	}
	
	public int getProgramCounter() {
		return pc;
	}
}
