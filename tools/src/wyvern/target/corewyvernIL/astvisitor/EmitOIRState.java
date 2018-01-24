package wyvern.target.corewyvernIL.astvisitor;

import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.oir.OIREnvironment;

public class EmitOIRState {
    private TypeContext ctx;
    private OIREnvironment env;

    public EmitOIRState(TypeContext ctx, OIREnvironment env) {
        this.ctx = ctx;
        this.env = env;
    }

    public TypeContext getContext() {
        return ctx;
    }

    public OIREnvironment getEnvironment() {
        return env;
    }

}
