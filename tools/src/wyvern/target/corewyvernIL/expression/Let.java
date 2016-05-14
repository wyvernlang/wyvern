package wyvern.target.corewyvernIL.expression;

import static wyvern.tools.errors.ToolError.reportError;

import java.io.IOException;
import java.util.Set;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.VarBinding;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIREnvironment;
import wyvern.tools.errors.ErrorMessage;

public class Let extends Expression {
	private VarBinding binding;
	private Expression inExpr;

	public Let(String varName, ValueType type, Expression toReplace, Expression inExpr) {
		this(new VarBinding(varName, type, toReplace), inExpr);
	}

	public Let(VarBinding binding, Expression inExpr) {
		super();
		this.binding = binding;
		if (inExpr == null) throw new RuntimeException();
		this.inExpr = inExpr;
	}

	public String getVarName() {
		return binding.getVarName();
	}

	public Expression getToReplace() {
		return binding.getExpression();
	}

	public Expression getInExpr() {
		return inExpr;
	}

	@Override
	public ValueType typeCheck(TypeContext ctx) {
		ValueType t = getToReplace().typeCheck(ctx);
		if (!t.isSubtypeOf(binding.getType(), ctx)) {
			reportError(ErrorMessage.NOT_SUBTYPE, this, t.toString(), binding.getType().toString());
		}
		final TypeContext extendedCtx = ctx.extend(getVarName(), binding.getType());
		final ValueType exprType = inExpr.typeCheck(extendedCtx);
		final ValueType cleanExprType = exprType.avoid(binding.getVarName(), extendedCtx);
		//cleanExprType.checkWellFormed(ctx);
		this.setExprType(cleanExprType);
		return getExprType();
	}

	@Override
	public void doPrettyPrint(Appendable dest, String indent) throws IOException {
		String newIndent = indent + "    ";
		dest.append("let\n");
		
		binding.doPrettyPrint(dest, newIndent);
		
		dest.append(indent).append("in ");
		inExpr.doPrettyPrint(dest,indent);
	}

	@Override
	public <T> T acceptVisitor(ASTVisitor <T> emitILVisitor,
			Environment env, OIREnvironment oirenv) {
		return emitILVisitor.visit(env, oirenv, this);
	}

	@Override
	public Value interpret(EvalContext ctx) {
		Value v = getToReplace().interpret(ctx);
		return inExpr.interpret(ctx.extend(getVarName(), v));
	}

	@Override
	public Set<String> getFreeVariables() {
		// Get free variables in the sub-expressions.
		Set<String> freeVars = inExpr.getFreeVariables();
		// Remove the name that just became bound.
		freeVars.remove(getVarName());
		freeVars.addAll(getToReplace().getFreeVariables());
		return freeVars;
	}
}
