package wyvern.tools.bytecode.visitors;

import wyvern.targets.Common.WyvernIL.Expr.BinOp;
import wyvern.targets.Common.WyvernIL.Expr.FnInv;
import wyvern.targets.Common.WyvernIL.Expr.Immediate;
import wyvern.targets.Common.WyvernIL.Expr.Inv;
import wyvern.targets.Common.WyvernIL.Expr.New;
import wyvern.targets.Common.WyvernIL.Imm.IntValue;
import wyvern.targets.Common.WyvernIL.Imm.Operand;
import wyvern.targets.Common.WyvernIL.visitor.ExprVisitor;
import wyvern.tools.bytecode.values.BytecodeInt;
import wyvern.tools.bytecode.values.BytecodeValue;

public class BytecodeExnVisitor implements ExprVisitor<BytecodeValue> {

	private final String name;

	public BytecodeExnVisitor(String n) {
		name = n;
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
		// for now:
		IntValue lv = (IntValue) l;
		IntValue rv = (IntValue) r;
		switch (op) {
		case "+":
			return new BytecodeInt(lv.getValue() + rv.getValue(), name);
		}
		System.out.println("nope");
		return null;
	}

	@Override
	public BytecodeValue visit(FnInv fnInv) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BytecodeValue visit(Immediate immediate) {
		Operand inner = immediate.getInner();
		return inner.accept(new BytecodeImmediateVisitor(name));
	}

	@Override
	public BytecodeValue visit(New aNew) {
		// TODO Auto-generated method stub
		return null;
	}

}
