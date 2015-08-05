package wyvern.target.corewyvernIL.expression;

import java.util.List;

import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.type.ValueType;

public class ObjectValue extends New implements Value {
	public ObjectValue(List<Declaration> decls, String selfName, ValueType exprType) {
		super(decls, selfName, exprType);
	}
}
