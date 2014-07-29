package wyvern.targets.Common.wyvernIL.interpreter.visitors;

import wyvern.targets.Common.wyvernIL.IL.Def.*;
import wyvern.targets.Common.wyvernIL.IL.Def.Def.Param;
import wyvern.targets.Common.wyvernIL.IL.Stmt.Statement;
import wyvern.targets.Common.wyvernIL.IL.visitor.DefVisitor;
import wyvern.targets.Common.wyvernIL.interpreter.core.BytecodeContext;
import wyvern.targets.Common.wyvernIL.interpreter.core.BytecodeContextImpl;
import wyvern.targets.Common.wyvernIL.interpreter.values.*;

import java.util.List;

/**
 * a DefVisitor for the IL interpreter
 * @author Tal Man
 *
 */
public class BytecodeDefVisitor implements DefVisitor<BytecodeContext> {

	private final BytecodeContext context;
	private final BytecodeContext evalContext; // context to evaluate against
											   // will also be changed
		
	/**
	 * sets up the visitor with a context to work with
	 * @param changeContext
	 * 		the context of the program to be altered at this point
	 * @param evaluateContext
	 * 		the context of the program to be evaluated against (this should
	 * 		be a throw away copy because it might be changed)
	 */
	public BytecodeDefVisitor(BytecodeContext changeContext, BytecodeContext evaluateContext) {
		context = changeContext;
		evalContext = evaluateContext;
	}
	
	/**
	 * sets up the visitor with a context to work with
	 * @param visContext
	 * 		the context of the program at this point
	 */
	public BytecodeDefVisitor(BytecodeContext visContext) {
		this(visContext,visContext);
	}
	
	@Override
	public BytecodeContext visit(VarDef varDef) {
		String name = varDef.getName();
		BytecodeExnVisitor visitor = new BytecodeExnVisitor(evalContext);
		BytecodeValue value = new BytecodeEmptyVal();
		if(varDef.getExn() != null) {
			value = varDef.getExn().accept(visitor);
		}
		BytecodeValue refValue = new BytecodeRef(value);
		context.addToContext(name, refValue);
		evalContext.addToContext(name, refValue);
		return context;
	}

	@Override
	public BytecodeContext visit(ValDef valDef) {
		String name = valDef.getName();
		BytecodeExnVisitor visitor = new BytecodeExnVisitor(evalContext);
		BytecodeValue value = null;
		if(valDef.getExn() != null) {
			value = valDef.getExn().accept(visitor);
		}
		context.addToContext(name, value);
		evalContext.addToContext(name, value);
		return context;
	}

	@Override
	public BytecodeContext visit(TypeDef typeDef) {
		/*
		 *  does nothing because currently TypeDef has no role in the IL itself
		 */
		return context;
	}

	@Override
	public BytecodeContext visit(Def def) {
		List<Statement> body = def.getBody();
		String name = def.getName();
		List<Param> params = def.getParams();
		BytecodeValue val = new BytecodeFunction(params, body, evalContext, name);
		context.addToContext(name, val);
		evalContext.addToContext(name, val);
		return context;
	}

	@Override
	public BytecodeContext visit(ClassDef classDef) {
		BytecodeContext newContext = new BytecodeContextImpl(evalContext);
		List<Definition> classDefs = classDef.getClassDefinitions();
		List<Definition> defs = classDef.getDefinitions();
		for(Definition def : classDefs) {
			newContext = def.accept(new BytecodeDefVisitor(newContext));
		}
		String name = classDef.getName();
		BytecodeValue val = new BytecodeClassDef(newContext,defs,name);
		context.addToContext(name, val);
		evalContext.addToContext(name, val);
		return context;
	}

	@Override
	public BytecodeContext visit(ImportDef importDef) {
		//TODO
		return context;
	}
}
