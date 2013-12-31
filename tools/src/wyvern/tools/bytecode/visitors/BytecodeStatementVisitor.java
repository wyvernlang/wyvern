package wyvern.tools.bytecode.visitors;

import wyvern.targets.Common.WyvernIL.Expr.Expression;
import wyvern.targets.Common.WyvernIL.Expr.Immediate;
import wyvern.targets.Common.WyvernIL.Expr.Inv;
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
import wyvern.tools.bytecode.core.Interpreter;
import wyvern.tools.bytecode.values.BytecodeBoolean;
import wyvern.tools.bytecode.values.BytecodeClass;
import wyvern.tools.bytecode.values.BytecodeRef;
import wyvern.tools.bytecode.values.BytecodeValue;

public class BytecodeStatementVisitor implements
		StatementVisitor<BytecodeContext> {

	private final BytecodeContext context;
	private final BytecodeExnVisitor visitor;
	private final Interpreter interperter;
	private static final String UNSAVED_MESSAGE = "unsaved in context"; 

	/**
	 * sets up the visitor with a context to work with
	 * @param visContext
	 * 		the context of the program at this point
	 * @param visInterperter
	 * 		the interpreter that is currently being executed
	 */
	public BytecodeStatementVisitor(BytecodeContext visContext, Interpreter visInterperter) {
		context = visContext;
		interperter = visInterperter;
		visitor = new BytecodeExnVisitor(context);
	}

	/*
	 * assumption: 
	 * dest will always be an Immediate with expression of type VarRef
	 * or an Inv that will lead to a BytecodeRef in a class
	 */
	@Override
	public BytecodeContext visit(Assign assign) {
		Expression destExpr = assign.getDest();
		BytecodeRef dest;
		if(destExpr instanceof Inv) {
			Inv inv = (Inv) destExpr;
			BytecodeOperandVisitor opVisitor = new BytecodeOperandVisitor(context);
			BytecodeClass clas = (BytecodeClass) inv.getSource().accept(opVisitor);
			BytecodeValue val = clas.getContext().getValue(inv.getId());
			dest = (BytecodeRef) val;
		} else if(destExpr instanceof Immediate) {
			Immediate imm = (Immediate) destExpr;
			VarRef ref = (VarRef) imm.getInner();
			dest = (BytecodeRef) context.getValue(ref.getName());
		} else {
			throw new RuntimeException("assignment not using Inv or Imm");
		}
		BytecodeValue src = assign.getSrc().accept(visitor);
		dest.setValue(src);
		return context;
	}

	@Override
	public BytecodeContext visit(Defn defn) {
		return defn.getDefinition().accept(new BytecodeDefVisitor(context));
	}

	@Override
	public BytecodeContext visit(Goto aGoto) {
		int id = aGoto.getLabel().getIdx();
		interperter.setProgramCounter(interperter.getLabelPC(id));
		return context;
	}

	@Override
	public BytecodeContext visit(Label label) {
		return context;
	}

	@Override
	public BytecodeContext visit(Pure pure) {
		Expression expression = pure.getExpression();
		if(expression != null) {
			interperter.setFinalVals(expression.accept(visitor),UNSAVED_MESSAGE);
		}
		return context;
	}

	/*
	 * currently unused in implementation (compiler doesn't generate Return)
	 * therefore this method has never been tested
	 */
	@Override
	public BytecodeContext visit(Return aReturn) {
		interperter.endExecution();
		BytecodeOperandVisitor opVisitor = new BytecodeOperandVisitor(context);
		BytecodeValue val = aReturn.getExn().accept(opVisitor);
		interperter.setFinalVals(val, UNSAVED_MESSAGE);
		return context;
	}

	@Override
	public BytecodeContext visit(IfStmt ifStmt) {
		BytecodeValue val = ifStmt.getCondition().accept(visitor);
		BytecodeBoolean bool = (BytecodeBoolean) val;
		if(bool.getValue()) {
			int id = ifStmt.getLabel().getIdx();
			int newPC = interperter.getLabelPC(id);
			interperter.setProgramCounter(newPC);
		}
		return context;
	}

}
