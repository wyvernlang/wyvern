package wyvern.DSL.deploy.typedAST.architecture.properties;

import wyvern.DSL.deploy.types.RequiresType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class RequiresProperty extends ConnectionProperty {
	private TypedAST predicate;
	private RequiresType type;

	public RequiresProperty(TypedAST predicate, TypedAST body) {
		super(body, "requires");
		this.predicate = predicate;
		this.type = new RequiresType(this);
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public FileLocation getLocation() {
		return FileLocation.UNKNOWN;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
	}

	public void setVals(TypedAST predicate, TypedAST body) {
		this.predicate = predicate;
		setBody(body);
	}
}
