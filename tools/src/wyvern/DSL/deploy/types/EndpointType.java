package wyvern.DSL.deploy.types;

import wyvern.DSL.deploy.typedAST.architecture.Endpoint;
import wyvern.tools.types.SubtypeRelation;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

import java.util.HashSet;

public class EndpointType implements Type {
	private Endpoint endpoint;

	public EndpointType(Endpoint endpoint) {

		this.endpoint = endpoint;
	}

	@Override
	public boolean subtype(Type other, HashSet<SubtypeRelation> subtypes) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean subtype(Type other) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public Endpoint getEndpoint() {
		return endpoint;
	}
	@Override
	public boolean isSimple() {
		// TODO Auto-generated method stub
		return true;
	}
}
