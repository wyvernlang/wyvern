package wyvern.target.corewyvernIL.support;

import java.util.LinkedList;
import java.util.List;

import wyvern.target.corewyvernIL.decl.VarDeclaration;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.FieldGet;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.type.ValueType;

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
		else if (super.getType(varName) != null) {
			String objName = super.getType(varName);
			return new FieldGet(new Variable(objName), varName);
		}
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
	public List<wyvern.target.corewyvernIL.decl.Declaration> genDeclTypeSeq() {
		List<wyvern.target.corewyvernIL.decl.Declaration> decls = super.genDeclTypeSeq();
	    VarDeclaration decl = new VarDeclaration(var, type, expr);
		decls.add(decl);
		return decls;
	} 
	
}
