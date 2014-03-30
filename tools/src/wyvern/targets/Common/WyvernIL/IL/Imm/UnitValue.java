package wyvern.targets.Common.wyvernIL.IL.Imm;

import wyvern.targets.Common.wyvernIL.IL.visitor.OperandVisitor;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Unit;

public class UnitValue implements Operand {
	
	public UnitValue() {
	}

	@Override
	public <R> R accept(OperandVisitor<R> visitor) {
		return visitor.visit(this);
	}
	
	public Type getType() {
		return Unit.getInstance();
	}
	
	@Override
	public String toString() {
		return "()";
	}
}
