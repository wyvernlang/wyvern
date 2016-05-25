package wyvern.target.corewyvernIL;

import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.oir.OIREnvironment;

public interface EmitOIR {
    public abstract <T, E> T acceptVisitor (ASTVisitor<T, E> emitILVisitor, E env,
                                            OIREnvironment oirenv);
}
