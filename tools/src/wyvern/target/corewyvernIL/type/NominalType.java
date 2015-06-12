package wyvern.target.corewyvernIL.type;

import wyvern.target.corewyvernIL.expression.Path;

public class NominalType extends ValueType{
	
	private Path path;
	private String typeMember;
	
	
	public NominalType(Path path, String typeMember) {
		super();
		this.path = path;
		this.typeMember = typeMember;
	}

	public Path getPath() {
		return path;
	}
	
	public void setPath(Path path) {
		this.path = path;
	}
	
	public String getTypeMember() {
		return typeMember;
	}
	
	public void setTypeMember(String typeMember) {
		this.typeMember = typeMember;
	}
}
