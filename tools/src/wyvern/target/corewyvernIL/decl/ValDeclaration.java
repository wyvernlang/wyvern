package wyvern.target.corewyvernIL.decl;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.ValDeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIREnvironment;

public class ValDeclaration extends DeclarationWithRHS {

	/*@Override
	public String toString() {
		return "ValDeclaration[" + getName() + " : " + type + " = " + getDefinition() + "]";
	}*/

	public ValDeclaration(String fieldName, ValueType type, Expression value) {
		super(fieldName, value);
		this.type = type;
	}

	private ValueType type;

	@Override
	public ValueType getType() {
		return type;
	}

	@Override
	public void doPrettyPrint(Appendable dest, String indent) throws IOException {
		dest.append(indent).append("val ").append(getName()).append(':');
		type.doPrettyPrint(dest, indent);
		dest.append(" = ");
		getDefinition().doPrettyPrint(dest, indent);
		dest.append('\n');
	}

	@Override
	public <T> T acceptVisitor(ASTVisitor <T> emitILVisitor,
			Environment env, OIREnvironment oirenv) {
		return emitILVisitor.visit(env, oirenv, this);
	}

	@Override
	public DeclType getDeclType() {
		return new ValDeclType(getName(), type);
	}

	@Override
	public Declaration interpret(EvalContext ctx) {
		Expression newValue = (Expression) getDefinition().interpret(ctx);
		return new ValDeclaration(getName(), type, newValue);
	}

	public Set<String> getFreeVariables() {
		Set<String> freeVars = new HashSet<>();
		freeVars.add(this.getName());
		return freeVars;
	}

}
