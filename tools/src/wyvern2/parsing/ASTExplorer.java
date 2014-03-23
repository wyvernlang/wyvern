package wyvern2.parsing;


import wyvern.tools.typedAST.core.expressions.New;
import wyvern.tools.typedAST.extensions.DSLLit;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.transformers.TypedAST.AbstractASTVisitor;

public class ASTExplorer extends AbstractASTVisitor {
	int discovered = 0;
	TypedAST ref;

	public boolean foundNew() {
		return (discovered & 1) != 0;
	}
	public boolean foundTilde() {
		return (discovered & 2) != 0;
	}
	public TypedAST getRef() {
		return ref;
	}

	@Override
	public TypedAST transform(TypedAST input) {
		if (input instanceof New) {
			if (discovered != 0)
				throw new RuntimeException("Discovered second forward reference");
			ref = input;
			discovered |= 1;
		}
		if (input instanceof DSLLit) {
			if (discovered != 0)
				throw new RuntimeException("Discovered second forward reference");
			ref = input;
			discovered |= 2;
		}
		return this.defaultTransformation(input);
	}
}
