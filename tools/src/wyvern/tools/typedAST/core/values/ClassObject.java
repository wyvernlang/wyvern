package wyvern.tools.typedAST.core.values;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.LineSequenceParser;
import wyvern.tools.typedAST.abs.AbstractValue;
import wyvern.tools.typedAST.core.Invocation;
import wyvern.tools.typedAST.core.declarations.ClassDeclaration;
import wyvern.tools.typedAST.interfaces.BoundCode;
import wyvern.tools.typedAST.interfaces.InvokableValue;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.util.TreeWriter;

public class ClassObject extends AbstractValue implements InvokableValue, Value {
	private Environment classEnv;
	
	public ClassObject(Environment classEnv) {
		this.classEnv = classEnv;
	}

	@Override
	public Type getType() {
		return new ClassType(new AtomicReference<>(classEnv), null, null);
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		// TODO Auto-generated method stub

	}

	public Value getValue(String operation, Obj receiver) {
		return receiver.getIntEnv().getValue(operation);
	}

	private FileLocation location = FileLocation.UNKNOWN;
	public FileLocation getLocation() {
		return this.location;
	}

	@Override
	public Value evaluateInvocation(Invocation exp, Environment env) {
		String operation = exp.getOperationName();
		return classEnv.getValue(operation);
	}

	public Environment getIntEnv() {
		return classEnv;
	}
}
