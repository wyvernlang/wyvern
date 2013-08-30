package wyvern.tools.typedAST.extensions.interop.java.types;

import wyvern.tools.typedAST.core.declarations.ClassDeclaration;
import wyvern.tools.typedAST.extensions.interop.java.typedAST.JavaClassDecl;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.types.extensions.TypeType;

import java.util.concurrent.atomic.AtomicReference;

public class JavaClassType extends ClassType {
	private final Class clazz;
	private final JavaClassDecl decl;


	public JavaClassType(JavaClassDecl cd) {
		super(cd);
		this.clazz = cd.getClazz();
		decl = cd;
	}

	public void initalize() {
		((JavaClassDecl)super.getDecl()).initalize();
	}

	public Class getInnerClass() {
		return clazz;
	}

	@Override
	public ClassDeclaration getDecl() {
		return decl;
	}

	@Override
	public TypeType getEquivType() {
		return new TypeType(decl.getDeclEnv());
	}


}
