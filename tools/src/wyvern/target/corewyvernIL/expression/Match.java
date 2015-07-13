package wyvern.target.corewyvernIL.expression;

import java.util.List;

import wyvern.target.corewyvernIL.Case;
import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;

public class Match extends Expression {

	private Expression matchExpr;
	private Expression elseExpr;
	private List<Case> cases;
	
	public Match(Expression matchExpr, Expression elseExpr, List<Case> cases) {
		super();
		this.matchExpr = matchExpr;
		this.elseExpr = elseExpr;
		this.cases = cases;
	}

	public Expression getMatchExpr() {
		return matchExpr;
	}
	
	public void setMatchExpr(Expression matchExpr) {
		this.matchExpr = matchExpr;
	}
	
	public Expression getElseExpr() {
		return elseExpr;
	}
	
	public void setElseExpr(Expression elseExpr) {
		this.elseExpr = elseExpr;
	}
	
	public List<Case> getCases() {
		return cases;
	}
	
	public void setCases(List<Case> cases) {
		this.cases = cases;
	}

	@Override
	public ValueType typeCheck(wyvern.tools.types.Environment env) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T acceptVisitor(ASTVisitor <T> emitILVisitor,
			Environment env, OIREnvironment oirenv) {
		return emitILVisitor.visit(env, oirenv, this);
	}
}
