package wyvern.tools.typedAST.extensions.interop.java.objects;

import wyvern.tools.typedAST.core.binding.LateValueBinding;
import wyvern.tools.typedAST.core.binding.ValueBinding;
import wyvern.tools.typedAST.core.declarations.ClassDeclaration;
import wyvern.tools.typedAST.core.values.ClassObject;
import wyvern.tools.typedAST.core.values.Obj;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class JavaObj extends Obj {

	private final Object obj;

	public JavaObj(Environment inRef, Object innerJavaObject) {
		super(inRef, new HashMap<String,Value>());
		this.obj = innerJavaObject;
	}

	public Object getObj() {
		return obj;
	}
}
