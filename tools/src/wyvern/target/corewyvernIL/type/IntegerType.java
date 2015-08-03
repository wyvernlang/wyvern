package wyvern.target.corewyvernIL.type;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.oir.OIREnvironment;

/**
 * Created by Ben Chung on 6/26/2015.
 */
public class IntegerType extends ValueType {

	@Override
	public boolean equals(Object o) {
		return o instanceof IntegerType;
	}

	@Override
	public int hashCode() {
		return IntegerType.class.hashCode();
	}

	@Override
	public String toString() {
		return "Int";
	}
	
	@Override
	public <T> T acceptVisitor(ASTVisitor<T> emitILVisitor, Environment env,
			OIREnvironment oirenv) {
		// TODO Auto-generated method stub
		return null;
	}
}
