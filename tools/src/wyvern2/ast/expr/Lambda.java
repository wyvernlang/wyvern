package wyvern2.ast.expr;

import wyvern2.ast.type.Type;

/**
 * Equivalent to fn paramName : paramType => inner
 */
public class Lambda implements Expr {
	String paramName;
	Type paramType;
	Expr inner;

}
