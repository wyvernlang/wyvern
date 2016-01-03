package wyvern.target.corewyvernIL.support;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.decl.DefDeclaration;
import wyvern.target.corewyvernIL.decl.TypeDeclaration;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.FieldGet;
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.Type;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.typedAST.core.TypeVarDecl;
import wyvern.tools.typedAST.core.declarations.TypeAbbrevDeclaration;
import wyvern.tools.typedAST.interfaces.TypedAST;

public abstract class GenContext extends TypeContext {
	
	public GenContext extend(String var, Expression expr, ValueType type) {
		return new VarGenContext(var, expr, type, this);
	}
	
	public final Expression lookupExp(String varName) {
		return getCallableExpr(varName).genExpr();
	}
	
	/**
	 * Gets the name of the container object for a type abbreviation.
	 * For a mapping T -> y.T, getContainerForTypeAbbrev(T) = y;
	 * 
	 * @param typeName the type abbreviation's name
	 * @return the name of the container object for the type abbreviation
	 */
	public abstract String getContainerForTypeAbbrev(String typeName);
	
	public static GenContext empty() {
		return theEmpty;
	}
	
	abstract String endToString();
	
	private static GenContext theEmpty = new EmptyGenContext();

	public static String generateName() {
		return "var_"+(count++);
	} 
	private static int count = 0;

	/**
	 * Adding mapping for a declaration may include recursive calls.
	 * if ast is a method declaration f then add f->y.f in the mapping
	 * if ast is a type declaration T then add T->y.T in the mapping
	 * where y is an object:
	 * 	y = new { IL declarations };
	 * 
	 * @param newName the generated new name to symbolize the outer object
	 * @param ast the declaration of Wyvern Module System
	 * @return a new functional environment which extends the mapping
	 */
	public GenContext rec(String newName, TypedAST ast) {
		if(ast instanceof TypeVarDecl) {
			String typeName = ((TypeVarDecl) ast).getName();
			return new TypeGenContext(typeName, newName, this); 
		} else if(ast instanceof wyvern.tools.typedAST.core.declarations.DefDeclaration) {
			//assert (ast instanceof wyvern.tools.typedAST.core.declarations.DefDeclaration);
			wyvern.tools.typedAST.core.declarations.DefDeclaration methodDecl = (wyvern.tools.typedAST.core.declarations.DefDeclaration) ast;
			String methodName = methodDecl.getName();
			return new MethodGenContext(methodName, newName, this); 
		}
		else {
			assert (ast instanceof TypeAbbrevDeclaration);
			TypeAbbrevDeclaration typeAbbrevDecl = (TypeAbbrevDeclaration) ast;
			return new TypeGenContext(typeAbbrevDecl.getAlias(), newName, this); 
		}
		
		
	}

	/**
	 * Internal recursive version.
	 * 
	 * @param varName the method name
	 * @param origCtx the original context the lookup was performed in
	 * @return the CallableExprGenerator
	 */
	abstract CallableExprGenerator getCallableExprRec(String varName, GenContext origCtx);
	
	/**
	 * Gets a CallableExprGenerator for the variable.
	 * This is used to produce more efficient code in the case where
	 * the variable is a method, so that the method does not need to
	 * be eta-expanded when it is to be called directly.
	 * 
	 * @param varName the method name
	 * @return the CallableExprGenerator
	 */
	public final CallableExprGenerator getCallableExpr(String varName) {
		return getCallableExprRec(varName, this);
	}
}
