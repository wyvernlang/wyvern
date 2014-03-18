package wyvern.tools.bytecode.visitors;

import java.util.List;

import wyvern.targets.Common.WyvernIL.Def.ClassDef;
import wyvern.targets.Common.WyvernIL.Def.Def;
import wyvern.targets.Common.WyvernIL.Def.Def.Param;
import wyvern.targets.Common.WyvernIL.Def.Definition;
import wyvern.targets.Common.WyvernIL.Def.TypeDef;
import wyvern.targets.Common.WyvernIL.Def.ValDef;
import wyvern.targets.Common.WyvernIL.Def.VarDef;
import wyvern.targets.Common.WyvernIL.Stmt.Statement;
import wyvern.targets.Common.WyvernIL.visitor.DefVisitor;
import wyvern.tools.bytecode.core.BytecodeContext;
import wyvern.tools.bytecode.core.BytecodeContextImpl;
import wyvern.tools.bytecode.values.BytecodeClassDef;
import wyvern.tools.bytecode.values.BytecodeEmptyVal;
import wyvern.tools.bytecode.values.BytecodeFunction;
import wyvern.tools.bytecode.values.BytecodeRef;
import wyvern.tools.bytecode.values.BytecodeValue;

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
}
