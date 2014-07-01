package wyvern.tools.typedAST.core.values;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractValue;
import wyvern.tools.typedAST.core.binding.AssignableValueBinding;
import wyvern.tools.typedAST.core.binding.evaluation.ValueBinding;
import wyvern.tools.typedAST.core.binding.evaluation.VarValueBinding;
import wyvern.tools.typedAST.core.binding.typechecking.AssignableNameBinding;
import wyvern.tools.typedAST.core.expressions.Assignment;
import wyvern.tools.typedAST.core.expressions.Invocation;
import wyvern.tools.typedAST.interfaces.Assignable;
import wyvern.tools.typedAST.interfaces.InvokableValue;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.types.extensions.TypeDeclUtils;
import wyvern.tools.util.Reference;
import wyvern.tools.util.TreeWriter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Obj extends AbstractValue implements InvokableValue, Assignable {
	protected Reference<Environment> intEnv;
	private Environment typeEquivEnv;
	
	public Obj(Environment declEnv) {
		this.intEnv = new Reference<>(declEnv);
	}

    public Obj(Reference<Environment> declEnv) {
		intEnv = declEnv;
    }

    private void updateTee() {
        typeEquivEnv = TypeDeclUtils.getTypeEquivalentEnvironment(intEnv.get());
    }

	@Override
	public Type getType() {
        updateTee();
		return new ClassType(intEnv, new Reference<>(typeEquivEnv), new LinkedList<String>(), null);
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		return new HashMap<>();
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		return this;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
	}

	@Override
	public Value evaluateInvocation(Invocation exp, Environment env) {
		String operation = exp.getOperationName();
		return getIntEnv().getValue(operation, env.extend(new ValueBinding("this", this)));
	}
	
	public Environment getIntEnv() {
		return intEnv.get();
	}

	@Override
	public void checkAssignment(Assignment ass, Environment env) {
		if (!(ass.getTarget() instanceof Invocation))
			throw new RuntimeException("Something really, really weird happened.");
		String operation = ((Invocation) ass.getTarget()).getOperationName();
		intEnv.get().lookupBinding(operation, AssignableNameBinding.class).orElseThrow(() -> new RuntimeException("Cannot set a non-existent or immutable var"));

		return;
	}

	@Override
	public Value evaluateAssignment(Assignment ass, Environment env) {
		if (!(ass.getTarget() instanceof Invocation))
			throw new RuntimeException("Something really, really weird happened.");
		String operation = ((Invocation) ass.getTarget()).getOperationName();

		Value newValue = ass.getValue().evaluate(env);

		intEnv.get().lookupBinding(operation, AssignableValueBinding.class)
				.orElseThrow(() -> new RuntimeException("Trying to assign a non-var"))
				.assign(newValue);

		return newValue;
	}

	private FileLocation location = FileLocation.UNKNOWN;
	public FileLocation getLocation() {
		return this.location;
	}
}