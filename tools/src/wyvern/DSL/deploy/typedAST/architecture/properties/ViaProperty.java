package wyvern.DSL.deploy.typedAST.architecture.properties;

import wyvern.DSL.deploy.types.ViaType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class ViaProperty extends ConnectionProperty {
	private Type connectionType;

	public ViaProperty(Type connectionType, TypedAST body) {
		super(body, "via");
		this.connectionType = new ViaType(this);
	}

	@Override
	public Type getType() {
		return connectionType;
	}

	@Override
	public FileLocation getLocation() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public void setVars(Type connType, TypedAST body) {
		connectionType = connType;
		setBody(body);
	}
}
