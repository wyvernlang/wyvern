package wyvern.targets.Common.wyvernIL.interpreter.visitors;

import wyvern.targets.Common.wyvernIL.IL.Def.Definition;
import wyvern.targets.Common.wyvernIL.IL.Expr.*;
import wyvern.targets.Common.wyvernIL.IL.Imm.Operand;
import wyvern.targets.Common.wyvernIL.IL.visitor.ExprVisitor;
import wyvern.targets.Common.wyvernIL.interpreter.core.BytecodeContext;
import wyvern.targets.Common.wyvernIL.interpreter.core.BytecodeContextImpl;
import wyvern.targets.Common.wyvernIL.interpreter.values.*;

import java.util.ArrayList;
import java.util.List;

/**
 * a ExprVisitor for the IL interpreter
 * @author Tal Man
 *
 */
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
		return inner.accept(new BytecodeOperandVisitor(context));
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
