package wyvern.tools.bytecode.values;

import wyvern.tools.bytecode.core.BytecodeContext;
import wyvern.tools.bytecode.core.BytecodeContextImpl;

public class BytecodeClassDef extends BytecodeClass {
	
	private BytecodeContext fullContext;

	public BytecodeClassDef(BytecodeContext context, BytecodeContext fullCtx, String name) {
		super(context);
		fullContext = fullCtx;
		coreContext.addToContext(name, this);
		fullContext.addToContext(name, this);
	}
	
	public BytecodeValue getCompleteClass() {
		return new BytecodeClass(new BytecodeContextImpl(fullContext));
	}

	@Override
	public String toString() {
		return "a class definition";
	}
}
