package wyvern.target.corewyvernIL.decltype;

import wyvern.target.corewyvernIL.EmitOIR;
import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;

public class ValDeclType extends DeclType implements EmitOIR{

	private String field;
	private ValueType type;
	
	public ValDeclType(String field, ValueType type) {
		super();
		this.field = field;
		this.type = type;
	}

	public String getField ()
	{
		return field;
	}
	
	public void setField (String _field)
	{
		field = _field;
	}
	
	public void setType (ValueType _type)
	{
		type = _type;
	}
	
	public ValueType getType ()
	{
		return type;
	}

	@Override
	public <T> T acceptVisitor(ASTVisitor <T> emitILVisitor,
			Environment env, OIREnvironment oirenv) {
		return emitILVisitor.visit(env, oirenv, this);
	}

	@Override
	public boolean isSubtypeOf(DeclType dt, TypeContext ctx) {
		if (!(dt instanceof ValDeclType))
			return false;
		ValDeclType vdt = (ValDeclType) dt;
		return vdt.field.equals(field) && type.isSubtypeOf(vdt.type, ctx);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		ValDeclType other = (ValDeclType) obj;
		if (field == null) {
			if (other.field != null)
				return false;
		} else if (!field.equals(other.field))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Val[" + field + " : " + type + "]";
	}
}
