package wyvern.tools.typedAST.transformers;

import java.util.function.Function;

import wyvern.target.corewyvernIL.ASTNode;

public interface ILWriter {
    void write(ASTNode node);
    void writePrefix(ASTNode node);
    void wrap(Function<ASTNode, ASTNode> wrapper);
}
