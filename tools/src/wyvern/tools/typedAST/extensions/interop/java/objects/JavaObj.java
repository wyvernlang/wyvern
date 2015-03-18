package wyvern.tools.typedAST.extensions.interop.java.objects;

import wyvern.tools.typedAST.core.values.Obj;
import wyvern.tools.typedAST.extensions.interop.java.typedAST.JavaClassDecl;
import wyvern.tools.typedAST.extensions.interop.java.types.JavaClassType;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;

public class JavaObj extends Obj {

	private final Object obj;
	private JavaClassDecl jcd;

	public JavaObj(Environment inRef, Object innerJavaObject, JavaClassDecl jcd) {
		super(inRef, null);
		this.obj = innerJavaObject;
		this.jcd = jcd;
	}

	public Object getObj() {
		return obj;
	}

	@Override
	public Type getType() {
		super.getType();
		return new JavaClassType(jcd);
	}

	@Override
	public String toString() {
		return "JavaObj("+obj.toString()+")";
	}
}
