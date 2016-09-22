package wyvern.tools.typedAST.core.expressions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.CachingTypedAST;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.StaticTypeBinding;
import wyvern.tools.typedAST.core.binding.evaluation.HackForArtifactTaggedInfoBinding;
import wyvern.tools.typedAST.core.values.Obj;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.UnresolvedType;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.types.extensions.TypeInv;
import wyvern.tools.util.EvaluationEnvironment;
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
	public Value evaluate(EvaluationEnvironment env) {
		//TaggedInfo.resolveAll(env, this);

		Type mo = matchingOver.getType();

		TaggedInfo matchingOverTag = TaggedInfo.lookupTagByType(mo); // FIXME:

		if (matchingOver instanceof Variable) {
			Variable w = (Variable) matchingOver;
			//ClassType wType = (ClassType) env.lookupValue(w.getName()).getType();
			// System.out.println("wType = " + wType);
			// System.out.println("looked up = " + TaggedInfo.lookupTagByType(wType));
			// System.out.println("but mot = " + matchingOverTag);
			matchingOverTag = ((Obj)env.lookup(w.getName()).map(ib -> ib.getValue(env))
					.orElseThrow(() -> new RuntimeException("Invalid matching over tag"))).getTaggedInfo();
		}

		// System.out.println("Evaluating match with matchingOver = " + matchingOver + " its class " + matchingOver.getClass());
		/*
		Variable v = (Variable) matchingOver;
		System.out.println("v.getType() = " + v.getType());
		System.out.println("v.getName() = " + v.getName());
		System.out.println(env);
		System.out.println(env.lookupValue(v.getName()));

		System.out.println("mo = " + mo + " and its class is " + mo.getClass());
		System.out.println(env.lookupValue(v.getName()).getUse());
		System.out.println(env.lookupValue(v.getName()).getValue(env));

		TypeType ttmo = (TypeType) mo;
		System.out.println("ttmo.getName() is declared but not actual type = " + ttmo.getName());
		TypeType tttmo = (TypeType) ((MetadataWrapper) v.typecheck(env, Optional.empty())).getInner();
		System.out.println("v.type = " + tttmo.getName());

		TaggedInfo.dumpall(env);
		*/


		// System.out.println("Evaluating match over tag: " + matchingOverTag + " with matchingOver = " + matchingOver.getType());
		if (matchingOver.getType() instanceof ClassType) {
			ClassType ct = (ClassType) matchingOver.getType();
			// System.out.println("hmm = " + this.matchingOver.typecheck(env, Optional.empty()));
			// System.out.println("ct = " + ct.getName());
		}

		// System.out.println("matchingOverTag (latest) = " + matchingOverTag);
		int cnt = 0;

		for (Case c : cases) {
			cnt++;

			// String caseTypeName = getTypeName(c.getAST());

			// System.out.println("case "+ cnt + " = " + c);

			Type tt = c.getTaggedTypeMatch();

			TaggedInfo caseTag;
			if (tt instanceof TypeInv) {
				TypeInv ti = (TypeInv) tt;

				// System.out.println("Processing TypeInv case: " + ti);

				// FIXME: In ECOOP2015Artifact, I am trying to tell the difference between winMod.Win and bigWinMod.Win...

				Type ttti = ti.getInnerType();
				String mbr = ti.getInvName();


				if (ttti instanceof UnresolvedType) {
					Value objVal = env.lookup(((UnresolvedType) ttti).getName()).get().getValue(env);
					caseTag = ((Obj) objVal).getIntEnv().lookupBinding(mbr, HackForArtifactTaggedInfoBinding.class)
							.map(b -> b.getTaggedInfo()).orElseThrow(() -> new RuntimeException("Invalid tag invocation"));

				} else {
					//tt = ti.resolve(env); TODO: is this valid?
					caseTag = TaggedInfo.lookupTagByType(tt); // FIXME:
				}
			} else {
				caseTag = TaggedInfo.lookupTagByType(tt); // FIXME:
			}

			// System.out.println("case " + cnt + " type = " + tt);

			// System.out.println("caseTag = " + caseTag);

			if (caseTag != null && isSubtag(matchingOverTag, caseTag)) {
				// We've got a match, evaluate this case
				// System.out.println("MAAAAATTTCH!");
				return c.getAST().evaluate(env);
			}
		}

		// No match, evaluate the default case
		// System.out.println("DEFAULT: " + defaultCase.getAST().evaluate(env));
		return defaultCase.getAST().evaluate(env);
	}

	/**
	 * Checks if matchingOver is a subtag of matchTarget.
	 *
	 * Searches recursively to see if what we are matching over is a sub-tag of the given target.
	 *
	 * @return
	 */
	//TODO: rename this method to something like isSubtag()
	private boolean isSubtag(TaggedInfo matchingOver, TaggedInfo matchTarget) {
		if (matchingOver == null) throw new NullPointerException("Matching Binding cannot be null");
		if (matchTarget == null) throw new NullPointerException("match target cannot be null");

		// String matchingOverTag = matchingOver.getTagName();
		// String matchTargetTag = matchTarget.getTagName();

		// System.out.println("matchingOverTag = " + matchingOverTag + " and matchTargetTag = " + matchTargetTag);

		// FIXME: Why do equals when that may not correspond to the tags being the same? Only reference == is safe I guess?
		// if (matchingOverTag.equals(matchTargetTag)) return true;
		if (matchingOver == matchTarget) return true;

		// If caseOf is hopelessly broken, this is a "fix": return false; :-)d

		TaggedInfo ti = matchingOver.getCaseOfTaggedInfo();

		if (ti == null) return false;
		return isSubtag(ti, matchTarget); // FIXME:
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
    public void codegenToIL(GenerationEnvironment environment, ILWriter writer) {
        //TODO: Part of tags support
    }

	@Override
	public FileLocation getLocation() {
		return location;
	}

	public void resolve(Environment env) {
		if (this.defaultCase != null)
			this.defaultCase.resolve(env, this);

		for (Case c : this.cases) {
			c.resolve(env, this);
		}
	}

	@Override
	protected Type doTypecheck(Environment env, Optional<Type> expected) {
		this.resolve(env);

		Type matchOverType = matchingOver.typecheck(env, expected);
		// System.out.println("env = " + env);
		// System.out.println("matchingOver = " + matchingOver);
		// System.out.println("matchOverType = " + matchOverType);

		if (!(matchingOver instanceof Variable)) {
			throw new RuntimeException("Can only match over variable");
		}

		// Variable v = (Variable) matchingOver;
		// System.out.println("v = " + v);
		// System.out.println("v.getType()=" + v.getType());

		StaticTypeBinding staticBinding = getStaticTypeBinding(matchingOver, env);

		// System.out.println(staticBinding);

		/*
		if (staticBinding == null) {
			throw new RuntimeException("variable matching over must be statically tagged");
		}
		*/

		// Variable we're matching must exist and be a tagged type
		// String typeName = getTypeName(matchOverType);

		TaggedInfo matchTaggedInfo = TaggedInfo.lookupTagByType(matchOverType); // FIXME:

		// System.out.println(matchOverType);

		if (matchTaggedInfo == null) {
			ToolError.reportError(ErrorMessage.TYPE_NOT_TAGGED, matchingOver, matchOverType.toString());
		}

		// System.out.println(matchTaggedInfo);
		matchTaggedInfo.resolve(env, this);
		// System.out.println(matchTaggedInfo);

		checkNotMultipleDefaults();

		checkDefaultLast();

		Type returnType = typecheckCases(env, expected);

		checkAllCasesAreTagged(env);

		checkAllCasesAreUnique();

		checkSubtagsPreceedSupertags();

		// ONLY FOR STATICS FIXME:
		// System.out.println(env);
		if (staticBinding != null) {
			NameBinding nb = env.lookup(staticBinding.getTypeName());
			if (nb != null) {
				checkStaticSubtags(TaggedInfo.lookupTagByType(nb.getType())); //matchTaggedInfo
				checkBoundedAndUnbounded(TaggedInfo.lookupTagByType(nb.getType())); //matchTaggedInfo
			}
		}


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

		// System.out.println(expected);

		if (returnType == null) {
			if (defaultCase != null) {
				if (!expected.equals(Optional.empty())) {
					// System.out.println(defaultCase.getAST().getType());
					// System.out.println(expected.get());
					if (!expected.get().equals(defaultCase.getAST().getType())) {
						ToolError.reportError(ErrorMessage.MATCH_NO_COMMON_RETURN, this);
						return null;
					}
				}
			}
		}

		return returnType;
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

	private Type typecheckCases(Environment env, Optional<Type> expected) {
		Type commonType = null;
		//do actual type-checking on cases
		for (Case c : cases) {
			Type rt = c.getAST().typecheck(env, expected);
			// System.out.println("rt = " + rt);
			if (commonType == null) commonType = rt;
			if (!rt.equals(commonType)) {
				// System.out.println("WARNING: rt = " + rt + " and commonType = " + commonType);
				ToolError.reportError(ErrorMessage.MATCH_NO_COMMON_RETURN, this);
				return null;
			}
		}

		if (expected.equals(Optional.empty())) {
			return commonType;
		} else {
			// System.out.println("expected = " + expected);
			// System.out.println("commonType = " + commonType);
			if (commonType == null || /*expected.equals(commonType) ||*/ expected.get().equals(commonType)) {
				return commonType;
			} else {
				ToolError.reportError(ErrorMessage.MATCH_NO_COMMON_RETURN, this);
				return null;
			}
		}
	}

	private void checkAllCasesAreTagged(Environment env) {
		//All things we match over must be tagged types
		for (Case c : cases) {
			if (c.isDefault()) continue;

			Type tagName = c.getTaggedTypeMatch();

			if (tagName instanceof UnresolvedType) {
				UnresolvedType ut = (UnresolvedType) tagName;
				// System.out.println("ut = " + ut.resolve(env));
			}

			if (tagName instanceof TypeInv) {
				// TypeInv ti = (TypeInv) tagName;
				// System.out.println("ti = " + ti.resolve(env));
				// tagName = ti.resolve(env);
				// if (tagName instanceof UnresolvedType) {
					// tagName = ((UnresolvedType) tagName).resolve(env);
				// } DO NOT UNCOMMENT THIS AS BREAKS CASES
				return; // FIXME: Assume TypeInv will sort itself out during runtime.
			}

			// System.out.println(tagName);

			//check type exists
			// TypeBinding type = env.lookupValue(tagName.toString()); // FIXME:

			// if (type == null) {
			//	ToolError.reportError(ErrorMessage.TYPE_NOT_DECLARED, this, tagName.toString());
			// }

			//check it is tagged
			TaggedInfo info = TaggedInfo.lookupTagByType(tagName); // FIXME:

			if (info == null) {
				ToolError.reportError(ErrorMessage.TYPE_NOT_TAGGED, matchingOver, tagName.toString());
			}
		}

	}

	private void checkAllCasesAreUnique() {
		// All tagged types must be unique
		Set<Type> caseSet = new HashSet<Type>();

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
			TaggedInfo beforeTag = TaggedInfo.lookupTagByType(beforeCase.getTaggedTypeMatch()); // FIXME:

			for (int j = i + 1; j < cases.size(); j++) {
				Case afterCase = cases.get(j);

				if (afterCase.isDefault()) break;

				TaggedInfo afterTag = TaggedInfo.lookupTagByType(afterCase.getTaggedTypeMatch()); // FIXME:
				//TagBinding afterBinding = TagBinding.get(afterCase.getTaggedTypeMatch());

				if (afterTag != null && beforeTag != null && isSubtag(afterTag, beforeTag)) {
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
			TaggedInfo matchTarget = TaggedInfo.lookupTagByType(c.getTaggedTypeMatch()); // FIXME:

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
		List<Type> comprisesTags = matchBinding.getComprisesTags();

		//add this tag because it needs to be included too
		comprisesTags.add(matchBinding.getTagType());

		//check that each tag is present
		for (Type t : comprisesTags) {
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
	private boolean containsTagBinding(List<Case> cases, Type tagName) {
		for (Case c : cases) {
			//Found a match, this tag is present
			if (c.getTaggedTypeMatch().equals(tagName)) return true;
		}

		return false;
	}

	@Override
	protected ExpressionAST doClone(Map<String, TypedAST> nc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression generateIL(GenContext ctx, ValueType expectedType, List<TypedModuleSpec> dependencies) {
		// TODO Auto-generated method stub
		return null;
	}
}
