package wyvern.tools.typedAST.extensions.interop.java.types;

import wyvern.tools.typedAST.core.declarations.ClassDeclaration;
import wyvern.tools.typedAST.extensions.interop.java.typedAST.JavaClassDecl;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.types.extensions.TypeType;

public class JavaClassType extends ClassType {
	private final Class clazz;

	public JavaClassType(Class clazz) {
		super(new JavaClassDecl(clazz));
		this.clazz = clazz;
	}

	public JavaClassType(JavaClassDecl cd) {
		super(cd);
		this.clazz = cd.getClazz();
	}

	public void initalize() {
		((JavaClassDecl)super.getDecl()).initalize();
	}

	public Class getInnerClass() {
		return clazz;
	}
}
