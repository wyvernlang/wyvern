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
	public List<DeclType> genDeclTypeSeq(GenContext origCtx) {
		List<DeclType> declts = genContext == origCtx ? new LinkedList<DeclType>():genContext.genDeclTypeSeq(origCtx);
		DeclType declt = new ConcreteTypeMember(typeName, new NominalType(objName, typeName)); 
		declts.add(declt);
		
		return declts;
	}

	@Override
	public ValueType lookup(String varName) {
		return genContext.lookup(varName);
	}

	@Override
	public List<Declaration> genDeclSeq(GenContext origCtx, HasLocation location) {
		List<Declaration> decls = genContext == origCtx ? new LinkedList<Declaration>():genContext.genDeclSeq(origCtx, location);
		TypeDeclaration decl = new TypeDeclaration(typeName, new NominalType(objName, typeName));
		decls.add(decl);
		return decls;
	}

	@Override
	public CallableExprGenerator getCallableExprRec(String varName, GenContext origCtx) {
		return genContext.getCallableExprRec(varName, origCtx);
	}

}
