package wyvern.tools.typedAST.core.expressions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractExpressionAST;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;

/**
 * Represents a match statement in Wyvern.
 *
 * @author Troy Shaw
 */
public class Match extends AbstractExpressionAST implements CoreAST {

	private TypedAST matchingOver;

	private List<Case> cases;
	private Case defaultCase;

	/** Original list which preserves the order and contents. Needed for checking. */
	private List<Case> originalCaseList;

	private FileLocation location;

	public String toString() {
		return "Match: " + matchingOver + " with " + cases + " cases and default: " + defaultCase;
	}

	public Match(TypedAST matchingOver, List<Case> cases, FileLocation location) {
		//clone original list so we have a canonical copy
		this.originalCaseList = new ArrayList<Case>(cases);

		this.matchingOver = matchingOver;
		this.cases = cases;

		//find the default case and remove it from the typed cases
		for (Case c : cases) {
			if (c.isDefault()) {
				defaultCase = c;
				break;
			}
		}

		cases.remove(defaultCase);

		this.location = location;
	}

	/**
	 * Internal constructor to save from finding the default case again.
	 *
	 * @param matchingOver
	 * @param cases
	 * @param defaultCase
	 * @param location
	 */
	private Match(TypedAST matchingOver, List<Case> cases, Case defaultCase, FileLocation location) {
		this.matchingOver = matchingOver;
		this.cases = cases;
		this.defaultCase = defaultCase;
		this.location = location;
	}


	@Override
	public Map<String, TypedAST> getChildren() {
		Map<String, TypedAST> children = new HashMap<>();

		for (Case c : cases) {
			//is there a proper convention for names in children?
			children.put("match case: " + c.getTaggedTypeMatch(), c.getAST());
		}

		if (defaultCase != null) {
			children.put("match default-case: " + defaultCase.getTaggedTypeMatch(), defaultCase.getAST());
		}


		return children;
	}

	@Override
	public ExpressionAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		return new Match(matchingOver, cases, defaultCase, location);
	}

	@Override
	public FileLocation getLocation() {
		return location;
	}

	@Override
	public Expression generateIL(GenContext ctx, ValueType expectedType, List<TypedModuleSpec> dependencies) {
		// TODO Auto-generated method stub
		return null;
	}
}
