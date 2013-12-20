package wyvern.targets.Common.WyvernIL.Imm;

import wyvern.targets.Common.WyvernIL.visitor.OperandVisitor;

public class VarRef implements Operand {

	private String name;

	public VarRef (String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public <R> R accept(OperandVisitor<R> visitor) {
		return visitor.visit(this);
	}
	
	@Override
	public String toString() {
		return name;
	}
}
