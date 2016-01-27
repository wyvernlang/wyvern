package wyvern.target.corewyvernIL.decl;

public abstract class NamedDeclaration extends Declaration {
	private String name;
	
	public NamedDeclaration(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

}
