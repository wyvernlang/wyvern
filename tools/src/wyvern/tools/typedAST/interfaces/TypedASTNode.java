package wyvern.tools.typedAST.interfaces;

import wyvern.tools.typedAST.typedastvisitor.TypedASTVisitor;

public interface TypedASTNode {
    <S, T> T acceptVisitor(TypedASTVisitor<S, T> visitor, S state);
}
