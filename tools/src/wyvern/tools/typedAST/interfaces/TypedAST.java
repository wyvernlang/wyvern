package wyvern.tools.typedAST.interfaces;

import java.util.Map;
import java.util.Optional;

import wyvern.target.corewyvernIL.support.TopLevelContext;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWritable;

public interface TypedAST extends TreeWritable, HasLocation {

	/** should call typecheck() before getType() -- except maybe for declarations */
    @Deprecated
	default Type getType() {
	    throw new RuntimeException();
	}
    
	public default void genTopLevel(TopLevelContext tlc) {
		throw new RuntimeException("genTopLevel not implemented for " + this.getClass());
	}

    public default StringBuilder prettyPrint() {
        throw new RuntimeException("prettyPrint not implemented for " + this.getClass());
    }
}
