package wyvern.tools.typedAST.extensions;

import java.util.Map;

import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.LineSequenceParser;
import wyvern.tools.typedAST.AbstractValue;
import wyvern.tools.typedAST.BoundCode;
import wyvern.tools.typedAST.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class ClassObject extends AbstractValue implements Value {
	private ClassDeclaration decl;
	
	public ClassObject(ClassDeclaration decl) {
		this.decl = decl;
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
}
