package wyvern.targets.Common.wyvernIL.interpreter.values;

import wyvern.targets.Common.wyvernIL.IL.Def.Definition;
import wyvern.targets.Common.wyvernIL.interpreter.core.BytecodeContext;
import wyvern.targets.Common.wyvernIL.interpreter.core.BytecodeContextImpl;
import wyvern.targets.Common.wyvernIL.interpreter.visitors.BytecodeDefVisitor;

import java.util.ArrayList;
import java.util.List;

public class BytecodeClassDef extends BytecodeClass {
	
	private List<Definition> defs;
	
	/**
	 * creates a new class definition
	 * @param context
	 * 		the context with which to instantiate the class definition
	 * @param defns
	 * 		definitions to be used for the full class instance
	 * @param name
	 * 		the name to be used for this class in the context
	 */
	public BytecodeClassDef(BytecodeContext context, List<Definition> defns, String name) {
		super(context);
		defs = defns;
		coreContext.addToContext(name, this);
	}
	
	/**
	 * createa a class instance of the class that is defined in this value
	 * and returns it
	 * @return
	 * 		a bytecodeClass representing the full class instance
	 */
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
