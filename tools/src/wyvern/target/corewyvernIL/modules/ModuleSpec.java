package wyvern.target.corewyvernIL.modules;

public class ModuleSpec {
	private static final String INTERNAL_MODULE_PREFIX = "MOD$";
	
	private final String qualifiedName;
	
	public ModuleSpec(String qualifiedName) {
		this.qualifiedName = qualifiedName;
	}
	
	public String getQualifiedName() {
		return qualifiedName;
	}
	
	public String getInternalName() {
		return INTERNAL_MODULE_PREFIX + qualifiedName;
	}
	
	@Override
	public String toString() {
		return "ModuleSpec("+qualifiedName+")";
	}
}
