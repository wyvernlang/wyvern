package wyvern.tools.errors;

import java.util.regex.Matcher;

public enum ErrorMessage {
	// Type errors
	ACTUAL_FORMAL_TYPE_MISMATCH("Actual argument type %ARG does not match formal argument type %ARG", 2),
	TYPE_CANNOT_BE_APPLIED("Type %ARG cannot be applied to an argument", 1),
	TYPE_CANNOT_BE_ASSIGNED("Type %ARG cannot assigned to a value after initalization", 1),
	TYPE_NOT_DEFINED("Type %ARG is not defined", 1),
	VARIABLE_NOT_DECLARED("Variable %ARG has no type in the context", 1),
	TYPE_NOT_DECLARED("Type %ARG has no declaration in the context", 1),
	OPERATOR_DOES_NOT_APPLY("Operator %ARG cannot be applied to type %ARG", 2),
	OPERATOR_DOES_NOT_APPLY2("Operator %ARG cannot be applied to types %ARG and %ARG", 3),
	MUST_BE_LITERAL_CLASS("The Name %ARG must refer to a class declaration currently in scope", 1),
	NOT_SUBTYPE("%ARG is not a subtype of %ARG", 2),
	DUPLICATE_MEMBER("%ARG has more than one member named %ARG", 2),
	EXPECTED_RECORD_TYPE("Expected a type with members", 0),
	
	// Syntax errors
	LEXER_ERROR("Error dyring lexing (often caused by inconsistent whitespace for indentation)", 0),
	UNEXPECTED_INPUT("Unexpected input", 0),
	UNEXPECTED_INPUT_WITH_ARGS("Unexpected input: %ARG", 1),
	INDENT_DEDENT_MISMATCH("Expected dedent to match earlier indent", 0),
	EXPECTED_TOKEN_NOT_EOF("Expected an expression but reached end of file", 0),
	MISMATCHED_PARENTHESES("No matching close parenthesis", 0),
	UNEXPECTED_EMPTY_BLOCK("Indented block parsing error: nothing inside", 0),
	
	// Tagged Type errors
	//TODO: these need parameters that give more information
	
	//For tagged declaration
	TYPE_NOT_TAGGED("Type is not tagged: %ARG", 1),
	CIRCULAR_TAGGED_RELATION("Circular tagged hierarchy found with tag: %ARG, case-of: %ARG", 2),
	COMPRISES_RELATION_NOT_RECIPROCATED("The tag declared to comprise this tag is not a case-of this tag", 0),
	COMPRISES_EXCLUDES_TAG("The comprises clause of: %ARG, excludes a tag which is a case-of of this tag: %ARG", 2),
	
	//For match expression
	BOUNDED_EXHAUSTIVE_WITH_DEFAULT("Default cannot be present in satisfied bounded match", 0),
	BOUNDED_INEXHAUSTIVE_WITHOUT_DEFAULT("Default must be present in inexhaustive bounded match", 0),
	DUPLICATE_TAG("Duplicate tag cannot be present", 0),
	DEFAULT_NOT_LAST("Default case is present, but not last", 0),
	MULTIPLE_DEFAULTS("More than 1 default case defined", 0),
	SUPERTAG_PRECEEDS_SUBTAG("Supertag %ARG preceeds subtag %ARG: unreachable case", 2),
	UNBOUNDED_WITHOUT_DEFAULT("Default must be present when matching over unbounded tag", 0),
	UNMATCHABLE_CASE("A variable of tag-type %ARG cannot possibly match against case %ARG", 2),
	MATCH_NO_COMMON_RETURN("Match statement does not have a common return type", 0),
	
	// Evaluation errors
	VALUE_CANNOT_BE_APPLIED("The value %ARG cannot be applied to an argument", 1),
	CANNOT_INVOKE("Cannot invoke operations on the value %ARG", 1),
    JAVA_INVOCATION_ERROR("Invocation of java method %ARG failed with message %ARG", 2),

	//Verification errors
	IMPORT_CYCLE("Import cycles have been found, with cycles\n%ARG", 1),

	ReaderError("An error has occured in import resolution of URI %ARG with exception:\n%ARG", 2),// end of error list
	
	MODULE_TYPE_ERROR("%ARG not a correct module type\n", 1);
	
	private ErrorMessage(String message, int numArgs) {
		this.errorMessage = message;
		this.numArgs = numArgs;
	}

    public String getErrorMessage(String... args) {
        assert numArgs == args.length;

        String str = errorMessage;
        for (String arg : args) {
            str = str.replaceFirst("%ARG", Matcher.quoteReplacement(arg) );
        }
        return str;
    }
	
	public int numberOfArguments() {
		return numArgs;
	}
	
	private String errorMessage;
	private int numArgs;
}
