package wyvern.target.corewyvernIL.expression;

import java.io.IOException;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;

public class Let extends Expression {

	@Override
	public String toString() {
		return "Let[" + varName + " = " + toReplace + " in " + inExpr + "]";
	}

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
	
	public void setVarName(String varName) {
		this.varName = varName;
	}
	
	public Expression getToReplace() {
		return toReplace;
	}
	
	public void setToReplace(Expression toReplace) {
		this.toReplace = toReplace;
	}
	
	public Expression getInExpr() {
		return inExpr;
	}
	
	public void setInExpr(Expression inExpr) {
		this.inExpr = inExpr;
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
}
