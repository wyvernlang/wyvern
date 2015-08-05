package wyvern.target.corewyvernIL.decltype;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;


public abstract class ConcreteTypeMember extends DeclType {
	
	private ValueType sourceType;
	
	public ConcreteTypeMember(String name, ValueType sourceType) {
		super(name);
		this.sourceType = sourceType;
	}

	public void setSourceType (ValueType _type)
	{
		sourceType = _type;
	}
	
	public ValueType getSourceType ()
	{
		return sourceType;
	}

	@Override
	public <T> T acceptVisitor(ASTVisitor <T> emitILVisitor,
			Environment env, OIREnvironment oirenv) {
		// TODO Auto-generated method stub
		return null;
	}
}
