package wyvern.tools.typedAST.core.expressions;

import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.interfaces.TypedAST;

public class Case {
	
	private String taggedTypeName;
	
	private TypedAST ast;
	
	//TODO refactor this class into two classes for each type?
	private CaseType caseType;
	
	/**
	 * Instantiates a new Case statement, which matches over the given tagged type name,
	 * executing the given AST.
	 * 
	 * @param caseType
	 * @param decls
	 */
	public Case(String taggedTypeName, TypedAST ast) {
		this.taggedTypeName = taggedTypeName;
		this.ast = ast;
		
		caseType = CaseType.TYPED;
	}
	
	/**
	 * Instantiates a new default Case statement, which is executed if no others match.
	 * 
	 * @param decls
	 */
	public Case(TypedAST ast) {
		this.ast = ast;
		
		caseType = CaseType.DEFAULT;
	}
	
	public String getTaggedTypeMatch() {
		return taggedTypeName;
	}
	
	public TypedAST getAST() {
		return ast;
	}
	
	public CaseType getCaseType() {
		return caseType;
	}
	
	//TODO: remove this helper function?
	public boolean isDefault() {
		return caseType == CaseType.DEFAULT;
	}
	
	//TODO: remove this helper function?
	public boolean isTyped() {
		return caseType == CaseType.TYPED;
	}
	
	/**
	 * The different types a case can be.
	 * 
	 * @author troy
	 *
	 */
	public enum CaseType {
		/** If the user specifies no type to match against. */
		DEFAULT,
		/** If the user specifies a type to match against. */
		TYPED
	}
}
