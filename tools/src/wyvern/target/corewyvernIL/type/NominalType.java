package wyvern.target.corewyvernIL.type;

import java.io.IOException;
import java.util.Arrays;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.decltype.ConcreteTypeMember;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.target.oir.OIREnvironment;

public class NominalType extends ValueType {
	private Path path;
	private String typeMember;

	public NominalType(String pathVariable, String typeMember) {
		super();
		this.path = new Variable(pathVariable);
		this.typeMember = typeMember;
	}

	public NominalType(Path path, String typeMember) {
		super();
        if(path.equals(null)) {
            throw new IllegalStateException("Path cannot be null.");
        }
		this.path = path;
		this.typeMember = typeMember;
	}

	public Path getPath() {
		return path;
	}

	public String getTypeMember() {
		return typeMember;
	}
	
	@Override
	public boolean isResource(TypeContext ctx) {
		return this.getStructuralType(ctx).isResource(ctx);
	}

	@Override
	public StructuralType getStructuralType(TypeContext ctx, StructuralType theDefault) {
		DeclType dt = path.typeCheck(ctx).getStructuralType(ctx).findDecl(typeMember, ctx);
		if (dt instanceof ConcreteTypeMember) {
			ValueType vt = ((ConcreteTypeMember)dt).getResultType(View.from(path, ctx));
			return vt.getStructuralType(ctx, theDefault);
		} else {
			return super.getStructuralType(ctx, theDefault);
		}
	}
	
	@Override
	public ValueType getCanonicalType(TypeContext ctx) {
		DeclType dt = path.typeCheck(ctx).getStructuralType(ctx).findDecl(typeMember, ctx);
		if (dt instanceof ConcreteTypeMember) {
			return ((ConcreteTypeMember)dt).getResultType(View.from(path, ctx)).getCanonicalType(ctx);
		} else {
			return this;
		}
	}

	@Override
	public void doPrettyPrint(Appendable dest, String indent) throws IOException {
		path.doPrettyPrint(dest, indent);
		dest.append('.').append(typeMember);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(new Object[] { path, typeMember, });
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof NominalType))
			return false;
		NominalType other = (NominalType)obj;
		return path.equals(other.path) && typeMember.equals(other.typeMember);
	}
	
	public boolean isSubtypeOf(ValueType t, TypeContext ctx) {
		if (super.isSubtypeOf(t, ctx))
			return true;
		DeclType dt = path.typeCheck(ctx).getStructuralType(ctx).findDecl(typeMember, ctx);
		if (dt instanceof ConcreteTypeMember) {
			ValueType vt = ((ConcreteTypeMember)dt).getResultType(View.from(path, ctx));
			return vt.isSubtypeOf(t, ctx);
		} else {
			ValueType ct = t.getCanonicalType(ctx);
			return super.isSubtypeOf(ct, ctx); // check for equality with the canonical type 
		}
	}

	@Override
	public <T> T acceptVisitor(ASTVisitor <T> emitILVisitor,
			Environment env, OIREnvironment oirenv) {
		return emitILVisitor.visit(env, oirenv, this);
	}

	@Override
	public ValueType adapt(View v) {
		return new NominalType(path.adapt(v), typeMember);
	}
	
}
