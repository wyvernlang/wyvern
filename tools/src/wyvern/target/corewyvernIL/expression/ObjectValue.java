package wyvern.target.corewyvernIL.expression;

import java.util.List;

import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.DeclarationWithRHS;
import wyvern.target.corewyvernIL.decl.DefDeclaration;
import wyvern.target.corewyvernIL.decl.DelegateDeclaration;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.type.ValueType;

public class ObjectValue extends New implements Invokable {
	final EvalContext evalCtx; // captured eval context
	final boolean hasDelegate;
	ObjectValue delegateTarget;
	
	/** Precondition: the decls argument must be unique.
	 * It is owned by this ObjectValue after the constructor call.
	 */
	public ObjectValue(List<Declaration> decls, String selfName, ValueType exprType, DelegateDeclaration delegateDecl,EvalContext ctx) {
		super(decls, selfName, exprType);
		
		if (selfName == null || selfName.length() == 0)
			throw new RuntimeException("selfName invariant violated");
		evalCtx = ctx.extend(selfName, this);
		hasDelegate = delegateDecl != null ? true : false; 
		if (hasDelegate) {
			delegateTarget = (ObjectValue)ctx.lookup(delegateDecl.getFieldName());
		}
	}

	@Override
	public Value invoke(String methodName, List<Value> args) {
		EvalContext methodCtx = evalCtx;
		DefDeclaration dd = (DefDeclaration) findDecl(methodName);
		if (dd != null) {
			for (int i = 0; i < args.size(); ++i) {
				methodCtx = methodCtx.extend(dd.getFormalArgs().get(i).getName(), args.get(i));
			}
			return dd.getBody().interpret(methodCtx);
		}
		else if(hasDelegate) {
			return delegateTarget.invoke(methodName, args);
		}
		else {
			throw new RuntimeException("can't reach here");
		}
	}

	@Override
	public Value getField(String fieldName) {
		DeclarationWithRHS decl = (DeclarationWithRHS) findDecl(fieldName);
		if (decl != null) {
			return (Value) decl.getDefinition();
		}
		else if(delegateTarget != null && delegateTarget.findDecl(fieldName) != null) {
			return delegateTarget.getField(fieldName);
		}
		
		throw new RuntimeException("can't find field: " + fieldName);
	}

	public void setDecl(Declaration decl) {
		List<Declaration> decls = this.getDecls();
		for (int i = 0; i < decls.size(); ++i) {
			if (decl.getName().equals(decls.get(i).getName())) {
				decls.set(i, decl);
				return;
			}
		}
		throw new RuntimeException("cannot set decl " + decl.getName());
	}
	
}
