package wyvern.target.corewyvernIL.support;

import java.util.LinkedList;
import java.util.List;

import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.HasLocation;

public class EmptyGenContext extends GenContext {

	@Override
	public ValueType lookup(String varName) {
		throw new RuntimeException("Variable " + varName + " not found");
	}

	@Override
	String endToString() {
		return "]";
	}

	@Override
	public String getContainerForTypeAbbrev(String typeName) {
		return null;
		//throw new RuntimeException("Type " + varName + " not found");
	}

	@Override
	public List<Declaration> genDeclSeq(GenContext origCtx, HasLocation location) {
		return new LinkedList<Declaration>();
	}

	@Override
	public List<DeclType> genDeclTypeSeq(GenContext origCtx) {
		return new LinkedList<DeclType>();
	}

	@Override
	public CallableExprGenerator getCallableExprRec(String varName, GenContext origCtx) {
		throw new RuntimeException("Variable " + varName + " not found");
	}

}
