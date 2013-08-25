package wyvern.tools.typedAST.core.values;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractValue;
import wyvern.tools.typedAST.core.Assignment;
import wyvern.tools.typedAST.core.Invocation;
import wyvern.tools.typedAST.core.binding.ValueBinding;
import wyvern.tools.typedAST.interfaces.Assignable;
import wyvern.tools.typedAST.interfaces.InvokableValue;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.types.extensions.TypeType;
import wyvern.tools.util.TreeWriter;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class Obj extends AbstractValue implements InvokableValue, Assignable {
	//private ObjectType type;
	private ClassObject cls;
	private Map<String, Value> fields;
	protected Environment intEnv;
	
	public Obj(Environment declEnv, Map<String, Value> fields) {
		this.fields = fields;

		this.intEnv = declEnv;
		for (Map.Entry<String, Value> elem : fields.entrySet()) {
            if (intEnv.getValue(elem.getKey()) != null &&
                    intEnv.getValue(elem.getKey()) instanceof VarValue) {
                ((VarValue)this.intEnv.getValue(elem.getKey())).setValue(elem.getValue());
                continue;
            }

			this.intEnv = 
				this.intEnv.extend(new ValueBinding(elem.getKey(), elem.getValue()));
        }

	}

	@Override
	public Type getType() {
		return new ClassType(new AtomicReference<>(intEnv), null, null);
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
		return intEnv;
	}

	@Override
	public Value evaluateAssignment(Assignment ass, Environment env) {
		if (!(ass.getTarget() instanceof Invocation))
			throw new RuntimeException("Something really, really weird happened.");
		String operation = ((Invocation) ass.getTarget()).getOperationName();
		
		Value value = intEnv.getValue(operation);
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