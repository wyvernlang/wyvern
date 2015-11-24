package wyvern.target.corewyvernIL.expression;

import java.util.List;

import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.DeclarationWithRHS;
import wyvern.target.corewyvernIL.decl.DefDeclaration;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.type.ValueType;

public class ObjectValue extends New implements Invokable {
	final EvalContext evalCtx; // captured eval context
	
	public ObjectValue(List<Declaration> decls, String selfName, ValueType exprType, EvalContext ctx) {
		super(decls, selfName, exprType);
		if (selfName == null || selfName.length() == 0)
			throw new RuntimeException("selfName invariant violated");
		evalCtx = ctx.extend(selfName, this);
	}

	@Override
	public Value invoke(String methodName, List<Value> args, EvalContext ctx) {
		ctx = ctx.combine(evalCtx);
		EvalContext methodCtx = ctx;
		DefDeclaration dd = (DefDeclaration) findDecl(methodName);
		for (int i = 0; i < args.size(); ++i) {
			methodCtx = methodCtx.extend(dd.getFormalArgs().get(i).getName(), args.get(i));
		}
		return dd.getBody().interpret(methodCtx);
	}

	@Override
	public Value getField(String fieldName, EvalContext ctx) {
		DeclarationWithRHS decl = (DeclarationWithRHS) findDecl(fieldName);
		return (Value) decl.getDefinition();
	}

}
