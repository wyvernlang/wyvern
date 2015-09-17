package wyvern.target.corewyvernIL.decltype;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIREnvironment;

public abstract class DeclTypeWithResult extends DeclType {
	private ValueType rawType;
	
	DeclTypeWithResult(String name, ValueType rawType) {
		super(name);
		this.rawType = rawType;
	}

	public ValueType getResultType(View v) {
		return rawType.adapt(v);
	}

	public ValueType getRawResultType() {
		return rawType;
	}

}
