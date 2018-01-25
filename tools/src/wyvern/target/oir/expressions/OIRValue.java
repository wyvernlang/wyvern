package wyvern.target.oir.expressions;

import wyvern.target.oir.EmitLLVM;
import wyvern.target.oir.OIREnvironment;
import wyvern.target.oir.declarations.OIRType;

public interface OIRValue extends EmitLLVM {
    OIRType typeCheck(OIREnvironment oirEnv);
}
