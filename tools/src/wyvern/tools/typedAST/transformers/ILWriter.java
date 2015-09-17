package wyvern.tools.typedAST.transformers;

import wyvern.target.corewyvernIL.ASTNode;

import java.util.function.Function;

public interface ILWriter {
    void write(ASTNode node);
    void writePrefix(ASTNode node);
    void wrap(Function<ASTNode, ASTNode> wrapper);
}
