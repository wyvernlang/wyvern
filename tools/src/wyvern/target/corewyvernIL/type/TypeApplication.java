package wyvern.target.corewyvernIL.type;

import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;

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
		throw new RuntimeException("use RefinementType");
		// TODO: just remove this class?
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
	public <S, T> T acceptVisitor(ASTVisitor <S, T> emitILVisitor,
			S state) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ValueType adapt(View v) {
		return new TypeApplication(baseType.adapt(v), typeArgument.adapt(v), typeMember);
	}

	@Override
	public void checkWellFormed(TypeContext ctx) {
		baseType.checkWellFormed(ctx);
		typeArgument.checkWellFormed(ctx);
		// also check if the typeMember is part of the baseType 
		throw new RuntimeException("not implemented");
	}

	@Override
	public ValueType doAvoid(String varName, TypeContext ctx, int count) {
		throw new RuntimeException("not implemented");
	}
}
