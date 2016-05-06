package wyvern.target.corewyvernIL;

import java.io.IOException;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.type.ValueType;

public class VarBinding extends ASTNode {
	private String varName;
	private ValueType type;
	private Expression expr;
	
	public VarBinding(String varName, ValueType type, Expression expr) {
		this.varName = varName;
		this.type = type;
		this.expr = expr;
		if (expr == null) throw new RuntimeException();
	}

	public String getVarName() {
		return varName;
	}

	public ValueType getType() {
		return type;
	}

	public Expression getExpression() {
		return expr;
	}
	
	public void doPrettyPrint(Appendable dest, String indent) throws IOException {
		dest.append(indent).append(getVarName()).append(" : ");
		getType().doPrettyPrint(dest, indent);
		dest.append(" = ");
		getExpression().doPrettyPrint(dest,indent);
		dest.append('\n');
	}
}
