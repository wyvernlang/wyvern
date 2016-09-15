package wyvern.target.corewyvernIL.expression;

import static wyvern.tools.errors.ToolError.reportError;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.VarBinding;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.support.EmptyTypeContext;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIREnvironment;
import wyvern.tools.errors.ErrorMessage;

public class Bind extends Expression {
	private List<VarBinding> bindings;
	private IExpr inExpr;

	public Bind(List<VarBinding> bindings, IExpr inExpr) {
		super();
		this.bindings = bindings;
		if (inExpr == null) throw new RuntimeException();
		this.inExpr = inExpr;
	}

	public List<String> getVarNames() {
		List<String> varNames = new ArrayList<String>();
		for (VarBinding vb : bindings) {
			varNames.add(vb.getVarName());
		}
		return varNames;
	}

	public List<? extends IExpr> getToReplaceExps() {
		List<IExpr> toReplaceExps = new ArrayList<IExpr>();
		for (VarBinding vb : bindings) {
			toReplaceExps.add(vb.getExpression());
		}
		return toReplaceExps;
	}

    public IExpr getInExpr() {
        return inExpr;
    }

	@Override
	public ValueType typeCheck(TypeContext ctx) {
		TypeContext bodyCtx = EmptyTypeContext.empty();
		for (VarBinding vb : bindings) {
			ValueType t = vb.getExpression().typeCheck(ctx);
			if (!t.isSubtypeOf(vb.getType(), ctx)) {
				reportError(ErrorMessage.NOT_SUBTYPE, this, t.toString(), vb.getType().toString());
			}
			bodyCtx = bodyCtx.extend(vb.getVarName(), vb.getType());
		}
		this.setExprType(inExpr.typeCheck(bodyCtx));
		return getExprType();
	}

	@Override
	public void doPrettyPrint(Appendable dest, String indent) throws IOException {
		String newIndent = indent + "    ";
		dest.append("bind\n");
		for (VarBinding vb : bindings) {
			dest.append(newIndent).append(vb.getVarName()).append(" = ");
			vb.getExpression().doPrettyPrint(dest, newIndent);
			dest.append("\n");
		}
		dest.append("in ");
		inExpr.doPrettyPrint(dest, indent);
	}

	@Override
	public <S, T> T acceptVisitor(ASTVisitor <S, T> emitILVisitor,
			S state) {
		return emitILVisitor.visit(state, this);
	}

	@Override
	public Value interpret(EvalContext ctx) {
		EvalContext evalCtx = ctx;
		for (VarBinding vb : bindings) {
			evalCtx = evalCtx.extend(vb.getVarName(), vb.getExpression().interpret(ctx));
		}
		return inExpr.interpret(evalCtx);
	}

	@Override
	public Set<String> getFreeVariables() {
		// Get free variables in the sub-expressions.
		Set<String> freeVars = inExpr.getFreeVariables();
		// Remove the name that just became bound.
		for (VarBinding vb : bindings) {
			freeVars.remove(vb.getVarName());
			freeVars.addAll(vb.getExpression().getFreeVariables());
		}
		return freeVars;
	}
}
