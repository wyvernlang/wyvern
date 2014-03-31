package wyvern.targets.Common.wyvernIL.IL.visitor;

import wyvern.targets.Common.wyvernIL.IL.Imm.*;

public interface OperandVisitor<R> {
	R visit(BoolValue boolValue);
	R visit(IntValue intValue);
	R visit(VarRef varRef);
	R visit(StringValue stringValue);
	R visit(TupleValue tupleValue);
	R visit(UnitValue unitValue);
}
