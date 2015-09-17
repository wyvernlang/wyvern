package wyvern.target.corewyvernIL.type;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.support.View;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;

public class TypeApplication extends ValueType {

	private ValueType baseType;
	private ValueType typeArgument;
	private String typeMember;
	
	
	public TypeApplication(ValueType baseType, ValueType typeArgument,
			String typeMember) {
		super();
		this.baseType = baseType;
		this.typeArgument = typeArgument;
		this.typeMember = typeMember;
	}

	public void setBaseType(ValueType baseType) {
		this.baseType = baseType;
	}

	public void setTypeArgument(ValueType typeArgument) {
		this.typeArgument = typeArgument;
	}

	public void setTypeMember(String typeMember) {
		this.typeMember = typeMember;
	}
	
	public ValueType getBaseType ()
	{
		return baseType;
	}
	
	public Type getTypeArgument ()
	{
		return typeArgument;
	}
	
	public String getTypeMember()
	{
		return typeMember;
	}

	@Override
	public <T> T acceptVisitor(ASTVisitor <T> emitILVisitor,
			Environment env, OIREnvironment oirenv) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ValueType adapt(View v) {
		return new TypeApplication(baseType.adapt(v), typeArgument.adapt(v), typeMember);
	}
}
