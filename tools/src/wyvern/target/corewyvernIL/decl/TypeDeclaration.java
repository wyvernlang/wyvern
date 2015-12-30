package wyvern.target.corewyvernIL.decl;

import java.io.IOException;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.decltype.ConcreteTypeMember;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.ValDeclType;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.Type;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;

public class TypeDeclaration extends NamedDeclaration {

	public TypeDeclaration(String typeName, Type sourceType) {
		super(typeName);
		this.sourceType = sourceType;
	}

	private Type sourceType;
	
	public Type getSourceType() {
		return sourceType;
	}

	@Override
	public <T> T acceptVisitor(ASTVisitor <T> emitILVisitor,
			Environment env, OIREnvironment oirenv) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DeclType typeCheck(TypeContext ctx, TypeContext thisCtx) {
		DeclType declt = new ConcreteTypeMember(super.getName(), (ValueType) this.sourceType); 
		return declt;
	}
	
	@Override
	public void doPrettyPrint(Appendable dest, String indent) throws IOException {
		dest.append(indent).append("type ").append(getName()).append(" = ");
		sourceType.doPrettyPrint(dest, indent);
		dest.append('\n');
	}

	/*@Override
	public String toString() {
		return "type declaration " + super.getName() + " = " + sourceType.toString();
	}*/
}
