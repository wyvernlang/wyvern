package wyvern.tools.typedAST.core;

import static wyvern.tools.errors.ErrorMessage.TYPE_CANNOT_BE_ASSIGNED;
import static wyvern.tools.errors.ErrorMessage.VALUE_CANNOT_BE_APPLIED;
import static wyvern.tools.errors.ToolError.reportError;
import static wyvern.tools.errors.ToolError.reportEvalError;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.parsing.BodyParser;
import wyvern.tools.parsing.LineSequenceParser;
import wyvern.tools.rawAST.LineSequence;
import wyvern.tools.typedAST.abs.CachingTypedAST;
import wyvern.tools.typedAST.core.expressions.LetExpr;
import wyvern.tools.typedAST.interfaces.Assignable;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.ApplyableType;
import wyvern.tools.types.AssignableType;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.TreeWriter;

public class Assignment extends CachingTypedAST implements CoreAST {
	private TypedAST target;
	private TypedAST value;
	
	private TypedAST nextExpr;

	public Assignment(TypedAST target, TypedAST value, FileLocation fileLocation) {
		this.target = target;
		this.value = value;
		this.location = fileLocation;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(target, value);
	}

	@Override
	protected Type doTypecheck(Environment env) {
		if (nextExpr == null) {
			target.typecheck(env);
		} else {
			nextExpr.typecheck(env);
		}
		return Unit.getInstance();
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
		return null;
	}

	private FileLocation location = FileLocation.UNKNOWN;
	public FileLocation getLocation() {
		return this.location;
	}
}
