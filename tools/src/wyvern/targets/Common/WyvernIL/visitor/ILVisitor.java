package wyvern.targets.Common.WyvernIL.visitor;

import wyvern.targets.Common.WyvernIL.Def.Definition;
import wyvern.targets.Common.WyvernIL.Expr.Expression;
import wyvern.targets.Common.WyvernIL.Imm.Operand;
import wyvern.targets.Common.WyvernIL.Stmt.Statement;

public interface ILVisitor {
	void visit(Statement statement);
	void visit(Operand operand);
	void visit(Expression expression);
	void visit(Definition definition);
}
