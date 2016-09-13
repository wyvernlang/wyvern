package wyvern.target.corewyvernIL;

import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.oir.OIREnvironment;

public interface EmitOIR {
    public abstract <S, T> T acceptVisitor (ASTVisitor<S, T> emitILVisitor, S state);
}
