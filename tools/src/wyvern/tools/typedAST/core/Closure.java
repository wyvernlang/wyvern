package wyvern.tools.typedAST.core;

import java.util.List;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractTypedAST;
import wyvern.tools.typedAST.abs.AbstractValue;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.ValueBinding;
import wyvern.tools.typedAST.core.values.TupleValue;
import wyvern.tools.typedAST.interfaces.ApplyableValue;
import wyvern.tools.typedAST.interfaces.BoundCode;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class Closure extends AbstractValue implements ApplyableValue {
	private BoundCode function;
	private Environment env;

	public Closure(BoundCode function, Environment env) {
		this.function = function;
		this.env = env;
	}

	@Override
	public Type getType() {
		return function.getType();
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(function, env);
	}

	@Override
	public Value evaluateApplication(Application app, Environment argEnv) {
		Value argValue = app.getArgument().evaluate(argEnv);
		Environment bodyEnv = env;
		List<NameBinding> bindings = function.getArgBindings();
		if (bindings.size() == 1)
			bodyEnv = bodyEnv.extend(new ValueBinding(bindings.get(0).getName(), argValue));
		else if (bindings.size() > 1 && argValue instanceof TupleValue)
			for (int i = 0; i < bindings.size(); i++)
				bodyEnv = bodyEnv.extend(new ValueBinding(bindings.get(i).getName(), ((TupleValue)argValue).getValue(i)));
		else if (bindings.size() != 0)
			throw new RuntimeException("Something bad happened!");
		
		return function.getBody().evaluate(bodyEnv);
	}

	private FileLocation location = FileLocation.UNKNOWN;
	public FileLocation getLocation() {
		return this.location;
	}
}
