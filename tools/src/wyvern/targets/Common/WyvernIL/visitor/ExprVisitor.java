package wyvern.targets.Common.WyvernIL.visitor;

import wyvern.targets.Common.WyvernIL.Expr.BinOp;
import wyvern.targets.Common.WyvernIL.Expr.FnInv;
import wyvern.targets.Common.WyvernIL.Expr.Immediate;
import wyvern.targets.Common.WyvernIL.Expr.Inv;

public interface ExprVisitor<R> {
	R visit(Inv inv);
	R visit(BinOp binOp);
	R visit(FnInv fnInv);
	R visit(Immediate immediate);
}
