package wyvern.target.corewyvernIL.decltype;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;


public class ConcreteTypeMember extends DeclTypeWithResult {
	
	public ConcreteTypeMember(String name, ValueType sourceType) {
		super(name, sourceType);
	}

	/*public void setSourceType (ValueType _type)
	{
		sourceType = _type;
	}*/
	
	public ValueType getSourceType ()
	{
		return getRawResultType();
	}

	@Override
	public <T> T acceptVisitor(ASTVisitor <T> emitILVisitor,
			Environment env, OIREnvironment oirenv) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSubtypeOf(DeclType dt, TypeContext ctx) {
		if (!(dt instanceof ConcreteTypeMember)) {
			return false;
		}
		ConcreteTypeMember ctm = (ConcreteTypeMember) dt;
		return ctm.getName().equals(getName()) && this.getRawResultType().isSubtypeOf(ctm.getRawResultType(), ctx);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
		result = prime * result + ((getRawResultType() == null) ? 0 : getRawResultType().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConcreteTypeMember other = (ConcreteTypeMember) obj;
		if (getName() == null) {
			if (other.getName() != null)
				return false;
		} else if (!getName().equals(other.getName()))
			return false;
		if (getRawResultType() == null) {
			if (other.getRawResultType() != null)
				return false;
		} else if (!getRawResultType().equals(other.getRawResultType()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TypeMember[" + getName() + " = " + getRawResultType() + "]";
	}

	@Override
	public DeclType adapt(View v) {
		return new ConcreteTypeMember(getName(), this.getRawResultType().adapt(v));
	}
}
