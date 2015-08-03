package wyvern.tools.typedAST.core.expressions;

import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.CachingTypedAST;
import wyvern.tools.typedAST.core.binding.typechecking.AssignableNameBinding;
import wyvern.tools.typedAST.core.values.VarValue;
import wyvern.tools.typedAST.interfaces.*;
import wyvern.tools.typedAST.transformers.ExpressionWriter;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Environment;
import wyvern.tools.types.OperatableType;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.TreeWriter;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

import static wyvern.tools.errors.ErrorMessage.CANNOT_INVOKE;
import static wyvern.tools.errors.ToolError.reportEvalError;

public class Invocation extends CachingTypedAST implements CoreAST, Assignable {
	private String operationName;
	private TypedAST receiver;
	private TypedAST argument;

	public Invocation(TypedAST op1, String operatorName, TypedAST op2, FileLocation fileLocation) {
		this.receiver = op1;
		this.argument = op2;
		this.operationName = operatorName;
		this.location = fileLocation;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(receiver, operationName, argument);
	}

	@Override
	protected Type doTypecheck(Environment env, Optional<Type> expected) {
		// System.out.println("env = " + env);
		// System.out.println("receiver = "  + receiver + " : "+ receiver.getClass());

		Type receiverType = receiver.typecheck(env, Optional.empty());

		// System.out.println("receiverType = " + receiverType);

		if (argument != null)
			argument.typecheck(env, Optional.empty());

		if (receiverType instanceof OperatableType) {
			return ((OperatableType)receiverType).checkOperator(this,env);
		} else {
			ToolError.reportError(ErrorMessage.OPERATOR_DOES_NOT_APPLY, this, "Trying to call a function on non OperatableType!", receiverType.toString());
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
	public Value evaluate(EvaluationEnvironment env) {
		/*
		if (this.receiver instanceof Variable) {
			Variable w = (Variable) this.receiver;
			if (w.getName().equals("w")) {
				System.out.println("Examining w more closely.");
				System.out.println(((MetadataWrapper) w.getType()).getInner());
				System.out.println(env.lookup(w.getName()).getType());
			}
		}
		*/

		Value lhs = receiver.evaluate(env);
		if (Globals.checkRuntimeTypes && !(lhs instanceof InvokableValue))
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
	public void checkAssignment(Assignment ass, Environment env) {
		Type recType = receiver.typecheck(env, Optional.empty());
		if (!(recType instanceof ClassType)) //TODO: Hack
			throw new RuntimeException("Cannot assign to a field on a type without fields!");
		((ClassType) recType).getEnv().lookupBinding(operationName, AssignableNameBinding.class).get();
	}

	@Override
	public Value evaluateAssignment(Assignment ass, EvaluationEnvironment env) {
		Value lhs = receiver.evaluate(env);
		if (!(lhs instanceof Assignable))
			reportEvalError(CANNOT_INVOKE, lhs.toString(), this);

		return ((Assignable)lhs).evaluateAssignment(ass, env);
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		Hashtable<String, TypedAST> children = new Hashtable<>();
		if (receiver != null)
			children.put("receiver", receiver);
		if (argument != null)
			children.put("argument", argument);
		return children;
	}

	@Override
	public void codegenToIL(GenerationEnvironment environment, ILWriter writer) {
		LinkedList<Expression> arguments = new LinkedList<>();
        if (argument == null) {

        } else if (!(argument instanceof TupleObject)) {
			arguments.add(ExpressionWriter.generate(iwriter -> argument.codegenToIL(environment, iwriter)));
		} else {
			for (TypedAST arg : ((TupleObject)(argument)).getObjects()) {
				arguments.add(ExpressionWriter.generate(iwriter->arg.codegenToIL(environment, iwriter)));
			}
		}
		writer.write(new MethodCall(ExpressionWriter.generate(iwriter->receiver.codegenToIL(environment, iwriter)), operationName, arguments));
	}

	@Override
	public TypedAST doClone(Map<String, TypedAST> nc) {
		return new Invocation(nc.get("receiver"), operationName, nc.get("argument"), location);
	}

	private FileLocation location = FileLocation.UNKNOWN;
	public FileLocation getLocation() {
		return this.location;
	}

	@Override
	public Expression generateIL(GenContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}
}