package wyvern.tools.bytecode.visitors;

import wyvern.targets.Common.WyvernIL.Stmt.Assign;
import wyvern.targets.Common.WyvernIL.Stmt.Defn;
import wyvern.targets.Common.WyvernIL.Stmt.Goto;
import wyvern.targets.Common.WyvernIL.Stmt.IfStmt;
import wyvern.targets.Common.WyvernIL.Stmt.Label;
import wyvern.targets.Common.WyvernIL.Stmt.Pure;
import wyvern.targets.Common.WyvernIL.Stmt.Return;
import wyvern.targets.Common.WyvernIL.visitor.StatementVisitor;
import wyvern.tools.bytecode.core.BytecodeContext;

public class BytecodeStatementVisitor implements StatementVisitor<BytecodeContext> {

	private final BytecodeContext context;
	
	public BytecodeStatementVisitor(BytecodeContext c) {
		context = c;
	}
	
	@Override
	public BytecodeContext visit(Assign assign) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BytecodeContext visit(Defn defn) {
		return defn.getDefinition().accept(new BytecodeDefVisitor(context));
	}

	@Override
	public BytecodeContext visit(Goto aGoto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BytecodeContext visit(Label label) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BytecodeContext visit(Pure pure) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BytecodeContext visit(Return aReturn) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BytecodeContext visit(IfStmt ifStmt) {
		// TODO Auto-generated method stub
		return null;
	}

}
