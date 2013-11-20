package wyvern.targets.Common.WyvernIL.visitor;

import wyvern.targets.Common.WyvernIL.Expr.*;

public interface ExprVisitor<R> {
	R visit(Inv inv);
	R visit(BinOp binOp);
	R visit(FnInv fnInv);
	R visit(Immediate immediate);
	R visit(New aNew);
}
