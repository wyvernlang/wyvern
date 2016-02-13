package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;

public class ReceiverView extends View {
	private Variable from;
	private Path to;

	public ReceiverView(IExpr e, TypeContext ctx) {
		if (e instanceof Variable) {
			to = (Variable) e;
		} else {
			to = null;
		}
		ValueType vt = e.typeCheck(ctx);
		StructuralType st = vt.getStructuralType(ctx);
		if (st != null) {
			from = new Variable(st.getSelfName());
		} else {
			from = null;
		}
	}

	public ReceiverView(Variable from, Path to) {
		this.from = from;
		this.to = to;
	}
	
	@Override
	public Path adapt(Variable v) {
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
