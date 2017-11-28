package wyvern.tools.typedAST.interfaces;

import java.util.Map;
import java.util.Optional;

import wyvern.target.corewyvernIL.support.TopLevelContext;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.TreeWritable;

public interface TypedAST extends TreeWritable, HasLocation {

	/** should call typecheck() before getType() -- except maybe for declarations */
	default Type getType() {
	    throw new RuntimeException();
	}
	Type typecheck(Environment env, Optional<Type> expected);
	
	/** an interpreter.  Out of date - should generate IL code and interpret that instead. */
	@Deprecated
	default Value evaluate(EvaluationEnvironment env) {
	    throw new RuntimeException("deprecated");
	}

	/**
	 * Gets the children of a composite node
	 * @return The children of the node
	 */
	default Map<String, TypedAST> getChildren() { return null; }
	/**
	 * Clones the current AST node with the given set of children
	 * @param newChildren The children to create
	 * @return The deep-copied AST node
	 */
	default TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) { return null; }

	public default void genTopLevel(TopLevelContext tlc) {
		throw new RuntimeException("genTopLevel not implemented for " + this.getClass());
	}

    public default StringBuilder prettyPrint() {
        throw new RuntimeException("prettyPrint not implemented for " + this.getClass());
    }
}
