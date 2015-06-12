package wyvern.target.corewyvernIL.expression;

import java.util.List;

import wyvern.target.corewyvernIL.Case;
import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.EmitILVisitor;
import wyvern.target.corewyvernIL.type.ValueType;

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
	public java.lang.String acceptEmitILVisitor(EmitILVisitor emitILVisitor,
			Environment env) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ValueType typeCheck(wyvern.tools.types.Environment env) {
		// TODO Auto-generated method stub
		return null;
	}
}
