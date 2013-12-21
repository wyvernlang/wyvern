package wyvern.tools.bytecode.visitors;

import wyvern.targets.Common.WyvernIL.Imm.BoolValue;
import wyvern.targets.Common.WyvernIL.Imm.IntValue;
import wyvern.targets.Common.WyvernIL.Imm.StringValue;
import wyvern.targets.Common.WyvernIL.Imm.TupleValue;
import wyvern.targets.Common.WyvernIL.Imm.UnitValue;
import wyvern.targets.Common.WyvernIL.Imm.VarRef;
import wyvern.targets.Common.WyvernIL.visitor.OperandVisitor;
import wyvern.tools.bytecode.core.BytecodeContext;
import wyvern.tools.bytecode.values.BytecodeBoolean;
import wyvern.tools.bytecode.values.BytecodeInt;
import wyvern.tools.bytecode.values.BytecodeString;
import wyvern.tools.bytecode.values.BytecodeValue;

public class BytecodeOperandVisitor implements OperandVisitor<BytecodeValue> {

	private final String name;
	private final BytecodeContext context;
	
	public BytecodeOperandVisitor(BytecodeContext c, String n) {
		name = n;
		context = c;
	}
	
	@Override
	public BytecodeValue visit(BoolValue boolValue) {
		return new BytecodeBoolean(boolValue.isValue(),name);
	}

	@Override
	public BytecodeValue visit(IntValue intValue) {
		return new BytecodeInt(intValue.getValue(),name);
	}

	@Override
	public BytecodeValue visit(VarRef varRef) {
		// right now returns (in a mutable way) the variable that is
		// refernced from the this reference
		return context.getValue(varRef.getName());
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
