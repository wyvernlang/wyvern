package wyvern.tools.typedAST.transformers;


import wyvern.target.corewyvernIL.ASTNode;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.WyvernException;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class DeclarationWriter implements ILWriter {
    private LinkedList<Declaration> decls = new LinkedList<>();
    private ILWriter parent;

    private DeclarationWriter(ILWriter parent) {
        this.parent = parent;
    }

    @Override
    public void write(ASTNode node) {
        decls.add((Declaration)node);
    }

    @Override
    public void writePrefix(ASTNode node) {
        decls.addFirst((Declaration)node);
    }

    @Override
    public void wrap(Function<ASTNode, ASTNode> wrapper) {
        parent.wrap(wrapper);
    }

    public static List<Declaration> generate(ILWriter parent, Consumer<DeclarationWriter> node) {
        DeclarationWriter writer = new DeclarationWriter(parent);
        node.accept(writer);
        return writer.decls;
    }
}
