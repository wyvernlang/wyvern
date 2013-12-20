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

	private final String name;
	
	public BytecodeImmediateVisitor(String n) {
		name = n;
	}
	
	@Override
	public BytecodeValue visit(BoolValue boolValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BytecodeValue visit(IntValue intValue) {
		return new BytecodeInt(intValue.getValue(),name);
	}

	@Override
	public BytecodeValue visit(VarRef varRef) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BytecodeValue visit(StringValue stringValue) {
		return new BytecodeString(stringValue.isValue(),name);
	}

	@Override
	public BytecodeValue visit(TupleValue tupleValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BytecodeValue visit(UnitValue unitValue) {
		// TODO Auto-generated method stub
		return null;
	}

}
