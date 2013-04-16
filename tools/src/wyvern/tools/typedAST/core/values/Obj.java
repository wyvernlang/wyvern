package wyvern.tools.typedAST.core.values;

import java.util.Map;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractTypedAST;
import wyvern.tools.typedAST.abs.AbstractValue;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Assignment;
import wyvern.tools.typedAST.core.Invocation;
import wyvern.tools.typedAST.core.binding.ValueBinding;
import wyvern.tools.typedAST.interfaces.Assignable;
import wyvern.tools.typedAST.interfaces.BoundCode;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.InvokableValue;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Int;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.util.TreeWriter;

public class Obj extends AbstractValue implements InvokableValue, Assignable {
	//private ObjectType type;
	private ClassObject cls;
	private Map<String, Value> fields;
	private Environment intEnv;
	
	public Obj(ClassObject cls, Map<String, Value> fields) {
		this.cls = cls;
		this.fields = fields;

		this.intEnv = cls.getObjEnv(this);
		for (Map.Entry<String, Value> elem : fields.entrySet())
			this.intEnv = 
				this.intEnv.extend(new ValueBinding(elem.getKey(), elem.getValue()));
		
	}

	@Override
	public Type getType() {
		return cls.getInstanceType();
	}
	
	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(cls);
		// TODO: add fields
	}

	@Override
	public Value evaluateInvocation(Invocation exp, Environment env) {
		String operation = exp.getOperationName();
		return cls.getValue(operation, this);
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