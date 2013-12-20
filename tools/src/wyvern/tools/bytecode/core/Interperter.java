package wyvern.tools.bytecode.core;

import java.util.List;

import wyvern.targets.Common.WyvernIL.Stmt.Statement;
import wyvern.tools.bytecode.visitors.BytecodeStatementVisitor;

public class Interperter {
	
	private BytecodeContext currentContext;
	private List<Statement> statements;
	private int nextStatement;
	
	public Interperter(List<Statement> s) {
		currentContext = new EmptyContext();
		statements = s;
		nextStatement = 0;
	}
	
	public void execute() {
		for( ; nextStatement < statements.size() ; nextStatement++) {
			Statement statement = statements.get(nextStatement);
			BytecodeStatementVisitor visitor = new BytecodeStatementVisitor(currentContext);
			currentContext = statement.accept(visitor);
		}
	}
	
	public void printContext() {
		System.out.println("Final result: " + currentContext.getValue().toString());
		System.out.println("\nEntire context:");
		System.out.println(currentContext.toString());
	}
}
