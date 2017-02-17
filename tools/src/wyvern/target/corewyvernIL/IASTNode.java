package wyvern.target.corewyvernIL;

import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;

public interface IASTNode {
    public abstract <S, T> T acceptVisitor (ASTVisitor<S, T> visitor, S state);
}
