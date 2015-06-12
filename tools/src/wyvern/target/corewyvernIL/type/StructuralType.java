package wyvern.target.corewyvernIL.type;

import java.util.List;

import wyvern.target.corewyvernIL.decltype.DeclType;

public class StructuralType extends ValueType {

	private String selfName;
	private List<DeclType> declTypes;
	
	public StructuralType(String selfName, List<DeclType> declTypes) {
		super();
		this.selfName = selfName;
		this.declTypes = declTypes;
	}

	public String getSelfName() {
		return selfName;
	}
	
	public void setSelfName(String selfName) {
		this.selfName = selfName;
	}
	
	public List<DeclType> getDeclTypes() {
		return declTypes;
	}
	
	public void setDeclTypes(List<DeclType> declTypes) {
		this.declTypes = declTypes;
	}
}
