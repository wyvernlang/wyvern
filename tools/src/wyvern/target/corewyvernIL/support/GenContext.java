package wyvern.target.corewyvernIL.support;

import static wyvern.tools.errors.ErrorMessage.VARIABLE_NOT_DECLARED;
import static wyvern.tools.errors.ToolError.reportError;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.typedAST.core.declarations.TypeVarDecl;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.core.declarations.TypeAbbrevDeclaration;
import wyvern.tools.typedAST.interfaces.TypedAST;

public abstract class GenContext extends TypeContext {
	
	private GenContext nextContext = null;
	
	protected GenContext(GenContext next) {
		nextContext = next;
	}
	
	public GenContext extend(String var, Expression expr, ValueType type) {
		return new VarGenContext(var, expr, type, this);
	}
	
	public final Expression lookupExp(String varName, FileLocation loc) {
		try {
			return getCallableExpr(varName).genExpr();
		} catch (RuntimeException e) {
			ToolError.reportError(VARIABLE_NOT_DECLARED, loc, varName);
			throw new RuntimeException("impossible");
		}
	}
	
	/**
	 * Gets the name of the container object for a type abbreviation.
	 * For a mapping T -> y.T, getContainerForTypeAbbrev(T) = y;
	 * 
	 * @param typeName the type abbreviation's name
	 * @return the name of the container object for the type abbreviation
	 */
	public abstract Path getContainerForTypeAbbrev(String typeName);
	
	public static GenContext empty() {
		return theEmpty;
	}
	
	protected abstract String endToString();
	
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
			return new MethodGenContext(methodName, newName, this, ast.getLocation()); 
		}
		else {
			assert (ast instanceof TypeAbbrevDeclaration);
			TypeAbbrevDeclaration typeAbbrevDecl = (TypeAbbrevDeclaration) ast;
			return new TypeGenContext(typeAbbrevDecl.getName(), newName, this); 
		}
		
		
	}

	/**
	 * Internal recursive version.
	 * 
	 * @param varName the method name
	 * @param origCtx the original context the lookupValue was performed in
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
	
	@Override
	protected GenContext getNext() {
		return nextContext;
	}
	
	public InterpreterState getInterpreterState() {
		return this.nextContext.getInterpreterState();
	}
}
