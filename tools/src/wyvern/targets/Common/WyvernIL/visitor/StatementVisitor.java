package wyvern.targets.Common.WyvernIL.visitor;

import wyvern.targets.Common.WyvernIL.Stmt.*;

public interface StatementVisitor<T> {
	T visit(Assign assign);
	T visit(Defn defn);
	T visit(Goto aGoto);
	T visit(Label label);
	T visit(Pure pure);
	T visit(Return aReturn);
}
