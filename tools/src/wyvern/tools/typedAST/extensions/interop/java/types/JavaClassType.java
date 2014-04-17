package wyvern.tools.typedAST.extensions.interop.java.types;

import wyvern.tools.typedAST.core.expressions.Invocation;
import wyvern.tools.typedAST.core.declarations.ClassDeclaration;
import wyvern.tools.typedAST.extensions.interop.java.typedAST.JavaClassDecl;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.util.Reference;

import java.util.LinkedList;

public class JavaClassType extends ClassType {
	private final Class clazz;
	private final JavaClassDecl decl;

	@Override
	public String getName() {
		return clazz.getSimpleName();
	}


	public JavaClassType(JavaClassDecl cd) {
		super(new Reference<Environment>() {
			@Override
			public Environment get() {
				return cd.getObjEnv();
			}
		}, null, new LinkedList<>(), "");
		this.clazz = cd.getClazz();
		decl = cd;
	}

	public void initalize() {
		((JavaClassDecl)super.getDecl()).initalize();
	}

	public Class getInnerClass() {
		decl.initalize();
		return decl.getClazz();
	}

	@Override
	public Type checkOperator(Invocation opExp, Environment env) {
		initalize();
		return super.checkOperator(opExp,env);
	}

	@Override
	public ClassDeclaration getDecl() {
		return decl;
	}

	@Override
	public boolean subtype(Type other) {
		decl.initalize();
		if (other instanceof JavaClassType
				&& ((JavaClassType)other).decl.getClazz().equals(decl.getClazz()))
			return true;
		return super.subtype(other);
	}


}
