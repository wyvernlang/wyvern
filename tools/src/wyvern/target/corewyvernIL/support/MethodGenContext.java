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
	GenContext genContext;
	
	public MethodGenContext(String methodName, String objectName, GenContext genContext) {
		this.objectName = objectName;
		this.methodName = methodName;
		this.genContext = genContext;
	}

	@Override
	public String toString() {
		return "GenContext[" + endToString();
	}
	
	@Override
	public String endToString() {
		return methodName + " = " + objectName + '.' + methodName +  ", " + genContext.endToString();
	}

	@Override
	public ValueType lookup(String varName) {
		return genContext.lookup(varName);
	}

	@Override
	public String getContainerForTypeAbbrev(String typeName) {
		return genContext.getContainerForTypeAbbrev(typeName);
	}

	@Override
	public CallableExprGenerator getCallableExprRec(String varName, GenContext origCtx) {
		if (this.methodName.equals(varName))
			return new InvocationExprGenerator(new Variable(objectName), varName, origCtx);
		else
			return genContext.getCallableExprRec(varName, origCtx);
	}

}
