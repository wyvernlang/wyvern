package wyvern.tools.typedAST.extensions.interop.java.objects;

import wyvern.tools.typedAST.core.values.Obj;
import wyvern.tools.types.Environment;

public class JavaObj extends Obj {

	private final Object obj;

	public JavaObj(Environment inRef, Object innerJavaObject) {
		super(inRef);
		this.obj = innerJavaObject;
	}

	public Object getObj() {
		return obj;
	}

	@Override
	public String toString() {
		return "JavaObj("+obj.toString()+")";
	}
}
