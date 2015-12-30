package wyvern.target.corewyvernIL.type;

import java.io.IOException;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
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
	public void doPrettyPrint(Appendable dest, String indent) throws IOException {
		dest.append("Int");
	}
	
	@Override
	public <T> T acceptVisitor(ASTVisitor<T> emitILVisitor, Environment env,
			OIREnvironment oirenv) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ValueType adapt(View v) {
		return this;
	}
}
