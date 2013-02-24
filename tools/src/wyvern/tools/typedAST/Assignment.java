package wyvern.tools.typedAST;

import static wyvern.tools.errors.ErrorMessage.TYPE_CANNOT_BE_ASSIGNED;
import static wyvern.tools.errors.ErrorMessage.VALUE_CANNOT_BE_APPLIED;
import static wyvern.tools.errors.ToolError.reportError;
import static wyvern.tools.errors.ToolError.reportEvalError;
import wyvern.tools.parsing.CoreParser;
import wyvern.tools.parsing.LineSequenceParser;
import wyvern.tools.rawAST.LineSequence;
import wyvern.tools.typedAST.extensions.LetExpr;
import wyvern.tools.types.ApplyableType;
import wyvern.tools.types.AssignableType;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class Assignment extends CachingTypedAST implements CoreAST {
	private TypedAST target;
	private TypedAST value;
	
	private TypedAST nextExpr;

	public Assignment(TypedAST target, TypedAST value) {
		this.target = target;
		this.value = value;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(target, value);
	}

	@Override
	protected Type doTypecheck(Environment env) {
		Type fnType = target.typecheck(env);
		if (!(fnType instanceof AssignableType))
			reportError(TYPE_CANNOT_BE_ASSIGNED, fnType.toString(), this);
		Type assignmentType = ((AssignableType)fnType).checkAssignment(this, env);
		
		if (nextExpr == null)
			return assignmentType;
		else
			return nextExpr.typecheck(env);
	}

	public TypedAST getTarget() {
		return target;
	}

	public TypedAST getValue() {
		return value;
	}

	@Override
	public Value evaluate(Environment env) {
		TypedAST lhs = target.evaluate(env);
		if (!(lhs instanceof AssignableValue))
			reportEvalError(VALUE_CANNOT_BE_APPLIED, lhs.toString(), this);
		AssignableValue fnValue = (AssignableValue) lhs;
		Value evaluated = ((AssignableValue) lhs).evaluateAssignment(this, env);
		if (nextExpr == null)
			return evaluated;
		else
			return nextExpr.evaluate(env);
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public LineSequenceParser getLineSequenceParser() {
		return new LineSequenceParser() {
			@Override
			public TypedAST parse(TypedAST first, LineSequence rest,
					Environment env) {
				TypedAST body = rest.accept(CoreParser.getInstance(), env);
				Assignment.this.nextExpr = body;
				return Assignment.this;
			}
		};
	}
}
