package wyvern.target.corewyvernIL.support;

import java.util.LinkedList;
import java.util.List;

import wyvern.target.corewyvernIL.decl.ValDeclaration;
import wyvern.target.corewyvernIL.decl.VarDeclaration;
import wyvern.target.corewyvernIL.decltype.ValDeclType;
import wyvern.target.corewyvernIL.decltype.VarDeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.FieldGet;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.type.ValueType;
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
	public Expression lookupExp(String varName) {
		if (varName.equals(var))
			return expr;
		/*
		else if (genContext.getType(varName) != null) {
			String objName = genContext.getType(varName);
			return new FieldGet(new Variable(objName), varName);
		}  
		*/
		// You are right, we do not need this.
		else {
			return genContext.lookupExp(varName);
		}
	}

	@Override
	public ValueType lookup(String varName) {
		if (varName.equals(var))
			return type;
		else
			return genContext.lookup(varName);
	}
	
	@Override
	public List<wyvern.target.corewyvernIL.decl.Declaration> genDeclSeq() {
		List<wyvern.target.corewyvernIL.decl.Declaration> decls = genContext.genDeclSeq();
	    ValDeclaration decl = new ValDeclaration(var, type, expr);
		decls.add(decl);
		return decls;
	} 
	
	@Override
	public List<wyvern.target.corewyvernIL.decltype.DeclType> genDeclTypeSeq() {
		List<wyvern.target.corewyvernIL.decltype.DeclType> declts = genContext.genDeclTypeSeq();
	    wyvern.target.corewyvernIL.decltype.DeclType declt = new ValDeclType(var, type);
		declts.add(declt);
		return declts;
	}

	@Override
	public String getType(String varName) {
		return genContext.getType(varName);
	}

	@Override
	public ILMethod getMethod(String varName) {
		return genContext.getMethod(varName);
	}
	
}
