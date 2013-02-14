package wyvern.tools.typedAST.extensions;

import java.util.Map;

import wyvern.tools.typedAST.AbstractTypedAST;
import wyvern.tools.typedAST.AbstractValue;
import wyvern.tools.typedAST.BoundCode;
import wyvern.tools.typedAST.CoreAST;
import wyvern.tools.typedAST.CoreASTVisitor;
import wyvern.tools.typedAST.InvokableValue;
import wyvern.tools.typedAST.Invocation;
import wyvern.tools.typedAST.Value;
import wyvern.tools.typedAST.binding.ValueBinding;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Int;
import wyvern.tools.types.extensions.ObjectType;
import wyvern.tools.util.TreeWriter;

public class Obj extends AbstractValue implements InvokableValue {
	//private ObjectType type;
	private ClassObject cls;
	private Map<String, Value> fields;
	private Environment intEnv;
	
	public Obj(ClassObject cls, Map<String, Value> fields) {
		this.cls = cls;
		this.fields = fields;
		this.intEnv = cls.getObjEnv(this);
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
}
