package wyvern.DSL.deploy.typedAST.architecture.properties;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class DomainProperty extends ConnectionProperty {
	private final TypedAST predicate;

	public DomainProperty(TypedAST predicate, TypedAST body) {
		super(body);
		this.predicate = predicate;
	}

	@Override
	protected Type doTypecheck(Environment env) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Value evaluate(Environment env) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
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
