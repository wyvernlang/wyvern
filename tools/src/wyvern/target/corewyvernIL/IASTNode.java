package wyvern.target.corewyvernIL;

import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.oir.OIREnvironment;

public interface IASTNode {
    public abstract <S, T> T acceptVisitor (ASTVisitor<S, T> visitor, S state);
}
