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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class IfExpr extends CachingTypedAST implements CoreAST {
	public abstract static class IfClause extends CachingTypedAST implements TypedAST {
		public abstract boolean satisfied(Environment env);
		
		public abstract TypedAST getClause();
		public abstract TypedAST getBody();

		protected abstract TypedAST createInstance(TypedAST clause, TypedAST body);

		@Override
		public Map<String, TypedAST> getChildren() {
			Map<String, TypedAST> childMap = new HashMap<>();
			childMap.put("clause", getClause());
			childMap.put("body", getBody());
			return childMap;
		}

		@Override
		public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
			return createInstance(newChildren.get("clause"), newChildren.get("body"));
		}
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
	public Map<String, TypedAST> getChildren() {
		Map<String, TypedAST> childMap = new HashMap<>();
		int i = 0;
		for (IfClause clause : clauses)
			childMap.put(i++ + "", clause);
		return childMap;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		ArrayList<IfClause> clauses = new ArrayList<>(newChildren.size());
		int i = 0;
		for (String s : newChildren.keySet())
			clauses.add(Integer.parseInt(s), (IfClause)newChildren.get(s));
		return new IfExpr(clauses, location);
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
	protected Type doTypecheck(Environment env, Optional<Type> expected) {
		Type lastType = null;
		for (IfClause clause : clauses) {
			if (lastType == null) {
				lastType = clause.typecheck(env, expected);
				continue;
			}
			if (clause.typecheck(env, expected) != lastType) {
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
