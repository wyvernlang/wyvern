package wyvern.targets.Common.wyvernIL.IL.visitor;

import wyvern.targets.Common.wyvernIL.IL.Stmt.*;

public interface StatementVisitor<T> {
	T visit(Assign assign);
	T visit(Defn defn);
	T visit(Goto aGoto);
	T visit(Label label);
	T visit(Pure pure);
	T visit(Return aReturn);
	T visit(IfStmt ifStmt);
}
