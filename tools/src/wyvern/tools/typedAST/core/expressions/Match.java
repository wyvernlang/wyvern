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
import wyvern.tools.typedAST.core.binding.Binding;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.TagBinding;
import wyvern.tools.typedAST.core.binding.TypeBinding;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Bool;
import wyvern.tools.types.extensions.ClassType;
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
		// We've already typechecked, so this cast should be safe 
		ClassType matchingOverClass = (ClassType) matchingOver.getType();
		String className = matchingOverClass.getName();
		
		TagBinding matchingOverBinding = TagBinding.get(className);
		//TODO: fix this, replace with real code
		
		for (Case c : cases) {
			TagBinding binding = TagBinding.get(c.getTaggedTypeMatch());
			//TODO: change to proper code: env.lookupBinding(c.getTaggedTypeMatch(), TagBinding.class).get();
			
			if (hasMatch(matchingOverBinding, binding.getName())) {
				// We've got a match, evaluate this case
				return c.getAST().evaluate(env);
			}
		}
		
		// No match, evaluate the default case
		return defaultCase.getAST().evaluate(env);
	}

	/**
	 * Searches recursively to see if what we are matching over is a sub-tag of the given target.
	 * @param tag
	 * @param currentBinding
	 * @return
	 */
	private boolean hasMatch(TagBinding matchingOver, String matchTarget) {
		if (matchingOver.getName().equals(matchTarget)) return true;
		
		if (matchingOver.getCaseOfParent() == null) return false;
		else return hasMatch(matchingOver.getCaseOfParent(), matchTarget);
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
		// First typecheck all children
		matchingOver.typecheck(env, expected);
		
		//Note: currently all errors given use matchingOver because it has a location
		//TODO: use the actual entity that is responsible for the error
		
		matchingOver.typecheck(env, expected);		
		
		// Check not more than 1 default
		for (int numDefaults = 0, i = 0; i < originalCaseList.size(); i++) {
			Case c = originalCaseList.get(i);
			
			if (c.isDefault()) {
				numDefaults++;
				
				if (numDefaults > 1) {
					ToolError.reportError(ErrorMessage.MULTIPLE_DEFAULTS, matchingOver);
				}
			}
		}
		
		//check default is last (do this after counting so user gets more specific error message)
		for (int i = 0; i < originalCaseList.size(); i++) {
		
			if (originalCaseList.get(i).isDefault() && i != originalCaseList.size() - 1) {
				ToolError.reportError(ErrorMessage.DEFAULT_NOT_LAST, matchingOver);
			}
		}
		
		
		// Variable we're matching must exist and be a tagged type
		Type matchingOverType = matchingOver.getType();
		
		if (!(matchingOverType instanceof ClassType)) {
			//TODO change this to be a typecheck error
			throw new RuntimeException("variable matching over must be of type ClassType");
		}
		
		ClassType matchingOverClass = (ClassType) matchingOver.getType();
		String className = matchingOverClass.getName();
		
		TagBinding matchBinding = TagBinding.get(className);
		
		if (matchBinding == null) {
			//TODO change this to a typecheck error
			throw new RuntimeException("Value is not tagged.");
		}
		
		for (Case c : cases) {
			if (c.isDefault()) continue;
			
			String tagName = c.getTaggedTypeMatch();
			
			Optional<ClassType> type = env.lookupBinding(tagName, ClassType.class);
			
			NameBinding binding = env.lookup(tagName);
			
			if (binding == null) {
				// type wasn't declared...
				ToolError.reportError(ErrorMessage.UNKNOWN_TAG, matchingOver);
			}
			
			Type t = binding.getType();
			
			if (t instanceof ClassType) {
				ClassType classType = (ClassType) t;
				
				String name = classType.getName();
				
				TagBinding tagBinding = TagBinding.get(name);
				
				if (tagBinding == null) {
					//not tagged
					ToolError.reportError(ErrorMessage.NOT_TAGGED, matchingOver);
				}
			}
		}
		
		// All tagged types must be unique
		Set<String> caseSet = new HashSet<String>();
		
		for (Case c : cases) {
			if (c.isTyped()) caseSet.add(c.getTaggedTypeMatch());
		}
		
		if (caseSet.size() != cases.size()) {
			ToolError.reportError(ErrorMessage.DUPLICATE_TAG, matchingOver);
		}
	
		// If we've omitted default, we must included all possible sub-tags
		if (defaultCase == null) {
			//first, the variables tag must use comprised-of!
			if (!matchBinding.hasAnyComprises()) {
				//TODO change to type-check exception
				ToolError.reportError(ErrorMessage.NO_COMPRISES, matchingOver);
			}
			
			//next, the match cases must include all those in the comprises-of list
			if (!comprisesSatisfied(matchBinding)) {
				ToolError.reportError(ErrorMessage.DEFAULT_NOT_PRESENT, matchingOver);
			}
		}
		
		// If we've included default, we can't have included all subtags for a tag using comprised-of
		if (defaultCase != null) {
			// We only care if tag specifies comprises-of
			if (matchBinding.hasAnyComprises()) {
				//all subtags were specified, error
				if (comprisesSatisfied(matchBinding)) {
					ToolError.reportError(ErrorMessage.DEFAULT_PRESENT, matchingOver);
				}
			}
		}
		
		return Unit.getInstance();
	}
	
	private boolean comprisesSatisfied(TagBinding matchBinding) {
		List<TagBinding> comprisesTags = new ArrayList<TagBinding>(matchBinding.getComprisesOf());
		
		//add this tag because it needs to be included too
		comprisesTags.add(matchBinding);
		
		//check that each tag is present 
		for (TagBinding t : comprisesTags) {
			if (containsTagBinding(cases, t)) continue;
			
			//if we reach here the tag wasn't present
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
	private boolean containsTagBinding(List<Case> cases, TagBinding binding) {
		for (Case c : cases) {
			//Found a match, this tag is present
			if (c.getTaggedTypeMatch().equals(binding.getName())) return true;
		}
		
		return false;
	}
}
