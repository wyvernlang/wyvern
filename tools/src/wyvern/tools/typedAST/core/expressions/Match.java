package wyvern.tools.typedAST.core.expressions;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.CachingTypedAST;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

/**
 * Represents a match statement in Wyvern.
 * 
 * @author Troy Shaw
 */
public class Match extends CachingTypedAST implements CoreAST {

	private String matchingOver;
	
	//TODO: the default expression
	private Map<String, TypedAST> cases = new HashMap<String, TypedAST>();
	
	private FileLocation location;
	
	public Match(String matchingOver, FileLocation location) {
		this.matchingOver = matchingOver;
		
		this.location = location;
	}
	
	@Override
	public Value evaluate(Environment env) {
		for (String typeName : cases.keySet()) {
			if (typeName.equals(matchingOver)) {
				TypedAST branch = cases.get(typeName);
				
				return branch.evaluate(env);
			}
		}
		
		//TODO default case
		//question, are we allowed to not declare a default?
		
		//no match
		return UnitVal.getInstance(location);
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		Map<String, TypedAST> children = new HashMap<>();
		
		for (String type : cases.keySet()) {
			//is there a proper convention for names in children?
			children.put("match case: " + type, cases.get(type));
		}
		
		//TODO: add the default case if present
		return children;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		return new Match(matchingOver, location);
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		//TODO: is this meant to be empty?
	}

	@Override
	public FileLocation getLocation() {
		return location;
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	protected Type doTypecheck(Environment env, Optional<Type> expected) {
		//TODO: type checking?
		return null;
	}
}
