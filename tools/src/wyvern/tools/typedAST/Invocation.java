package wyvern.tools.typedAST;

import static wyvern.tools.errors.ErrorMessage.CANNOT_INVOKE;
import static wyvern.tools.errors.ErrorMessage.OPERATOR_DOES_NOT_APPLY;
import static wyvern.tools.errors.ToolError.reportError;
import static wyvern.tools.errors.ToolError.reportEvalError;
import wyvern.tools.types.Environment;
import wyvern.tools.types.OperatableType;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class Invocation extends CachingTypedAST {
	private String operationName;
	private TypedAST receiver;
	private TypedAST argument;

	public Invocation(TypedAST op1, String operatorName, TypedAST op2) {
		this.receiver = op1;
		this.argument = op2;
		this.operationName = operatorName;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(receiver, operationName, argument);
	}

	@Override
	protected Type doTypecheck() {
		Type receiverType = receiver.typecheck();
		
		if (!(receiverType instanceof OperatableType))
			reportError(OPERATOR_DOES_NOT_APPLY, operationName, receiverType.toString(), this);
		return ((OperatableType)receiverType).checkOperator(this);
	}

	public TypedAST getArgument() {
		return argument;
	}
	public TypedAST getReceiver() {
		return receiver;
	}
	public String getOperationName() {
		return operationName;
	}
	
	@Override
	public Value evaluate(Environment env) {
		Value lhs = receiver.evaluate(env);
		if (!(lhs instanceof InvokableValue))
			reportEvalError(CANNOT_INVOKE, lhs.toString(), this);
		InvokableValue receiverValue = (InvokableValue) lhs;
		return receiverValue.evaluateInvocation(this, env);
	}

}
