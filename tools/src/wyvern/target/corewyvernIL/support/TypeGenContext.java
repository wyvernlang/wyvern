package wyvern.target.corewyvernIL.support;

import java.util.LinkedList;
import java.util.List;

import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.TypeDeclaration;
import wyvern.target.corewyvernIL.decltype.ConcreteTypeMember;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.ValDeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.HasLocation;

public class TypeGenContext extends GenContext {

	String typeName;
	String objName;
	GenContext genContext;
	
	public TypeGenContext(String typeName, String newName, GenContext genContext) {
		this.typeName = typeName;
		this.objName = newName;
		this.genContext = genContext;
	}

	@Override
	public String getContainerForTypeAbbrev(String typeName) {
		if(this.typeName.equals(typeName)) return objName;
		else return genContext.getContainerForTypeAbbrev(typeName);
	}

	@Override
	public String toString() {
		return "GenContext[" + endToString();
	}
	
	@Override
	public String endToString() {
		return typeName + " : " + objName  + ", " + genContext.endToString();
	}

	@Override
	public ValueType lookup(String varName) {
		return genContext.lookup(varName);
	}

	@Override
	public CallableExprGenerator getCallableExprRec(String varName, GenContext origCtx) {
		return genContext.getCallableExprRec(varName, origCtx);
	}

}
