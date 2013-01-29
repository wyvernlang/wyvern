package wyvern.targets.JavaScript.types;

import static wyvern.tools.errors.ErrorMessage.OPERATOR_DOES_NOT_APPLY;
import static wyvern.tools.errors.ToolError.reportError;
import wyvern.tools.typedAST.Application;
import wyvern.tools.typedAST.Declaration;
import wyvern.tools.typedAST.Invocation;
import wyvern.tools.typedAST.extensions.ClassDeclaration;
import wyvern.tools.types.AbstractTypeImpl;
import wyvern.tools.types.ApplyableType;
import wyvern.tools.types.Environment;
import wyvern.tools.types.OperatableType;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Bool;
import wyvern.tools.types.extensions.Int;
import wyvern.tools.types.extensions.Str;
import wyvern.tools.util.TreeWriter;

public class JSObjectType extends AbstractTypeImpl implements OperatableType, ApplyableType {
	public static JSObjectType getInstance() {
		if (instance == null)
			instance = new JSObjectType();
		return instance;
	}
	
	private static JSObjectType instance = null;
	private JSObjectType() {
	}
	
	@Override
	public void writeArgsToTree(TreeWriter writer) {
		// nothing to write		
	}
	
	@Override
	public String toString() {
		return "JSObjectType";
	}

	@Override
	public Type checkOperator(Invocation opExp, Environment env) {
		if (opExp.getOperationName().equals("asInt"))
			return Int.getInstance();
		else if (opExp.getOperationName().equals("asBool"))
			return Bool.getInstance();
		else if (opExp.getOperationName().equals("asString"))
			return Str.getInstance();
		return this;
	}

	@Override
	public Type checkApplication(Application application, Environment env) {
		return this;
	}
}
