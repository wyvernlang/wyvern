package wyvern.tools.typedAST.core.expressions;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.CachingTypedAST;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class IfExpr extends CachingTypedAST implements CoreAST {
	public interface IfClause extends TypedAST {
		public boolean satisfied(Environment env);
		
		public TypedAST getClause();
		public TypedAST getBody();
	}

	private Iterable<IfClause> clauses;
	private FileLocation location;
	
	public IfExpr(Iterable<IfClause> clauses, FileLocation location) {
		this.clauses = clauses;
		this.location = location;
	}

	@Override
	public Value evaluate(Environment env) {
		for (IfClause clause : clauses) {
			if (clause.satisfied(env))
				return clause.evaluate(env);
		}
		return UnitVal.getInstance(location);
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
	}

	@Override
	public FileLocation getLocation() {
		return location;
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	protected Type doTypecheck(Environment env) {
		Type lastType = null;
		for (IfClause clause : clauses) {
			if (lastType == null) {
				lastType = clause.typecheck(env);
				continue;
			}
			if (clause.typecheck(env) != lastType) {
				ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, clause);
			}
		}
		if (lastType == null)
			ToolError.reportError(ErrorMessage.UNEXPECTED_EMPTY_BLOCK, this);
		return lastType;
	}
	
	public Iterable<IfClause> getClauses() {
		return clauses;
	}

}
