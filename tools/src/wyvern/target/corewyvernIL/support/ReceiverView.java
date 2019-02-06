package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.expression.FieldGet;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;

public class ReceiverView extends View {
    private Variable from;
    private Path to;
    private TypeContext ctx;

    public ReceiverView(IExpr e, TypeContext ctx) {
        this.ctx = ctx;
        if (e instanceof Variable) {
            to = (Variable) e;
        } else if (e instanceof FieldGet) {
            to = (FieldGet) e;
        } else {
            to = null;
        }
        ValueType vt = e.typeCheck(ctx, null);
        StructuralType st = vt.getStructuralType(ctx);
        if (st != null) {
            if (st.getSelfSite() == null) {
                from = new Variable(st.getSelfName());
            } else {
                from = new Variable(st.getSelfSite());
            }
        } else {
            from = null;
        }
        normalize();
    }

    private void normalize() {
        if (from != null && to != null && to instanceof Variable
                && from.getSite() == ((Variable) to).getSite() && from.getName().equals(((Variable) to).getName())) {
            from = null; // empty transformation
        }
    }

    public ReceiverView(Variable from, Path to) {
        this.from = from;
        this.to = to;
        normalize();
    }

    @Override
    public Path adapt(Variable v) {
        if (from == null) {
            return v;
        }
        if (v.equals(from)) {
            if (to == null) {
                return v;
            }
            return to;
        } else {
            return v;
        }
    }

    @Override
    public String toString() {
        return "ReceiverView(" + from + " => " + to + ')';
    }

    @Override
    public TypeContext getContext() {
        return ctx;
    }
}
