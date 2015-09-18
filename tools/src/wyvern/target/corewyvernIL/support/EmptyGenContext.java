package wyvern.target.corewyvernIL.support;

import java.util.LinkedList;
import java.util.List;

import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.type.ValueType;

public class EmptyGenContext extends GenContext {

	@Override
	public Expression lookupExp(String varName) {
		throw new RuntimeException("Variable " + varName + " not found");
	}

	@Override
	public ValueType lookup(String varName) {
		throw new RuntimeException("Variable " + varName + " not found");
	}

	@Override
	String endToString() {
		return "]";
	}

	@Override
	public String getType(String varName) {
		return null;
		//throw new RuntimeException("Type " + varName + " not found");
	}

	@Override
	public ILMethod getMethod(String varName) {
		return null;
		//throw new RuntimeException("Method " + varName + " not found");
	}

	@Override
	public List<Declaration> genDeclSeq() {
		return new LinkedList<Declaration>();
	}

	@Override
	public List<DeclType> genDeclTypeSeq() {
		return new LinkedList<DeclType>();
	}

}
