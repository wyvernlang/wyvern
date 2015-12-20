package wyvern.target.corewyvernIL.expression;

import java.io.IOException;

import wyvern.target.corewyvernIL.EmitOIR;
import wyvern.target.corewyvernIL.support.View;

public interface Path extends EmitOIR, IExpr {

	Path adapt(View v);
	void doPrettyPrint(Appendable dest, String indent) throws IOException;
}
