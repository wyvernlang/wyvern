package wyvern.target.corewyvernIL.decl;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.type.Type;

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
	public Type typeCheck(Environment env) {
		// TODO Auto-generated method stub
		return null;
	}
}
