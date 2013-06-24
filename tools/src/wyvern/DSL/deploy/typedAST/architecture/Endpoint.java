package wyvern.DSL.deploy.typedAST.architecture;

import wyvern.DSL.deploy.types.EndpointType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.parsing.extensions.TypeParser;
import wyvern.tools.typedAST.abs.CachingTypedAST;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.TypeBinding;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.FunDeclaration;
import wyvern.tools.typedAST.core.declarations.TypeDeclaration;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

import java.util.LinkedList;
import java.util.List;

public class Endpoint extends Declaration {
	private final String name;
	private final EndpointType type;
	private final List<Connection> connections = new LinkedList<>();
	private final TypeParser.MutableTypeDeclaration mtd;

	public Endpoint(String name) {
		this.type = new EndpointType(this);
		this.name = name;
		mtd = new TypeParser.MutableTypeDeclaration(name, FileLocation.UNKNOWN);
	}

	public TypeDeclaration getDecl() {
		return mtd;
	}

	public TypeDeclaration resolve() {
		LinkedList<FunDeclaration> mds = new LinkedList<>();

		for (Connection connection : connections) {
			mds.add(
					new FunDeclaration(
							connection.getName(),
							connection.getArgs(),
							connection.getReturnType(),
							null,
							false,
							FileLocation.UNKNOWN));
		}

		mtd.setDecls(new DeclSequence(mds));
		return mtd;
	}

	@Override
	protected Type doTypecheck(Environment env) {
		return type;
	}

	@Override
	protected Environment doExtend(Environment old) {
		return old.extend(new TypeBinding(name, type));
	}

	@Override
	public Environment extendWithValue(Environment old) {
		return old;
	}

	@Override
	public void evalDecl(Environment evalEnv, Environment declEnv) {
	}

	@Override
	public Type getType() {
		return type;
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

	public void addConnection(Connection connection) {
		connections.add(connection);
	}
}
