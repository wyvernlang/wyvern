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
		if (nextExpr == null)
			return target.typecheck(env);
		else
			return nextExpr.typecheck(env);
	}

	public TypedAST getTarget() {
		return target;
	}

	public TypedAST getValue() {
		return value;
	}
	
	public TypedAST getNext() { 
		return nextExpr;
	}

	@Override
	public Value evaluate(Environment env) {
		if (!(target instanceof Assignable))
			reportEvalError(VALUE_CANNOT_BE_APPLIED, target.toString(), this);
		Value evaluated = ((Assignable) target).evaluateAssignment(this, env);
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

	private int line = -1;
	public int getLine() {
		return this.line; // TODO: NOT IMPLEMENTED YET.
	}
}
