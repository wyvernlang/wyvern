package wyvern.targets.Common.WyvernIL.visitor;

import wyvern.targets.Common.WyvernIL.Imm.BoolValue;
import wyvern.targets.Common.WyvernIL.Imm.IntValue;
import wyvern.targets.Common.WyvernIL.Imm.VarRef;

public interface OperandVisitor<R> {
	R visit(BoolValue boolValue);
	R visit(IntValue intValue);
	R visit(VarRef varRef);
}
