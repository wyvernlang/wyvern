package wyvern.tools.rawAST;

import wyvern.tools.errors.HasLocation;

public interface RawAST extends HasLocation {
	<A,R> R accept(RawASTVisitor<A,R> visitor, A arg);
}
