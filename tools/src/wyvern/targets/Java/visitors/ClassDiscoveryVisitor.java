package wyvern.targets.Java.visitors;

import wyvern.tools.typedAST.core.declarations.ClassDeclaration;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.visitors.BaseASTVisitor;

public class ClassDiscoveryVisitor extends BaseASTVisitor {
	private final ClassStore store;

	public ClassDiscoveryVisitor(ClassStore store) {
		this.store = store;
	}

	@Override
	public void visit(ClassDeclaration decl) {
		super.visit(decl);
		store.registerClass(decl);
	}
}
