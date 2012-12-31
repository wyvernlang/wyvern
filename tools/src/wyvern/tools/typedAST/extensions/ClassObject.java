package wyvern.tools.typedAST.extensions;

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
	private Environment memberEnv;
	
	public ClassObject(ClassDeclaration decl, Environment memberEnv) {
		this.decl = decl;
		this.memberEnv = memberEnv;
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

	public Closure getClosure(String operation) {
		return (Closure) memberEnv.getValue(operation);
	}

}
