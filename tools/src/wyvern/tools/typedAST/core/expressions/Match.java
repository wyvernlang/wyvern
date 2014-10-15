package wyvern.tools.typedAST.core.expressions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.CachingTypedAST;
import wyvern.tools.typedAST.core.binding.StaticTypeBinding;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.types.extensions.TypeType;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.TreeWriter;

/**
 * Represents a match statement in Wyvern.
 * 
 * @author Troy Shaw
 */
public class Match extends CachingTypedAST implements CoreAST {

	private TypedAST matchingOver;

	private List<Case> cases;
	private Case defaultCase;

	/** Original list which preserves the order and contents. Needed for checking. */
	private List<Case> originalCaseList;

	private FileLocation location;

	public String toString() {
		return "Match: " + matchingOver + " with " + cases.size() + " cases and default: " + defaultCase;
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
	public Value evaluate(Environment env) { 
		String className = getTypeName(matchingOver.getType());
		
		TaggedInfo matchingOverTag = TaggedInfo.lookupTag(className);
		
		for (Case c : cases) {
			//String caseTypeName = getTypeName(c.getAST());
			TaggedInfo caseTag = TaggedInfo.lookupTag(c.getTaggedTypeMatch());
			
			if (isSubtag(matchingOverTag, caseTag)) {
				// We've got a match, evaluate this case
				return c.getAST().evaluate(env);
			}
		}
		
		// No match, evaluate the default case
		return defaultCase.getAST().evaluate(env);
	}
	
	/**
	 * Checks if matchingOver is a subtag of matchTarget.
	 * 
	 * Searches recursively to see if what we are matching over is a sub-tag of the given target.
	 * 
	 * @param tag
	 * @param currentBinding
	 * @return
	 */
	//TODO: rename this method to something like isSubtag()
	private boolean isSubtag(TaggedInfo matchingOver, TaggedInfo matchTarget) {
		if (matchingOver == null) throw new NullPointerException("Matching Binding cannot be null");
		if (matchTarget == null) throw new NullPointerException("match target cannot be null");
		
		String matchingOverTag = matchingOver.getTagName();
		String matchTargetTag = matchTarget.getTagName();
		
		if (matchingOverTag.equals(matchTargetTag)) return true;
		
		String matchingOverCaseOf = matchingOver.getCaseOfTag();
		
		if (matchingOverCaseOf == null) return false;
		else return isSubtag(TaggedInfo.lookupTag(matchingOverCaseOf), matchTarget);
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
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		return new Match(matchingOver, cases, defaultCase, location);
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
		Type matchOverType = matchingOver.typecheck(env, expected);
		
		if (!(matchingOver instanceof Variable)) {
			throw new RuntimeException("Can only match over variable");
		}

		StaticTypeBinding staticBinding = getStaticTypeBinding(matchingOver, env);
		
		if (staticBinding == null) {
			throw new RuntimeException("variable matching over must be statically tagged");
		}
		
		// Variable we're matching must exist and be a tagged type
		String typeName = getTypeName(matchOverType);

		TaggedInfo matchTaggedInfo = TaggedInfo.lookupTag(staticBinding.getTypeName());
				
		if (matchTaggedInfo == null) {
			ToolError.reportError(ErrorMessage.TYPE_NOT_TAGGED, matchingOver, typeName);
		}
		
		checkNotMultipleDefaults();
		
		checkDefaultLast();		
		
		typecheckCases(env, expected);

		checkAllCasesAreTagged(env);
		
		checkAllCasesAreUnique();

		checkSubtagsPreceedSupertags();
			
		checkBoundedAndUnbounded(matchTaggedInfo);

		checkStaticSubtags(matchTaggedInfo);
		
		
		// If we've omitted default, we must included all possible sub-tags
		if (defaultCase == null) {
			//first, the variables tag must use comprised-of!
			
			//next, the match cases must include all those in the comprises-of list
			if (true) {
				
			}
		}
		
		// If we've included default, we can't have included all subtags for a tag using comprised-of
		if (defaultCase != null) {
			// We only care if tag specifies comprises-of
			if (matchTaggedInfo.hasComprises()) {
				//all subtags were specified, error
				if (comprisesSatisfied(matchTaggedInfo)) {
					//ToolError.reportError(ErrorMessage.DEFAULT_PRESENT, matchingOver);
				}
			}
		}
		
		return Unit.getInstance();
	}

	/**
	 * Checks there is not more than one default.
	 */
	private void checkNotMultipleDefaults() {
		for (int numDefaults = 0, i = 0; i < originalCaseList.size(); i++) {
			Case c = originalCaseList.get(i);
			
			if (c.isDefault()) {
				numDefaults++;
				
				if (numDefaults > 1) {
					ToolError.reportError(ErrorMessage.MULTIPLE_DEFAULTS, matchingOver);
				}
			}
		}
	}

	/**
	 * Checks that if a default is present it is last.
	 */
	private void checkDefaultLast() {
		//check default is last (do this after counting so user gets more specific error message)
		for (int i = 0; i < originalCaseList.size(); i++) {
			if (originalCaseList.get(i).isDefault() && i != originalCaseList.size() - 1) {
				ToolError.reportError(ErrorMessage.DEFAULT_NOT_LAST, matchingOver);
			}
		}
	}
	
	private void typecheckCases(Environment env, Optional<Type> expected) {
		//do actual type-checking on cases
				for (Case c : cases) {
					c.getAST().typecheck(env, expected);
				}
	}
	
	private void checkAllCasesAreTagged(Environment env) {
		//All things we match over must be tagged types
		for (Case c : cases) {
			if (c.isDefault()) continue;
			
			String tagName = c.getTaggedTypeMatch();
			
			//check type exists
			TypeBinding type = env.lookupType(tagName);
			
			if (type == null) {
				ToolError.reportError(ErrorMessage.TYPE_NOT_DECLARED, this, tagName);
			}
			
			//check it is tagged
			TaggedInfo info = TaggedInfo.lookupTag(tagName);
			
			if (info == null) {
				ToolError.reportError(ErrorMessage.TYPE_NOT_TAGGED, matchingOver, tagName);
			}
		}
		
	}
	
	private void checkAllCasesAreUnique() {
		// All tagged types must be unique
		Set<String> caseSet = new HashSet<String>();				
				
		for (Case c : cases) {
			if (c.isTyped()) caseSet.add(c.getTaggedTypeMatch());
		}
				
		if (caseSet.size() != cases.size()) {
			ToolError.reportError(ErrorMessage.DUPLICATE_TAG, matchingOver);
		}
	}

	private void checkSubtagsPreceedSupertags() {
		//A tag cannot be earlier than one of its subtags
		for (int i = 0; i < cases.size() - 1; i++) {
			Case beforeCase = cases.get(i);
			TaggedInfo beforeTag = TaggedInfo.lookupTag(beforeCase.getTaggedTypeMatch());
			
			for (int j = i + 1; j < cases.size(); j++) {
				Case afterCase = cases.get(j);
				
				if (afterCase.isDefault()) break;
				
				TaggedInfo afterTag = TaggedInfo.lookupTag(afterCase.getTaggedTypeMatch());
				//TagBinding afterBinding = TagBinding.get(afterCase.getTaggedTypeMatch());
				
				if (isSubtag(afterTag, beforeTag)) {
					ToolError.reportError(ErrorMessage.SUPERTAG_PRECEEDS_SUBTAG, matchingOver, beforeTag.getTagName(), afterTag.getTagName());
				}
			}
		}
	}
	
	private void checkBoundedAndUnbounded(TaggedInfo matchTaggedInfo) {
		// If we're an unbounded type, check default exists
		if (!matchTaggedInfo.hasComprises()) {
			if (defaultCase == null) {
				ToolError.reportError(ErrorMessage.UNBOUNDED_WITHOUT_DEFAULT, matchingOver);
			}
		} else {
			//we're bounded. Check if comprises is satisfied
			boolean comprisesSatisfied = comprisesSatisfied(matchTaggedInfo);
			
			//if comprises is satisfied, default must be excluded
			if (comprisesSatisfied && defaultCase != null) {
				ToolError.reportError(ErrorMessage.BOUNDED_EXHAUSTIVE_WITH_DEFAULT, matchingOver);
			}

			//if comprises is not satisfied, default must be present
			if (!comprisesSatisfied && defaultCase == null) {
				ToolError.reportError(ErrorMessage.BOUNDED_INEXHAUSTIVE_WITHOUT_DEFAULT, matchingOver);
			}
		}
	}
	
	/**
	 * Checks that the tag we are matching over is a supertag of 
	 * every tag in the case-list.
	 * 
	 * This ensures that each tag could actually have a match and that case is
	 * not unreachable code.
	 * 
	 * @param matchingOverTag
	 */
	private void checkStaticSubtags(TaggedInfo matchingOver) {
		for (Case c : cases) {
			TaggedInfo matchTarget = TaggedInfo.lookupTag(c.getTaggedTypeMatch());
			
			if (!isSubtag(matchTarget, matchingOver)) {
				ToolError.reportError(ErrorMessage.UNMATCHABLE_CASE, this.matchingOver, matchingOver.getTagName(), matchTarget.getTagName());
			}
		}
	}
	
	private StaticTypeBinding getStaticTypeBinding(TypedAST varAST, Environment env) {
		if (varAST instanceof Variable) {
			Variable var = (Variable) varAST;
			
			StaticTypeBinding binding = env.lookupStaticType(var.getName());
			
			return binding;
		}
		
		return null;
	}
	
	private boolean comprisesSatisfied(TaggedInfo matchBinding) {
		List<String> comprisesTags = matchBinding.getComprisesTags();
		
		//add this tag because it needs to be included too
		comprisesTags.add(matchBinding.getTagName());
		
		//check that each tag is present 
		for (String t : comprisesTags) {
			if (containsTagBinding(cases, t)) continue;
			
			//tag wasn't present
			return false;
		}
		
		//we made it through them all
		return true;
	}

	/**
	 * Helper method to simplify checking for a tag.
	 * Returns true if the given binding tag is present in the list of cases.
	 * 
	 * @param cases
	 * @param binding
	 * @return
	 */
	private boolean containsTagBinding(List<Case> cases, String tagName) {
		for (Case c : cases) {
			//Found a match, this tag is present
			if (c.getTaggedTypeMatch().equals(tagName)) return true;
		}

		return false;
	}

	private String getTypeName(Type type) {
		if (type instanceof ClassType) {
			ClassType matchingOverClass = (ClassType) matchingOver.getType();
			return matchingOverClass.getName();
		} else if (type instanceof TypeType) {
			TypeType matchingOverClass = (TypeType) matchingOver.getType();
			return matchingOverClass.getName();
		}
		
		return null;
	}
	
	@Override
	protected TypedAST doClone(Map<String, TypedAST> nc) {
		// TODO Auto-generated method stub
		return null;
	}
}
