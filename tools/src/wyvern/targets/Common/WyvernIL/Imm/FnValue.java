package wyvern.targets.Common.WyvernIL.Imm;

import java.util.List;

import wyvern.targets.Common.WyvernIL.Stmt.Statement;
import wyvern.targets.Common.WyvernIL.visitor.OperandVisitor;

public class FnValue implements Operand {
	private List<Operand> args;
	private List<Statement> body;
	
	public FnValue(List<Operand> args, List<Statement> body) {
		this.args = args;
		this.body = body;;
	}

	@Override
	public <R> R accept(OperandVisitor<R> visitor) {
		return visitor.visit(this);
	}

	public List<Operand> getArgs() {
		return args;
	}

	public List<Statement> getBody() {
		return body;
	}

}
