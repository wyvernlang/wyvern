package wyvern.tools.typedAST.extensions.interop.java.types;

import wyvern.tools.typedAST.extensions.interop.java.typedAST.JavaClassDecl;
import wyvern.tools.types.extensions.ClassType;

public class JavaClassType extends ClassType {
	private final Class clazz;

	public JavaClassType(Class clazz) {
		super(new JavaClassDecl(clazz));
		this.clazz = clazz;
	}

	public void initalize() {
		((JavaClassDecl)super.getDecl()).initalize();
	}

	public Class getInnerClass() {
		return clazz;
	}
}
