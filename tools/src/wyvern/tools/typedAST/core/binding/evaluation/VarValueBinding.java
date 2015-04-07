package wyvern.tools.typedAST.core.binding.evaluation;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.core.binding.AbstractBinding;
import wyvern.tools.typedAST.core.binding.AssignableValueBinding;
import wyvern.tools.typedAST.core.expressions.Variable;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.EvaluationEnvironment;

import java.util.Optional;

public class VarValueBinding extends AssignableValueBinding {
	private Value value;

	public VarValueBinding(String name, Type type, Value value) {
		super(name, value);
		this.value = value;
	}

	@Override
	public TypedAST getUse() {
		return new Variable(this, FileLocation.UNKNOWN);
	}

	@Override
	public void assign(Value value) {
		this.value = value;
	}

	@Override
	public Value getValue(EvaluationEnvironment env) {
		return value;
	}
}
