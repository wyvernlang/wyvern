package wyvern.DSL.deploy.typedAST.architecture;

import wyvern.DSL.deploy.types.ConnectionType;
import wyvern.DSL.deploy.types.DomainType;
import wyvern.DSL.deploy.types.EndpointType;
import wyvern.DSL.deploy.types.ViaType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.CachingTypedAST;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.TypeBinding;
import wyvern.tools.typedAST.core.declarations.DefDeclaration;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.util.TreeWriter;

import java.util.List;

public class Connection extends Declaration {
	private final String name;
	private final List<NameBinding> args;
	private final Type returnType;
	private TypedAST modifiers;
	private ConnectionType connectionType;

	public Connection(String name, List<NameBinding> args, Type returnType, TypedAST modifiers) {
		this.name = name;
		this.args = args;
		this.returnType = returnType;
		this.modifiers = modifiers;
		connectionType = new ConnectionType(name, DefDeclaration.getMethodType(args, returnType));
	}

	public void setModifiers(TypedAST newModifiers) {
		this.modifiers = newModifiers;
	}

	@Override
	protected Type doTypecheck(Environment env) {
		TypeBinding domainBinding = env.lookupType("domain");
		TypeBinding viaBinding = env.lookupType("via");
		TypeBinding requiresBinding = env.lookupType("requires");

		if (domainBinding == null || viaBinding == null)
			throw new RuntimeException();

		(((DomainType) domainBinding.getType()).getDomain().getFinalEndpoint())
				.getEndpoint().addConnection(this);


		connectionType = new ConnectionType(name, connectionType.getArrow(), (DomainType) domainBinding.getType(), viaBinding.getType());
		return connectionType;
	}

	public void setProperties(DomainType domain, ViaType viaType) {
		connectionType = new ConnectionType(name, connectionType.getArrow(), domain, viaType);
	}

	@Override
	protected Environment doExtend(Environment old) {
		return old.extend(new TypeBinding(name, connectionType));
	}

	@Override
	public Environment extendWithValue(Environment old) {
		return old;
	}

	@Override
	public void evalDecl(Environment evalEnv, Environment declEnv) {
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public FileLocation getLocation() {
		return FileLocation.UNKNOWN;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Type getType() {
		return connectionType;
	}

	public List<NameBinding> getArgs() {
		return args;
	}

	public Type getReturnType() {
		return returnType;
	}
}
