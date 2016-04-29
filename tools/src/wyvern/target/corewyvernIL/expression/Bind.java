package wyvern.target.corewyvernIL.expression;

import java.io.IOException;

import wyvern.target.corewyvernIL.VarBinding;
import wyvern.target.corewyvernIL.support.EmptyTypeContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.ValueType;

public class Bind extends Let {
	public Bind(VarBinding binding, Expression inExpr) {
		super(binding, inExpr);
	}

	@Override
	public ValueType typeCheck(TypeContext ctx) {
		return doTypeCheck(ctx, EmptyTypeContext.empty());
	}

	@Override
	public void doPrettyPrint(Appendable dest, String indent) throws IOException {
		String newIndent = indent + "    ";
		dest.append("bind\n").append(newIndent)
		.append(getVarName()).append(" = ");
		getToReplace().doPrettyPrint(dest,newIndent);
		dest.append('\n').append(indent).append("in ");
		getInExpr().doPrettyPrint(dest,indent);
	}
}
