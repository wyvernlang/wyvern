package wyvern.tools.typedAST.core.values;

import java.util.Map;

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
import wyvern.tools.util.TreeWriter;

public class ClassObject extends AbstractValue implements InvokableValue, Value {
	private ClassDeclaration decl;
	private Environment classEnv;
	
	public ClassObject(ClassDeclaration decl) {
		this.decl = decl;
		this.classEnv = decl.getClassEnv();
	}

	public ClassDeclaration getClassDecl() {
		return decl;
	}

	@Override
	public Type getType() {
		return decl.getType();
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		// TODO Auto-generated method stub

	}

	public Type getInstanceType() {
		return decl.getType();
	}

	public Value getValue(String operation, Obj receiver) {
		return receiver.getIntEnv().getValue(operation);
	}

	public Environment getObjEnv(Obj obj) {
		return decl.evaluateDeclarations(obj);
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
}
