package wyvern.DSL.deploy.typedAST.architecture;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.CachingTypedAST;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

import java.util.List;

public class Connection extends CachingTypedAST {
	private final String name;
	private final List<NameBinding> args;
	private final Type returnType;
	private final TypedAST modifiers;

	public Connection(String name, List<NameBinding> args, Type returnType, TypedAST modifiers) {

		this.name = name;
		this.args = args;
		this.returnType = returnType;
		this.modifiers = modifiers;
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
