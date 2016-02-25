package wyvern.target.corewyvernIL.expression;

import java.io.IOException;
import java.util.Set;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIREnvironment;

public class Let extends Expression {

	private String varName;
	private Expression toReplace;
	private Expression inExpr;

	public Let(String varName, Expression toReplace, Expression inExpr) {
		super();
		this.varName = varName;
		this.toReplace = toReplace;
		if (inExpr == null) throw new RuntimeException();
		if (toReplace == null) throw new RuntimeException();
		this.inExpr = inExpr;
	}

	public String getVarName() {
		return varName;
	}

	public Expression getToReplace() {
		return toReplace;
	}

	public Expression getInExpr() {
		return inExpr;
	}

	@Override
	public ValueType typeCheck(TypeContext ctx) {
		ValueType t = toReplace.typeCheck(ctx);
		this.setExprType(inExpr.typeCheck(ctx.extend(varName, t)));
		return getExprType();
	}

	@Override
	public void doPrettyPrint(Appendable dest, String indent) throws IOException {
		String newIndent = indent + "    ";
		dest.append("let\n").append(newIndent)
		.append(varName).append(" = ");
		toReplace.doPrettyPrint(dest,newIndent);
		dest.append('\n').append(indent).append("in ");
		inExpr.doPrettyPrint(dest,indent);
	}

	@Override
	public <T> T acceptVisitor(ASTVisitor <T> emitILVisitor,
			Environment env, OIREnvironment oirenv) {
		return emitILVisitor.visit(env, oirenv, this);
	}

	@Override
	public Value interpret(EvalContext ctx) {
		Value v = toReplace.interpret(ctx);
		return inExpr.interpret(ctx.extend(varName, v));
	}

	@Override
	public Set<String> getFreeVariables() {
		
		// Get free variables in the sub-expressions.
		Set<String> freeVars = toReplace.getFreeVariables();
		freeVars.addAll(inExpr.getFreeVariables());
		
		// Remove the name that just became bound.
		freeVars.remove(varName);
		return freeVars;
	}
}
