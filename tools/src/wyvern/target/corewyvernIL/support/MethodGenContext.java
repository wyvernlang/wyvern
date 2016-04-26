package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;

public class MethodGenContext extends GenContext {

	String objectName;
	String methodName;
	FileLocation loc;
	
	public MethodGenContext(String methodName, String objectName, GenContext genContext, FileLocation loc) {
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
	public ValueType lookupType(String varName) {
		return getNext().lookupType(varName);
	}

	@Override
	public String getContainerForTypeAbbrev(String typeName) {
		return getNext().getContainerForTypeAbbrev(typeName);
	}

	@Override
	public CallableExprGenerator getCallableExprRec(String varName, GenContext origCtx) {
		if (this.methodName.equals(varName))
			return new InvocationExprGenerator(new Variable(objectName), varName, origCtx, loc);
		else
			return getNext().getCallableExprRec(varName, origCtx);
	}

}
