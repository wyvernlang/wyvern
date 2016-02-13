package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.type.ValueType;

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
