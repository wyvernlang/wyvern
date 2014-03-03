package wyvern2.ast.expr;

/**
 * Represents the function application fn(arg)
 */
public class Application implements Expr {
	private Expr fn, arg;
}
