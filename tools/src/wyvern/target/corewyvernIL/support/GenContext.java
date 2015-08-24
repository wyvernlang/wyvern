package wyvern.target.corewyvernIL.support;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import wyvern.target.corewyvernIL.decl.DefDeclaration;
import wyvern.target.corewyvernIL.decl.TypeDeclaration;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.FieldGet;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.typedAST.core.TypeVarDecl;
import wyvern.tools.typedAST.interfaces.TypedAST;

public abstract class GenContext extends TypeContext {
	
	private Map<String, String> methodBinding = new HashMap<String, String>();
	private Map<String, String> typeBinding = new HashMap<String, String>();
	
	public GenContext extend(String var, Expression expr, ValueType type) {
		return new VarGenContext(var, expr, type, this);
	}
	
	public abstract Expression lookupExp(String varName);
	
	public static GenContext empty() {
		return theEmpty;
	}
	
	abstract String endToString();
	
	private static GenContext theEmpty = new EmptyGenContext();

	public static String generateName() {
		return "var_"+(count++);
	} 
	private static int count = 0;

	public GenContext rec(String newName, TypedAST ast) {
		if(ast instanceof TypeVarDecl) {
			String typeName = ((TypeVarDecl) ast).getName();
			typeBinding.put(typeName, newName+'.'+typeName);
		} else {
			assert (ast instanceof DefDeclaration);
			String methodName = ((DefDeclaration) ast).getName();
			methodBinding.put(methodName, newName+'.'+methodName);
		}
		return null;
	}

	public List<wyvern.target.corewyvernIL.decl.Declaration> genDeclTypeSeq() {
		List<wyvern.target.corewyvernIL.decl.Declaration> decls = 
				new LinkedList<wyvern.target.corewyvernIL.decl.Declaration>();
		
		for(Map.Entry<String, String> entry : methodBinding.entrySet()) {
			String methodName = entry.getKey();
			String objName = entry.getValue();
			DefDeclaration decl = new DefDeclaration(objName, null, null, null);
		}
		
		for(Map.Entry<String, String> entry : typeBinding.entrySet()) {
			String typeName = entry.getKey();
			String objName = entry.getValue();
			TypeDeclaration decl = new TypeDeclaration(typeName, new NominalType(objName, typeName));
		}

		return null;
	} 
	public ValueType genDeclSeq() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getMethod(String methodName) {
		return methodBinding.get(methodName);
	}
	
	public String getType(String typeName) {
		return typeBinding.get(typeName);
	}
}
