package wyvern.target.corewyvernIL.support;

import java.util.List;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.typedAST.interfaces.TypedAST;

public abstract class GenContext extends TypeContext {
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
		// TODO Auto-generated method stub
		return null;
	}

	public List<wyvern.target.corewyvernIL.decl.Declaration> genDeclTypeSeq() {
		// TODO Auto-generated method stub
		return null;
	}

	public ValueType genDeclSeq() {
		// TODO Auto-generated method stub
		return null;
	}
}
