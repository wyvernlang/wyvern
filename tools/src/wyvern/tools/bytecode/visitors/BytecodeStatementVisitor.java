package wyvern.tools.bytecode.visitors;

import wyvern.targets.Common.WyvernIL.Expr.Immediate;
import wyvern.targets.Common.WyvernIL.Imm.VarRef;
import wyvern.targets.Common.WyvernIL.Stmt.Assign;
import wyvern.targets.Common.WyvernIL.Stmt.Defn;
import wyvern.targets.Common.WyvernIL.Stmt.Goto;
import wyvern.targets.Common.WyvernIL.Stmt.IfStmt;
import wyvern.targets.Common.WyvernIL.Stmt.Label;
import wyvern.targets.Common.WyvernIL.Stmt.Pure;
import wyvern.targets.Common.WyvernIL.Stmt.Return;
import wyvern.targets.Common.WyvernIL.visitor.StatementVisitor;
import wyvern.tools.bytecode.core.BytecodeContext;
import wyvern.tools.bytecode.core.BytecodeContextImpl;
import wyvern.tools.bytecode.core.Interperter;
import wyvern.tools.bytecode.values.BytecodeLabel;
import wyvern.tools.bytecode.values.BytecodeRef;
import wyvern.tools.bytecode.values.BytecodeValue;

public class BytecodeStatementVisitor implements
		StatementVisitor<BytecodeContext> {

	private final BytecodeContext context;
	private final BytecodeExnVisitor visitor;
	private final Interperter interperter;

	public BytecodeStatementVisitor(BytecodeContext c, Interperter i) {
		context = c;
		interperter = i;
		visitor = new BytecodeExnVisitor(context);
	}

	// assumption: 
	// dest will always be an Immediate with expression of type VarRef
	@Override
	public BytecodeContext visit(Assign assign) {
		Immediate imm = (Immediate) assign.getDest();
		VarRef ref = (VarRef) imm.getInner();
		BytecodeRef dst = (BytecodeRef) context.getValue(ref.getName());
		BytecodeValue src = assign.getSrc().accept(visitor);
		dst.setValue(src);
		return context;
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
