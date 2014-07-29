package wyvern.targets.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import wyvern.tools.typedAST.core.declarations.KeywordDeclaration;
import wyvern.tools.typedAST.core.declarations.ValDeclaration;
import wyvern.tools.typedAST.core.declarations.VarDeclaration;
import wyvern.tools.typedAST.core.expressions.KeywordInvocation;
import wyvern.tools.typedAST.core.expressions.Variable;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.visitors.BaseASTVisitor;
import wyvern.tools.types.Type;
import wyvern.tools.util.Pair;

public class VariableResolver extends BaseASTVisitor {
	private HashMap<String, Type> externalContext;
	private HashMap<String, Type> depends;
	public VariableResolver(Map<String, Type> externalContext) {
		this.depends = new HashMap<String, Type>();
		this.externalContext = new HashMap<String, Type>(externalContext);
	}
	
	public List<Pair<String, Type>> getUsedVars() {
		List<Pair<String, Type>> pairs = new ArrayList<Pair<String, Type>>();
		for (Entry<String, Type> entry : depends.entrySet())
			pairs.add(new Pair<String, Type>(entry.getKey(), entry.getValue()));
		return pairs;
	}
	
	@Override
	public void visit(ValDeclaration valDecl) {
		if (valDecl.getDefinition() != null)
			((CoreAST)valDecl.getDefinition()).accept(this);
		if (externalContext.containsKey(valDecl.getName()))
			externalContext.remove(valDecl.getName());
	}
	
	@Override
	public void visit(VarDeclaration varDecl) {
		if (varDecl.getDefinition() != null)
			((CoreAST)varDecl.getDefinition()).accept(this);
		if (externalContext.containsKey(varDecl.getName()))
			externalContext.remove(varDecl.getName());
	}
	
	@Override
	public void visit(Variable var) {
		if (externalContext.containsKey(var.getName()))
			depends.put(var.getName(), var.getType());
	}

	@Override
	public void visit(KeywordDeclaration keywordDeclaration) {
		// TODO Auto-generated method stub
		// TODO (Stanley) Whether visit here?
	}

	@Override
	public void visit(KeywordInvocation keywordInvocation) {
		// TODO Auto-generated method stub
		// TODO (Stanley) Not sure what to do here visit here?
	}
	
	//TODO: Declsequence
}
