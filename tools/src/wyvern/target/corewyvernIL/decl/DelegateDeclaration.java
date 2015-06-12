package wyvern.target.corewyvernIL.decl;

import java.util.List;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.type.Type;

public class DelegateDeclaration extends Declaration{

	private List<DefDeclType> methods;
	private String fieldName;
	
	public DelegateDeclaration(List<DefDeclType> methods, String fieldName) {
		super();
		this.methods = methods;
		this.fieldName = fieldName;
	}

	public List<DefDeclType> getMethods() {
		return methods;
	}
	
	public void setMethods(List<DefDeclType> methods) {
		this.methods = methods;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	@Override
	public Type typeCheck(Environment env) {
		// TODO Auto-generated method stub
		return null;
	}
}
