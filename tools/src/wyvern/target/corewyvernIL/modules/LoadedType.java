package wyvern.target.corewyvernIL.modules;

public class LoadedType {
	private Module module;
	private String typeName;
	
	public LoadedType(String typeName, Module module) {
		this.module = module;
		this.typeName = typeName;
	}
	
	public Module getModule() {
		return module;
	}
	
	public String getTypeName() {
		return typeName;
	}
}
