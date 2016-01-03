package wyvern.target.corewyvernIL.support;

import java.util.LinkedList;
import java.util.List;

import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.ValDeclaration;
import wyvern.target.corewyvernIL.decl.VarDeclaration;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.ValDeclType;
import wyvern.target.corewyvernIL.decltype.VarDeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.FieldGet;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.typedAST.interfaces.TypedAST;

public class VarGenContext extends GenContext {
	private String var;
	private Expression expr;
	private GenContext genContext;
	private ValueType type;
	
	public VarGenContext(String var, Expression expr, ValueType type, GenContext genContext) {
		this.var = var;
		this.expr = expr;
		this.type = type;
		this.genContext = genContext;
	}
	
	@Override
	public String toString() {
		return "GenContext[" + endToString();
	}
	
	@Override
	public String endToString() {
		return var + " : " + type + " = " + expr + ", " + genContext.endToString();
	}
	
	@Override
	public ValueType lookup(String varName) {
		if (varName.equals(var))
			return type;
		else
			return genContext.lookup(varName);
	}
	
	@Override
	public String getContainerForTypeAbbrev(String typeName) {
		return genContext.getContainerForTypeAbbrev(typeName);
	}

	@Override
	public CallableExprGenerator getCallableExprRec(String varName, GenContext origCtx) {
		if (varName.equals(var))
			return new DefaultExprGenerator(expr);
		else {
			return genContext.getCallableExprRec(varName, origCtx);
		}
	}	
}
