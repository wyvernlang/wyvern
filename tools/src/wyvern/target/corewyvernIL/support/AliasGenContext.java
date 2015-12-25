package wyvern.target.corewyvernIL.support;

import java.util.List;

import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.types.Type;

public class AliasGenContext extends GenContext {

	
	
	private String alias;
	private Type type;
	private GenContext genContext;

	public AliasGenContext(String alias, Type refrenceType , GenContext genContext) {
		this.alias = alias;
		this.type = refrenceType;
		this.genContext = genContext;
	}
	
	@Override
	public String getType(String varName) {
		return genContext.getType(varName);
	}

	@Override
	public String toString() {
		return "GenContext[" + endToString();
	}
	
	@Override
	public String endToString() {
		return alias + " : " + type.toString()  + ", " + genContext.endToString();
	}

	@Override
	public List<Declaration> genDeclSeq(GenContext origCtx) {
		return genContext.genDeclSeq(origCtx);
	}

	@Override
	public List<DeclType> genDeclTypeSeq(GenContext origCtx) {
		return genContext.genDeclTypeSeq(origCtx);
	}

	@Override
	CallableExprGenerator getCallableExprRec(String varName, GenContext origCtx) {
		return getCallableExprRec(varName, origCtx);
	}

	@Override
	public ValueType lookup(String varName) {
		return lookup(varName);
	}

	@Override
	public ValueType getAliasType(String aliasName) {
		if (aliasName.equals(this.alias)) {
			return type.getILType(genContext);
		}
		else {
			return genContext.getAliasType(aliasName);
		}
	}

}
