package wyvern.tools.bytecode.values;

import wyvern.tools.bytecode.core.BytecodeContext;
import wyvern.tools.bytecode.core.BytecodeContextImpl;

public class BytecodeClassDef extends BytecodeClass {
	
	private BytecodeContext fullContext;

	public BytecodeClassDef(BytecodeContext context, BytecodeContext fullCtx) {
		super(context);
		fullContext = fullCtx;
	}
	
	public BytecodeValue getCompleteClass() {
		return new BytecodeClass(new BytecodeContextImpl(fullContext));
	}
}
