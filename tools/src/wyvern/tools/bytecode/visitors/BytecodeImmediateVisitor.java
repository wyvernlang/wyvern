package wyvern.tools.bytecode.visitors;

import wyvern.targets.Common.WyvernIL.Imm.BoolValue;
import wyvern.targets.Common.WyvernIL.Imm.IntValue;
import wyvern.targets.Common.WyvernIL.Imm.StringValue;
import wyvern.targets.Common.WyvernIL.Imm.TupleValue;
import wyvern.targets.Common.WyvernIL.Imm.UnitValue;
import wyvern.targets.Common.WyvernIL.Imm.VarRef;
import wyvern.targets.Common.WyvernIL.visitor.OperandVisitor;
import wyvern.tools.bytecode.core.BytecodeContext;
import wyvern.tools.bytecode.values.BytecodeInt;
import wyvern.tools.bytecode.values.BytecodeString;
import wyvern.tools.bytecode.values.BytecodeValue;

public class BytecodeImmediateVisitor implements OperandVisitor<BytecodeValue> {

	private final BytecodeContext context;
	private final BytecodeOperandVisitor visitor;
	
	public BytecodeImmediateVisitor(BytecodeContext c) {
		context = c;
		visitor = new BytecodeOperandVisitor(context);
	}
	
	@Override
	public BytecodeValue visit(BoolValue boolValue) {
		return boolValue.accept(visitor);
	}

	@Override
	public BytecodeValue visit(IntValue intValue) {
		return intValue.accept(visitor);
	}

	@Override
	public BytecodeValue visit(VarRef varRef) {
		return varRef.accept(visitor);
	}

	@Override
	public BytecodeValue visit(StringValue stringValue) {
		return stringValue.accept(visitor);
	}

	@Override
	public BytecodeValue visit(TupleValue tupleValue) {
		return tupleValue.accept(visitor);
	}

	@Override
	public BytecodeValue visit(UnitValue unitValue) {
		return unitValue.accept(visitor);
	}

}
