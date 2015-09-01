package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;

public class ReceiverView extends View {
	private Variable from;
	private Variable to;

	public ReceiverView(Expression e, TypeContext ctx) {
		if (e instanceof Variable) {
			to = (Variable) e;
		} else {
			to = null;
		}
		ValueType vt = e.typeCheck(ctx);
		StructuralType st = vt.getStructuralType();
		if (st != null) {
			from = new Variable(st.getSelfName());
		} else {
			from = null;
		}
	}

	@Override
	public Variable adapt(Variable v) {
		if (from == null)
			return v;
		if (v.equals(from)) {
			if (to == null)
				throw new RuntimeException("view adaptation failed");
			return to;
		} else {
			return v;
		}
	}

}
