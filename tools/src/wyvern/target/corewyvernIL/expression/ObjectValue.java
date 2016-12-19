package wyvern.target.corewyvernIL.expression;

import java.util.List;

import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.DeclarationWithRHS;
import wyvern.target.corewyvernIL.decl.DefDeclaration;
import wyvern.target.corewyvernIL.decl.DelegateDeclaration;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;

public class ObjectValue extends New implements Invokable {
	final EvalContext evalCtx; // captured eval context
	final boolean hasDelegate;
	ObjectValue delegateTarget;
	
	/** Precondition: the decls argument must be unique.
	 * It is owned by this ObjectValue after the constructor call.
	 */
	public ObjectValue(List<Declaration> decls, String selfName, ValueType exprType, DelegateDeclaration delegateDecl,FileLocation loc,EvalContext ctx) {
		super(decls, selfName, exprType, loc);
		
		if (selfName == null || selfName.length() == 0)
			throw new RuntimeException("selfName invariant violated");
		evalCtx = ctx.extend(selfName, this);
		hasDelegate = delegateDecl != null ? true : false; 
		if (hasDelegate) {
			delegateTarget = (ObjectValue)ctx.lookupValue(delegateDecl.getFieldName());
		}
	}

	@Override
	public Value invoke(String methodName, List<Value> args) {
		EvalContext methodCtx = evalCtx;
		DefDeclaration dd = (DefDeclaration) findDecl(methodName);
		if (dd != null) {
      if (args.size() != dd.getFormalArgs().size()) {
        throw new RuntimeException("invoke called on " + methodName + " with " + args.size() + " arguments, but " + dd.getFormalArgs().size() + " were expected");
      }
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

	@Override
	public ValueType getType() {
		return getExprType();
	}

	public EvalContext getEvalCtx() {
		return this.evalCtx;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ObjectValue other = (ObjectValue) obj;
		
		// Other ObjectValue needs the same declarations, in the same order.
		if (this.getDecls().size() != other.getDecls().size())
			return false;
		return this.getDecls().equals(other.getDecls());
		
	}
	
}
