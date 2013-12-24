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
import wyvern.tools.bytecode.values.BytecodeClass;
import wyvern.tools.bytecode.values.BytecodeFunction;
import wyvern.tools.bytecode.values.BytecodeRef;
import wyvern.tools.bytecode.values.BytecodeValue;

public class BytecodeDefVisitor implements DefVisitor<BytecodeContext> {

	private final BytecodeContext context;
	
	public BytecodeDefVisitor(BytecodeContext c) {
		context = c;
	}
	
	@Override
	public BytecodeContext visit(VarDef varDef) {
		String name = varDef.getName();
		BytecodeExnVisitor visitor = new BytecodeExnVisitor(context);
		BytecodeValue value = varDef.getExn().accept(visitor);
		BytecodeValue refValue = new BytecodeRef(value);
		return new BytecodeContextImpl(refValue, name, context);
	}

	@Override
	public BytecodeContext visit(ValDef valDef) {
		String name = valDef.getName();
		BytecodeExnVisitor visitor = new BytecodeExnVisitor(context);
		BytecodeValue value = valDef.getExn().accept(visitor);
		return new BytecodeContextImpl(value, name, context);
	}

	@Override
	public BytecodeContext visit(TypeDef typeDef) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BytecodeContext visit(Def def) {
		List<Statement> body = def.getBody();
		String name = def.getName();
		List<Param> params = def.getParams();
		BytecodeValue val = new BytecodeFunction(params, body, context, name);
		return new BytecodeContextImpl(val,name,context);
	}

	@Override
	public BytecodeContext visit(ClassDef classDef) {
		BytecodeContext newContext = context.clone();
		List<Definition> defs = classDef.getDefinitions();
		for(Definition def : defs) {
			newContext = def.accept(new BytecodeDefVisitor(newContext));
		}
		BytecodeValue val =  new BytecodeClass(newContext);
		return new BytecodeContextImpl(val,classDef.getName(),context);
	}

}
