package wyvern.tools.bytecode.visitors;

import java.util.ArrayList;
import java.util.List;

import wyvern.targets.Common.WyvernIL.Expr.BinOp;
import wyvern.targets.Common.WyvernIL.Expr.FnInv;
import wyvern.targets.Common.WyvernIL.Expr.Immediate;
import wyvern.targets.Common.WyvernIL.Expr.Inv;
import wyvern.targets.Common.WyvernIL.Expr.New;
import wyvern.targets.Common.WyvernIL.Imm.Operand;
import wyvern.targets.Common.WyvernIL.visitor.ExprVisitor;
import wyvern.tools.bytecode.core.BytecodeContext;
import wyvern.tools.bytecode.values.BytecodeFunction;
import wyvern.tools.bytecode.values.BytecodeTuple;
import wyvern.tools.bytecode.values.BytecodeValue;

public class BytecodeExnVisitor implements ExprVisitor<BytecodeValue> {

	private final BytecodeContext context;
	private final BytecodeOperandVisitor opVisitor;
	
	public BytecodeExnVisitor(BytecodeContext c) {
		context = c;
		opVisitor = new BytecodeOperandVisitor(context);
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
		BytecodeValue left = l.accept(opVisitor);
		BytecodeValue right = r.accept(opVisitor);
		return left.doInvoke(right, op);
	}

	@Override
	public BytecodeValue visit(FnInv fnInv) {
		BytecodeFunction fun;
		List<BytecodeValue> unpacked;
		fun = (BytecodeFunction) fnInv.getFn().accept(opVisitor);
		BytecodeValue arguments = fnInv.getArg().accept(opVisitor);
		if(arguments instanceof BytecodeTuple) {
			unpacked = ((BytecodeTuple) arguments).getValue();
		} else {
			unpacked = new ArrayList<BytecodeValue>();
			unpacked.add(arguments);
		}
		return fun.run(unpacked);
	}

	@Override
	public BytecodeValue visit(Immediate immediate) {
		Operand inner = immediate.getInner();
		return inner.accept(new BytecodeImmediateVisitor(context));
	}

	@Override
	public BytecodeValue visit(New aNew) {
		// TODO Auto-generated method stub
		throw new RuntimeException("trying to use New");
	}

}
