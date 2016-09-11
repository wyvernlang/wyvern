package wyvern.target.corewyvernIL.astvisitor;

import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.oir.OIREnvironment;

public class EmitOIRState {
    public TypeContext cxt;
    public OIREnvironment env;

    public EmitOIRState(TypeContext cxt, OIREnvironment env) {
        this.cxt = cxt;
        this.env = env;
    }
}
