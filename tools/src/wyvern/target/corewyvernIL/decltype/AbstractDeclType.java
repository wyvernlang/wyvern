package wyvern.target.corewyvernIL.decltype;


public class AbstractDeclType extends DeclType {

	private String typeName;
	
	public AbstractDeclType(String typeName) {
		super();
		this.typeName = typeName;
	}

	public String getTypeName ()
	{
		return typeName;
	}
	
	public void setTypeName (String _typeName)
	{
		typeName = _typeName;
	}
}
