package wyvern.target.corewyvernIL.expression;

import java.io.IOException;

import wyvern.target.corewyvernIL.IASTNode;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;

public interface Path extends IASTNode, IExpr {
    /**
     * Returns a path that is equivalent to this path
     * under the View v.  If v maps x to y.f, for example,
     * then if this path is of the form x.g, adapt(v) will
     * return y.f.g
     */
    Path adapt(View v);
    void doPrettyPrint(Appendable dest, String indent) throws IOException;
    /** replaces the underlying variable with the gen expression, if one exists */
    Path adaptVariables(GenContext ctx);
    boolean hasFreeVariable(String name);
    /** converts variables without a binding site to ones with a binding site, if possible */
    void canonicalize(TypeContext ctx);
}
