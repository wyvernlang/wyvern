package wyvern.target.corewyvernIL.support;

import java.util.LinkedList;
import java.util.List;

import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.DefDeclaration;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.typedAST.interfaces.TypedAST;

public class MethodGenContext extends GenContext {

	String objectName;
	String methodName;
	
	public MethodGenContext(String methodName, String objectName, GenContext genContext) {
		super(genContext);
		this.objectName = objectName;
		this.methodName = methodName;
	}

	@Override
	public String toString() {
		return "GenContext[" + endToString();
	}
	
	@Override
	public String endToString() {
		return methodName + " = " + objectName + '.' + methodName +  ", " + getNext().endToString();
	}

	@Override
	public ValueType lookup(String varName) {
		return getNext().lookup(varName);
	}

	@Override
	public String getContainerForTypeAbbrev(String typeName) {
		return getNext().getContainerForTypeAbbrev(typeName);
	}

	@Override
	public CallableExprGenerator getCallableExprRec(String varName, GenContext origCtx) {
		if (this.methodName.equals(varName))
			return new InvocationExprGenerator(new Variable(objectName), varName, origCtx);
		else
			return getNext().getCallableExprRec(varName, origCtx);
	}

}
