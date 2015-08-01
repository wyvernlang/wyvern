package wyvern.target.corewyvernIL.decl;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.Type;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;

public class TypeDeclaration extends Declaration {

	public TypeDeclaration(String typeName, Type sourceType) {
		super();
		this.typeName = typeName;
		this.sourceType = sourceType;
	}

	private String typeName;
	private Type sourceType;
	
	public String getTypeName() {
		return typeName;
	}
	
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
	public Type getSourceType() {
		return sourceType;
	}
	
	public void setSourceType(Type sourceType) {
		this.sourceType = sourceType;
	}

	@Override
	public <T> T acceptVisitor(ASTVisitor <T> emitILVisitor,
			Environment env, OIREnvironment oirenv) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DeclType typeCheck(TypeContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}
}
