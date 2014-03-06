package wyvern2.ast.expr;

import wyvern2.ast.decl.Decl;

import java.util.List;

public class Let implements Expr {
	private Decl decl;
	private Expr inner;
}
