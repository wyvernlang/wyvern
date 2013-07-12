package wyvern.tools.typedAST.core.binding;

import wyvern.tools.typedAST.core.declarations.ClassDeclaration;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class ClassBinding implements Binding {
	private final String name;
	private final ClassDeclaration cd;

	public ClassBinding(String name, ClassDeclaration cd) {
		this.name = name;
		this.cd = cd;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Type getType() {
		return cd.getType();
	}

	public ClassDeclaration getClassDecl() {
		return cd;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
	}
}
