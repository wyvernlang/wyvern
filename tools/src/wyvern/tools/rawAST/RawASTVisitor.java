package wyvern.tools.rawAST;

public interface RawASTVisitor<A,R> {
	R visit(Int node, A arg);
	R visit(StringNode node, A arg);
	R visit(Symbol node, A arg);
	R visit(Unit node, A arg);
	R visit(LineSequence node, A arg);
	R visit(Line node, A arg);
	R visit(Parenthesis node, A arg);
}
