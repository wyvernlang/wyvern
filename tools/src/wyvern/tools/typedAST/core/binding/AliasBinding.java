package wyvern.tools.typedAST.core.binding;

import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class AliasBinding implements Binding {

	
	private String alias;
	private Type reference;

	public AliasBinding(String alias, Type reference){
		this.alias = alias;
		this.reference = reference;		
	}
	
	@Override
	public void writeArgsToTree(TreeWriter writer) {
		// TODO Auto-generated method stub

	}

	
	
	@Override
	public String getName() {
		return null;
	}

	public String getAlias() {
		return alias;
	}
	
	@Override
	public Type getType() {
		// TODO Auto-generated method stub
		return reference;
	}

}
