package wyvern.target.corewyvernIL.expression;

import java.io.IOException;

import wyvern.target.corewyvernIL.EmitOIR;
import wyvern.target.corewyvernIL.support.View;

public interface Path extends EmitOIR, IExpr {
	/**
	 * Returns a path that is equivalent to this path
	 * under the View v.  If v maps x to y.f, for example,
	 * then if this path is of the form x.g, adapt(v) will
	 * return y.f.g
	 */
	Path adapt(View v);
	void doPrettyPrint(Appendable dest, String indent) throws IOException;
}
