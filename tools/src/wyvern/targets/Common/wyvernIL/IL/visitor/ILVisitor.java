package wyvern.targets.Common.wyvernIL.IL.visitor;

import wyvern.targets.Common.wyvernIL.IL.Def.Definition;
import wyvern.targets.Common.wyvernIL.IL.Expr.Expression;
import wyvern.targets.Common.wyvernIL.IL.Imm.Operand;
import wyvern.targets.Common.wyvernIL.IL.Stmt.Statement;

public interface ILVisitor {
	void visit(Statement statement);
	void visit(Operand operand);
	void visit(Expression expression);
	void visit(Definition definition);
}
