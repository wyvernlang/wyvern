package wyvern.target.corewyvernIL.expression;

import wyvern.target.corewyvernIL.EmitOIR;
import wyvern.target.corewyvernIL.support.View;

public interface Path extends EmitOIR {

	Path adapt(View v);
}
