package wyvern.tools.typedAST.core.values;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractValue;
import wyvern.tools.typedAST.core.Assignment;
import wyvern.tools.typedAST.core.Invocation;
import wyvern.tools.typedAST.interfaces.Assignable;
import wyvern.tools.typedAST.interfaces.InvokableValue;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.types.extensions.TypeDeclUtils;
import wyvern.tools.util.TreeWriter;

import java.util.concurrent.atomic.AtomicReference;

public class Obj extends AbstractValue implements InvokableValue, Assignable {
	protected AtomicReference<Environment> intEnv;
	private Environment typeEquivEnv;
	
	public Obj(Environment declEnv) {
		this.intEnv = new AtomicReference<>(declEnv);
	}

    public Obj(AtomicReference<Environment> declEnv) {
        intEnv = declEnv;
    }

    private void updateTee() {
        typeEquivEnv = TypeDeclUtils.getTypeEquivalentEnvironment(intEnv.get());
    }

	@Override
	public Type getType() {
        updateTee();
		return new ClassType(intEnv, new AtomicReference<>(typeEquivEnv));
	}
	
	@Override
	public void writeArgsToTree(TreeWriter writer) {
	}

	@Override
	public Value evaluateInvocation(Invocation exp, Environment env) {
		String operation = exp.getOperationName();
		return getIntEnv().getValue(operation);
	}
	
	public Environment getIntEnv() {
		return intEnv.get();
	}

	@Override
	public Value evaluateAssignment(Assignment ass, Environment env) {
		if (!(ass.getTarget() instanceof Invocation))
			throw new RuntimeException("Something really, really weird happened.");
		String operation = ((Invocation) ass.getTarget()).getOperationName();
		
		Value value = intEnv.get().getValue(operation);
		if (!(value instanceof VarValue)) {
			throw new RuntimeException("Trying to assign a non-var");
		}
		VarValue varValue = (VarValue)value;
		
		Value newValue = ass.getValue().evaluate(env);
		varValue.setValue(newValue);
		return newValue;
	}

	private FileLocation location = FileLocation.UNKNOWN;
	public FileLocation getLocation() {
		return this.location;
	}
}