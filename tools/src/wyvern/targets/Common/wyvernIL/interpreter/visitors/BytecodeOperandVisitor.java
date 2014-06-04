package wyvern.targets.Common.wyvernIL.interpreter.visitors;

import wyvern.targets.Common.wyvernIL.IL.Imm.*;
import wyvern.targets.Common.wyvernIL.IL.visitor.OperandVisitor;
import wyvern.targets.Common.wyvernIL.interpreter.core.BytecodeContext;
import wyvern.targets.Common.wyvernIL.interpreter.values.*;

import java.util.ArrayList;
import java.util.List;

/**
 * an OperandVisitor for the IL interpreter
 * @author Tal Man
 *
 */
public class BytecodeOperandVisitor implements OperandVisitor<BytecodeValue> {

	private final BytecodeContext context;
	
	/**
	 * sets up the visitor with a context to work with
	 * @param visContext
	 * 		the context of the program at this point
	 */
	public BytecodeOperandVisitor(BytecodeContext visContext) {
		context = visContext;
	}
	
	@Override
	public BytecodeValue visit(BoolValue boolValue) {
		return new BytecodeBoolean(boolValue.isValue());
	}

	@Override
	public BytecodeValue visit(IntValue intValue) {
		return new BytecodeInt(intValue.getValue());
	}

	@Override
	public BytecodeValue visit(VarRef varRef) {
		// probably going to rework this after i understand the system better
		return context.getValue(varRef.getName()).dereference();
	}

	@Override
	public BytecodeValue visit(StringValue stringValue) {
		return new BytecodeString(stringValue.isValue());
	}

	@Override
	public BytecodeValue visit(TupleValue tupleValue) {
		List<Operand> operands = tupleValue.getOperands();
		List<BytecodeValue> values = new ArrayList<BytecodeValue>();
		for(Operand op : operands) {
			values.add(op.accept(this));
		}
		return new BytecodeTuple(values);
	}

	/*
	 *  unit value represented as an empty tuple
	 */
	@Override
	public BytecodeValue visit(UnitValue unitValue) {
		return new BytecodeTuple(new ArrayList<BytecodeValue>());
	}

}
