package wyvern.tools.bytecode.values;

import java.util.ArrayList;
import java.util.List;

import wyvern.targets.Common.WyvernIL.Def.Definition;
import wyvern.tools.bytecode.core.BytecodeContext;
import wyvern.tools.bytecode.core.BytecodeContextImpl;
import wyvern.tools.bytecode.visitors.BytecodeDefVisitor;

public class BytecodeClassDef extends BytecodeClass {
	
	private List<Definition> defs;
	
	public BytecodeClassDef(BytecodeContext context, List<Definition> defns, String name) {
		super(context);
		defs = defns;
		coreContext.addToContext(name, this);
	}
	
	public BytecodeValue getCompleteClass() {	
		BytecodeContext context = new BytecodeContextImpl(coreContext);
		BytecodeFunction init = (BytecodeFunction) context.getValue("$init");
		init.run(new ArrayList<BytecodeValue>(), context);
		for(Definition def : defs) {
			context = def.accept(new BytecodeDefVisitor(context));
		}
		return new BytecodeClass(context);
	}

	@Override
	public String toString() {
		return "a class definition";
	}
}
