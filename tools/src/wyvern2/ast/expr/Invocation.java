package wyvern2.ast.expr;

/**
 * Represents the lookup host.id
 */
public class Invocation implements Expr {
	Expr host;
	String id;
}
