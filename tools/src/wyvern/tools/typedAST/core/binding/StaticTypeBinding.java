package wyvern.tools.typedAST.core.binding;

public class StaticTypeBinding extends AbstractBinding {

	private String typeName;
	
	public StaticTypeBinding(String name, String typeName) {
		super(name, null);
		
		this.typeName = typeName;
	}

	public String getTypeName() {
		return typeName;
	}

	@Override
	public String toString() {
		return "StaticTypeBinding [var=" + getName() + ", typeName=" + typeName + "]";
	}
	
	
}
