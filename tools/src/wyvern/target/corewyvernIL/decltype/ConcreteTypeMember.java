package wyvern.target.corewyvernIL.decltype;

import java.io.IOException;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIREnvironment;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;


public class ConcreteTypeMember extends DeclTypeWithResult {
	
	public ConcreteTypeMember(String name, ValueType sourceType) {
		this(name, sourceType, null);
	}
	public ConcreteTypeMember(String name, ValueType sourceType, IExpr metadata) {
		super(name, sourceType);
		this.metadata = metadata;
	}
	
	private IExpr metadata;

	/*public void setSourceType (ValueType _type)
	{
		sourceType = _type;
	}*/
	
	public ValueType getSourceType ()
	{
		return getRawResultType();
	}
	
	@Override
	public void checkWellFormed(TypeContext ctx) {
		/*if (metadata != null) {
			ValueType t = metadata.typeCheck(ctx);
			t.checkWellFormed(ctx);
		}*/
		super.checkWellFormed(ctx);
	}
	@Override
	public Value getMetadataValue() {
		if (!(metadata instanceof Value)) {
			ToolError.reportError(ErrorMessage.CANNOT_USE_METADATA_IN_SAME_FILE, this);
		}
		return (Value) metadata;
	}

	@Override
	public <T> T acceptVisitor(ASTVisitor <T> emitILVisitor,
			Environment env, OIREnvironment oirenv) {
    return emitILVisitor.visit(env, oirenv, this);
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
	public void doPrettyPrint(Appendable dest, String indent) throws IOException {
		dest.append(indent).append("type ").append(getName()).append(" = ");
		getSourceType().doPrettyPrint(dest, indent);
		dest.append('\n');
	}

	@Override
	public DeclType adapt(View v) {
		return new ConcreteTypeMember(getName(), this.getRawResultType().adapt(v), metadata);
	}
	
	@Override
	public DeclType interpret(EvalContext ctx) {
		if (metadata == null)
			return this;
		return new ConcreteTypeMember(getName(), this.getRawResultType(), metadata.interpret(ctx));
	}
	@Override
	public DeclType doAvoid(String varName, TypeContext ctx, int count) {
		ValueType t = this.getRawResultType().doAvoid(varName, ctx, count);
		if (t.equals(this.getRawResultType())) {
			return this;
		} else {
			return new ConcreteTypeMember(this.getName(),t, metadata);
		}
	}
}
