package wyvern.tools.bytecode.visitors;

import java.util.ArrayList;
import java.util.List;

import wyvern.targets.Common.WyvernIL.Def.Definition;
import wyvern.targets.Common.WyvernIL.Expr.BinOp;
import wyvern.targets.Common.WyvernIL.Expr.FnInv;
import wyvern.targets.Common.WyvernIL.Expr.Immediate;
import wyvern.targets.Common.WyvernIL.Expr.Inv;
import wyvern.targets.Common.WyvernIL.Expr.New;
import wyvern.targets.Common.WyvernIL.Imm.Operand;
import wyvern.targets.Common.WyvernIL.visitor.ExprVisitor;
import wyvern.tools.bytecode.core.BytecodeContext;
import wyvern.tools.bytecode.core.BytecodeContextImpl;
import wyvern.tools.bytecode.values.BytecodeClass;
import wyvern.tools.bytecode.values.BytecodeClassDef;
import wyvern.tools.bytecode.values.BytecodeFunction;
import wyvern.tools.bytecode.values.BytecodeTuple;
import wyvern.tools.bytecode.values.BytecodeValue;

public class BytecodeExnVisitor implements ExprVisitor<BytecodeValue> {

	private final BytecodeContext context;
	private final BytecodeOperandVisitor opVisitor;
	
	/**
	 * sets up the visitor with a context to work with
	 * @param visContext
	 * 		the context of the program at this point
	 */
	public BytecodeExnVisitor(BytecodeContext visContext) {
		context = visContext;
		opVisitor = new BytecodeOperandVisitor(context);
	}

	@Override
	public BytecodeValue visit(Inv inv) {
		BytecodeClass clas = (BytecodeClass) inv.getSource().accept(opVisitor);
		if(clas.getContext().getValue(inv.getId()) == null) {
			throw new RuntimeException("invoked a null");
		}
		BytecodeValue val = clas.getContext().getValue(inv.getId()).dereference();
		if(val instanceof BytecodeFunction) {
			((BytecodeFunction) val).setThis(clas);
		}
		return val;
	}

	@Override
	public BytecodeValue visit(BinOp binOp) {
		Operand l = binOp.getL();
		Operand r = binOp.getR();
		String op = binOp.getOp();
		BytecodeValue left = l.accept(opVisitor);
		BytecodeValue right = r.accept(opVisitor);
		return left.doInvoke(right, op);
	}

	@Override
	public BytecodeValue visit(FnInv fnInv) {
		BytecodeFunction fun;
		List<BytecodeValue> unpacked;
		fun = (BytecodeFunction) fnInv.getFn().accept(opVisitor);
		BytecodeValue arguments = fnInv.getArg().accept(opVisitor);
		if(arguments instanceof BytecodeTuple) {
			unpacked = ((BytecodeTuple) arguments).getValue();
		} else {
			unpacked = new ArrayList<BytecodeValue>();
			unpacked.add(arguments);
		}
		return fun.run(unpacked);
	}

	@Override
	public BytecodeValue visit(Immediate immediate) {
		Operand inner = immediate.getInner();
		return inner.accept(new BytecodeImmediateVisitor(context));
	}

	@Override
	public BytecodeValue visit(New aNew) {
		BytecodeContext newContext;
		List<Definition> defs = aNew.getDefs();
		BytecodeValue thisObject = context.getValue("this").dereference();
		BytecodeClass newClass;
		if(thisObject != null) {
			BytecodeClassDef thisClass = (BytecodeClassDef) thisObject;
			newContext = new BytecodeContextImpl(thisClass.getContext());
			newClass = (BytecodeClass) thisClass.getCompleteClass();
		} else {
			newContext = context;
			newClass = new BytecodeClass(newContext);
		}
		BytecodeContext tempContext = new BytecodeContextImpl(context);
		for(Definition def : defs) {
			def.accept(new BytecodeDefVisitor(newClass.getContext(),tempContext));
		}
		return newClass;
	}
}
