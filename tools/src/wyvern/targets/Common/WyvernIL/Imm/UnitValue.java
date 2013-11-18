package wyvern.targets.Common.WyvernIL.Imm;

import wyvern.targets.Common.WyvernIL.visitor.OperandVisitor;
import wyvern.tools.types.Type;

public class UnitValue implements Operand {
	private Type type;
	
	public UnitValue(Type type) {
		this.type = type;
	}

	@Override
	public <R> R accept(OperandVisitor<R> visitor) {
		return visitor.visit(this);
	}
	
	public Type getType() {
		return this.type;
	}
}
