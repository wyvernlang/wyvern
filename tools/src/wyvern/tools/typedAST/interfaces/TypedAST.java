package wyvern.tools.typedAST.interfaces;

import wyvern.target.corewyvernIL.support.TopLevelContext;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.util.TreeWritable;

public interface TypedAST extends TreeWritable, HasLocation, TypedASTNode {

    default void genTopLevel(TopLevelContext tlc) {
        throw new RuntimeException("genTopLevel not implemented for " + this.getClass());
    }

    default StringBuilder prettyPrint() {
        throw new RuntimeException("prettyPrint not implemented for " + this.getClass());
    }
}
