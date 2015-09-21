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
import wyvern.tools.typedAST.core.TypeVarDecl;
import wyvern.tools.typedAST.interfaces.TypedAST;

public abstract class GenContext extends TypeContext {
	
	/* ILMethod: the type of an IL method */
	public static class ILMethod {
		String objName;
		wyvern.tools.typedAST.core.declarations.DefDeclaration defDecl;
		
		public ILMethod(String objName, wyvern.tools.typedAST.core.declarations.DefDeclaration defDecl) {
			this.objName = objName;
			this.defDecl = defDecl;
		}
		
		public String getObjName() {
			return objName;
		}
		public List<FormalArg> getArgsILType() {
			return defDecl.getArgILTypes();
		}
		public ValueType getReturnILType() {
			return defDecl.getReturnILType();
		}
	}
	
	public GenContext extend(String var, Expression expr, ValueType type) {
		return new VarGenContext(var, expr, type, this);
	}
	
	public abstract Expression lookupExp(String varName);
	
	/**
	 * Getting the outer object name of a type.
	 * For a mapping T -> y.T, getType(T) = y;
	 * 
	 * @param varName the type name
	 * @return the outer object name of the of the type
	 */
	public abstract String getType(String varName);
	
	/**
	 * Getting the outer object name of a type.
	 * For a mapping f -> y.f, getMethod(f) = y;
	 * 
	 * @param varName the method name
	 * @return the outer object name of the of the method
	 */
	public abstract ILMethod getMethod(String varName);
	
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
		} else {
			assert (ast instanceof wyvern.tools.typedAST.core.declarations.DefDeclaration);
			wyvern.tools.typedAST.core.declarations.DefDeclaration methodDecl = (wyvern.tools.typedAST.core.declarations.DefDeclaration) ast;
			String methodName = methodDecl.getName();
			ILMethod method = new ILMethod(newName, methodDecl);
			return new MethodGenContext(methodName, method, this); 
		}
		
	}
	/**
	 * Generate the Wyvern IL declaration list for all mapping in the context
	 * @return the Wyvern IL declaration list for all mapping inside the context
	 */
	public abstract List<wyvern.target.corewyvernIL.decl.Declaration> genDeclSeq(); 
	/**
	 * Generate the Wyvern IL declType list for all mapping in the context
	 * @return the Wyvern IL declType list for all mapping inside the context
	 */
	public abstract List<wyvern.target.corewyvernIL.decltype.DeclType> genDeclTypeSeq(); 
	
}
