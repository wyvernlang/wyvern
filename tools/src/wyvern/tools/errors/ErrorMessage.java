package wyvern.tools.errors;

import java.util.regex.Matcher;

public enum ErrorMessage {
	// Type errors
	ACTUAL_FORMAL_TYPE_MISMATCH("Actual argument type %ARG does not match formal argument type %ARG", 2),
    EXTRA_GENERICS_AT_CALL_SITE("More generic arguments were provided at the call site than in the declaration of the function.", 0),
	TYPE_CANNOT_BE_APPLIED("Type %ARG cannot be applied to an argument", 1),
	CANNOT_BE_ASSIGNED("Member %ARG cannot be assigned after initalization", 1),
	TYPE_NOT_DEFINED("Type %ARG is not defined", 1),
	VARIABLE_NOT_DECLARED("No variable named %ARG is in scope", 1),
	NO_SUCH_METHOD("There is no visible method named %ARG", 1),
	NO_SUCH_FIELD("There is no visible field named %ARG", 1),
	NO_METHOD_WITH_THESE_ARG_TYPES("There is no visible method '%ARG'", 1),
	NOT_A_METHOD("%ARG is not a method", 1),
	TYPE_NOT_DECLARED("Type %ARG has no declaration in the context", 1),
	OPERATOR_DOES_NOT_APPLY("Operator %ARG cannot be applied to type %ARG", 2),
	OPERATOR_DOES_NOT_APPLY2("Operator %ARG cannot be applied to types %ARG and %ARG", 3),
	MUST_BE_LITERAL_CLASS("The Name %ARG must refer to a class declaration currently in scope", 1),
	NOT_SUBTYPE("%ARG is not a subtype of %ARG", 2),
	ASSIGNMENT_SUBTYPING("The assigned value is not a subtype of the left-hand side of this assignment", 0),
	DUPLICATE_MEMBER("%ARG has more than one member named %ARG", 2),
	EXPECTED_RECORD_TYPE("Expected a type with members", 0),
	CANNOT_INFER_ARG_TYPE("Cannot infer the argument type as there is no expected type for the function expression", 0),
	NOT_ASSIGNABLE("The left-hand side of the assignment is not an var or field", 0),
	MUST_BE_ASSIGNED_TO_RESOURCE_TYPE("This new statement captures a resource and must be assigned to a resource type", 0),
	CANNOT_USE_METADATA_IN_SAME_FILE("Cannot use a TSL in the same file as the defining type",0),
	NO_EXPECTED_TYPE("Cannot parse a DSL block without an expected type", 0),
	NO_METADATA_FROM_RESOURCE("Cannot load metadata from a resource module",0),
	MUST_BE_A_RESOURCE("%ARG must be a resource type",1),
	MUST_BE_A_RESOURCE_MODULE("%ARG must be a resource module",1),
	METADATA_MUST_INCLUDE_PARSETSL("Metadata used to parse a TSL must include a parseTSL method",0),
	METADATA_MUST_BE_AN_OBJECT("Metadata of type %ARG must be an object",1),
	WRONG_NUMBER_OF_ARGUMENTS("Wrong number of arguments, expected %ARG",1),
	SYNTAX_FOR_NO_ARG_LAMBDA("Use \"() => <expression>\" rather than \"x => <expression>\" for a zero-argument function expression",0),
	CANNOT_AVOID_VARIABLE("Cannot avoid variable %ARG in type of this expression", 1),
  DELEGATE_MUST_BE_VARIABLE("Expected variable in delegate declaration, got %ARG", 1),

	// Syntax errors
	LEXER_ERROR("Error during lexing (often caused by inconsistent whitespace for indentation)", 0),
	UNEXPECTED_INPUT("Unexpected input", 0),
	UNEXPECTED_INPUT_WITH_ARGS("Unexpected input: %ARG", 1),
	INDENT_DEDENT_MISMATCH("Expected dedent to match earlier indent", 0),
	INCONSISTENT_INDENT("Expected indent to match or extend earlier indent", 0),
	EXPECTED_TOKEN_NOT_EOF("Expected an expression but reached end of file", 0),
	EXPECTED_NEW_BLOCK("Expected an indented block with definitions for the new expression", 0),
	EXPECTED_DSL_BLOCK("Expected an indented DSL block defining this DSL expression", 0),
	MISMATCHED_PARENTHESES("No matching close parenthesis", 0),
	UNEXPECTED_EMPTY_BLOCK("Indented block parsing error: nothing inside", 0),
	ILLEGAL_INDENTED_BLOCK("Indented block cannot appear here", 0),

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

	MODULE_TYPE_ERROR("%ARG not a correct module type\n", 1),

	PARSE_ERROR("Parse error: %ARG", 1),
	READ_FILE_ERROR("Could not read file %ARG", 1),
	MODULE_NOT_FOUND_ERROR("Could not find %ARG %ARG in either the current Wyvern path or the standard library", 2),
	SCRIPT_REQUIRED_MODULE_ONLY_JAVA("A module required by a top-level script can only have java as its requirement", 0),
	NOT_AN_FFI("Expected an FFI object as the schema in an import URI", 0),
	SCHEME_NOT_RECOGNIZED("import scheme %ARG not recognized; did you forget to \"require java\"?", 1),
	UNSAFE_JAVA_IMPORT("To import the java package %ARG, make sure you \"require java\" or add the package to the built-in whitelist (experts only)", 1),
	ILLEGAL_ESCAPE_SEQUENCE("Illegal escape sequence", 0),
	UNCLOSED_STRING_LITERAL("Unclosed string literal", 0),
	NO_ABSTRACT_TYPES_IN_OBJECTS("Abstract types may not be declared in objects or modules, only in type definitions", 0),
	METHODS_MUST_BE_INVOKED("Cannot access a method as if it were a field; use ()",0),
	TSL_ERROR("Error in type-specific language: %ARG",1),
	;

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
