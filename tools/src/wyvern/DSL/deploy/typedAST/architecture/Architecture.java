package wyvern.DSL.deploy.typedAST.architecture;

import wyvern.DSL.deploy.typedAST.architecture.properties.ConnectionProperty;
import wyvern.DSL.deploy.types.ArchitectureType;
import wyvern.DSL.deploy.types.ConnectionType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.LineSequenceParser;
import wyvern.tools.typedAST.abs.CachingTypedAST;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.TypeBinding;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

import java.util.List;

public class Architecture extends Declaration {
	private final String name;
	private ArchitectureType type;
	private TypedAST body;

	public Architecture(String name, TypedAST body) {

		this.name = name;
		this.body = body;
		type = new ArchitectureType(this);
	}


	public void setBody(TypedAST body) {
		this.body = body;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	protected Type doTypecheck(Environment env) {
		if (body != null)
			body.typecheck(env);
		List<ConnectionType> connections = ConnectionProperty.getConnections(body);
		return type;
	}

	@Override
	protected Environment doExtend(Environment old) {
		if (getType() == null)
			throw new RuntimeException("Must typecheck before extension");
		return old.extend(new TypeBinding(name, getType()));
	}

	@Override
	public Environment extendWithValue(Environment old) {
		return old;//No value
	}

	@Override
	public void evalDecl(Environment evalEnv, Environment declEnv) {
		//Do nothing, it's not a runtime object
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public FileLocation getLocation() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		//To change body of implemented methods use File | Settings | File Templates.
	}
}
