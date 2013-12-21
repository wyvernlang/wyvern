package wyvern.tools.bytecode.visitors;

import wyvern.targets.Common.WyvernIL.Expr.BinOp;
import wyvern.targets.Common.WyvernIL.Expr.FnInv;
import wyvern.targets.Common.WyvernIL.Expr.Immediate;
import wyvern.targets.Common.WyvernIL.Expr.Inv;
import wyvern.targets.Common.WyvernIL.Expr.New;
import wyvern.targets.Common.WyvernIL.Imm.IntValue;
import wyvern.targets.Common.WyvernIL.Imm.Operand;
import wyvern.targets.Common.WyvernIL.visitor.ExprVisitor;
import wyvern.tools.bytecode.core.BytecodeContext;
import wyvern.tools.bytecode.values.BytecodeInt;
import wyvern.tools.bytecode.values.BytecodeValue;

public class BytecodeExnVisitor implements ExprVisitor<BytecodeValue> {

	private final BytecodeContext context;
	
	public BytecodeExnVisitor(BytecodeContext c) {
		context = c;
	}

	@Override
	public BytecodeValue visit(Inv inv) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BytecodeValue visit(BinOp binOp) {
		Operand l = binOp.getL();
		Operand r = binOp.getR();
		String op = binOp.getOp();
		BytecodeValue val = l.accept(new BytecodeOperandVisitor(context));
		return val.doInvoke(r.accept(new BytecodeOperandVisitor(context)), op);
	}

	@Override
	public BytecodeValue visit(FnInv fnInv) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BytecodeValue visit(Immediate immediate) {
		Operand inner = immediate.getInner();
		return inner.accept(new BytecodeImmediateVisitor(context));
	}

	@Override
	public BytecodeValue visit(New aNew) {
		// TODO Auto-generated method stub
		return null;
	}

}
