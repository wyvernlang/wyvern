package wyvern.tools.typedAST.extensions.interop.java.objects;

import wyvern.tools.typedAST.core.declarations.ClassDeclaration;
import wyvern.tools.typedAST.core.values.ClassObject;
import wyvern.tools.typedAST.core.values.Obj;
import wyvern.tools.typedAST.interfaces.Value;

import java.util.HashMap;

public class JavaObj extends Obj {

	private final Object obj;

	public JavaObj(ClassObject inObj, Object innerJavaObject) {
		super(inObj, new HashMap<String,Value>());
		this.obj = innerJavaObject;
	}

	public Object getObj() {
		return obj;
	}
}
