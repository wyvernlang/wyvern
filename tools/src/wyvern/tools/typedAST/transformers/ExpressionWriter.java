package wyvern.tools.typedAST.transformers;

import wyvern.target.corewyvernIL.ASTNode;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.tools.errors.WyvernException;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by Ben Chung on 6/24/2015.
 */
public class ExpressionWriter implements ILWriter {
    private Expression output = null;
    private LinkedList<Function<ASTNode, ASTNode>> wrappers = new LinkedList<>();

    private ExpressionWriter() {}

    @Override
    public void write(ASTNode node) {
        output = (Expression)node;
    }

    @Override
    public void writePrefix(ASTNode node) {
        output = new Let(GenerationEnvironment.generateVariableName(), (Expression)node, output);
    }

    @Override
    public void wrap(Function<ASTNode, ASTNode> wrapper) {
        wrappers.push(wrapper);
    }

    public static Expression generate(Consumer<ExpressionWriter> fn) {
        ExpressionWriter writer = new ExpressionWriter();
        fn.accept(writer);
        for (Function<ASTNode, ASTNode> fun : writer.wrappers)
            writer.output = (Expression)fun.apply(writer.output);
        return writer.output;
    }
}
