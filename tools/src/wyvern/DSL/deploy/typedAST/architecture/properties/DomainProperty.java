package wyvern.DSL.deploy.typedAST.architecture.properties;

import wyvern.DSL.deploy.types.DomainType;
import wyvern.DSL.deploy.types.EndpointType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.core.binding.TypeBinding;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class DomainProperty extends ConnectionProperty {

	private EndpointType from;
	private EndpointType to;
	private DomainType type;

	public DomainProperty(EndpointType from, EndpointType to, TypedAST body) {
		super(body, "domain");
		this.from = from;
		this.to = to;
		this.type = new DomainType(this);
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
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public void setVals(EndpointType argument, EndpointType result, TypedAST body) {
		from = argument;
		to = result;
		setBody(body);
	}

	public EndpointType getFinalEndpoint() {
		return to;
	}
}
