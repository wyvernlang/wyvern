package wyvern.tools.typedAST;

import static wyvern.tools.errors.ErrorMessage.CANNOT_INVOKE;
import static wyvern.tools.errors.ErrorMessage.OPERATOR_DOES_NOT_APPLY;
import static wyvern.tools.errors.ToolError.reportError;
import static wyvern.tools.errors.ToolError.reportEvalError;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.typedAST.extensions.values.UnitVal;
import wyvern.tools.typedAST.extensions.values.VarValue;
import wyvern.tools.types.Environment;
import wyvern.tools.types.OperatableType;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class Invocation extends CachingTypedAST implements CoreAST, Assignable {
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
	protected Type doTypecheck(Environment env) {
		Type receiverType = receiver.typecheck(env);
		 
		if (receiverType instanceof OperatableType) {
			return ((OperatableType)receiverType).checkOperator(this,env);
		} else if (receiverType instanceof Unit) {
			// FIXME: UnitType is temporary hack till proper type handling is done!
			return receiverType;
		} else {
			reportError(OPERATOR_DOES_NOT_APPLY, operationName, receiverType.toString(), this);
			return null;
		}
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
		Value out = receiverValue.evaluateInvocation(this, env);
		
		//TODO: bit of a hack
		if (out instanceof VarValue)
			out = ((VarValue)out).getValue();
		return out;
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Value evaluateAssignment(Assignment ass, Environment env) {
		Value lhs = receiver.evaluate(env);
		if (!(lhs instanceof Assignable))
			reportEvalError(CANNOT_INVOKE, lhs.toString(), this);
		
		return ((Assignable)lhs).evaluateAssignment(ass, env);
	}

	private int line = -1;
	public int getLine() {
		return this.line; // TODO: NOT IMPLEMENTED YET.
	}
}
