package wyvern.target.corewyvernIL.type;

import java.util.LinkedList;
import java.util.List;

import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;

public class DataType extends TagType {

	private List<NominalType> cases;

	public DataType(NominalType parentType, ValueType valueType, List<NominalType> cases) {
		super(parentType, valueType);
		this.cases = cases;
	}

	public List<NominalType> getCases ()
	{
		return cases;
	}

	@Override
	public void checkWellFormed(TypeContext ctx) {
		super.checkWellFormed(ctx);
		for (NominalType t:cases) {
			t.checkWellFormed(ctx);
		}
	}
	
	public <S, T> T acceptVisitor (ASTVisitor<S, T> emitILVisitor,
	                                 S state) {
	    return emitILVisitor.visit(state, this);
	}

	@Override
	public Type adapt(View v) {
		NominalType newCT = (NominalType)getParentType(v);
		List<NominalType> newCases = new LinkedList<NominalType>();
		for (NominalType t : cases) {
			newCases.add((NominalType)t.adapt(v));
		}
		return new DataType(newCT, getValueType().adapt(v), newCases);
	}
	
	@Override
	public Type doAvoid(String varName, TypeContext ctx, int depth) {
		NominalType newCT = parentType != null ? (NominalType)parentType.doAvoid(varName, ctx, depth) : null;
		List<NominalType> newCases = new LinkedList<NominalType>();
		for (NominalType t : cases) {
			newCases.add((NominalType)t.doAvoid(varName, ctx, depth));
		}
		return new DataType(newCT, getValueType().doAvoid(varName, ctx, depth), newCases);
	}
}
