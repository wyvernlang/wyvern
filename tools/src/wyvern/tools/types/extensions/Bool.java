package wyvern.tools.types.extensions;

import static wyvern.tools.errors.ErrorMessage.OPERATOR_DOES_NOT_APPLY;
import static wyvern.tools.errors.ErrorMessage.OPERATOR_DOES_NOT_APPLY2;
import static wyvern.tools.errors.ToolError.reportError;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import wyvern.tools.typedAST.Invocation;
import wyvern.tools.types.AbstractTypeImpl;
import wyvern.tools.types.Environment;
import wyvern.tools.types.OperatableType;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class Bool extends AbstractTypeImpl implements OperatableType {
	private Bool() { }
	private static Bool instance = new Bool();
	public static Bool getInstance() { return instance; }

	private static final Set<String> legalOperators = new HashSet<String>(Arrays.asList(new String[] {
			"&&",
			"||",
	}));

	@Override
	public Type checkOperator(Invocation opExp, Environment env) {
		Type type2 = opExp.getArgument().typecheck(env);
		String operatorName = opExp.getOperationName();
		
		if (!(legalOperators.contains(operatorName)))
			reportError(OPERATOR_DOES_NOT_APPLY, operatorName, this.toString(), opExp);
		
		if (!(type2 instanceof Bool))
			reportError(OPERATOR_DOES_NOT_APPLY2, operatorName, this.toString(), type2.toString(), opExp);
		
		return this;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		// TODO Auto-generated method stub
		// POTANIN: At this stage as this was based on Int class, I assume there is nothing to write.
	}

	@Override
	public String toString() {
		return "Bool";
	}

}
