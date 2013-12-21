package wyvern.tools.bytecode.visitors;

import wyvern.targets.Common.WyvernIL.Def.ClassDef;
import wyvern.targets.Common.WyvernIL.Def.Def;
import wyvern.targets.Common.WyvernIL.Def.TypeDef;
import wyvern.targets.Common.WyvernIL.Def.ValDef;
import wyvern.targets.Common.WyvernIL.Def.VarDef;
import wyvern.targets.Common.WyvernIL.visitor.DefVisitor;
import wyvern.targets.Common.WyvernIL.visitor.ExprVisitor;
import wyvern.tools.bytecode.core.BytecodeContext;
import wyvern.tools.bytecode.core.BytecodeContextImpl;
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
		return new BytecodeContextImpl(value, name, context);
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BytecodeContext visit(ClassDef classDef) {
		// TODO Auto-generated method stub
		return null;
	}

}
