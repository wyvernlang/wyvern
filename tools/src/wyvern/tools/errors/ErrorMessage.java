package wyvern.tools.errors;

import java.util.regex.Matcher;

public enum ErrorMessage {
    // Type errors
    ACTUAL_FORMAL_TYPE_MISMATCH("Actual argument type %ARG does not match formal argument type %ARG", 2),
    CANNOT_INFER_GENERIC("Cannot infer the type of the generic argument at the call site. "
            + "Please provide the type of the generic argument at the call site.", 0),
    EXTRA_GENERICS_AT_CALL_SITE("More generic arguments were provided at the call site than in the declaration of the function.", 0),
    MISSING_GENERICS_AT_CALL_SITE("Generic argument(s) for the method %ARG "
            + "were not inferrable and must be provided at the call site", 1),
    TYPE_CANNOT_BE_APPLIED("Type %ARG cannot be applied to an argument", 1),
    CANNOT_BE_ASSIGNED("Member %ARG cannot be assigned after initalization", 1),
    TYPE_NOT_DEFINED("Type %ARG is not defined", 1),
    VARIABLE_NOT_DECLARED("No variable named \"%ARG\" is in scope", 1),
    NO_SUCH_METHOD("There is no visible method named %ARG in type %ARG", 2),
    MUST_INSTANTIATE("Must instantiate module def %ARG before using it", 1),
    DYNAMIC_METHOD_ERROR("Called method %ARG, but the method does not exist on the receiver object.  Did you use type Dyn?", 1),
    JAVA_NULL_EXCEPTION("Called method %ARG on a null pointer from Java", 1),
    NO_SUCH_FIELD("There is no visible field named %ARG", 1),
    NO_METHOD_WITH_THESE_ARG_TYPES("The callee method cannot accept actual arguments with types: '%ARG'", 1),
    NOT_A_METHOD("%ARG is not a method", 1),
    TYPE_NOT_DECLARED("Type %ARG has no declaration in the context", 1),
    OPERATOR_DOES_NOT_APPLY("Operator %ARG cannot be applied to type %ARG", 2),
    OPERATOR_DOES_NOT_APPLY2("Operator %ARG cannot be applied to types %ARG and %ARG", 3),
    MUST_BE_LITERAL_CLASS("The Name %ARG must refer to a class declaration currently in scope", 1),
    NOT_SUBTYPE("%ARG is not a subtype of %ARG; %ARG", 3),
    ASSIGNMENT_SUBTYPING("The assigned value's type %ARG is not a subtype of the left-hand side type %ARG of this assignment; %ARG", 3),
    DUPLICATE_MEMBER("%ARG has more than one member named %ARG", 2),
    EXPECTED_RECORD_TYPE("Expected a type with members", 0),
    NO_SUCH_TYPE_MEMBER("No such type member: %ARG", 1),
    CANNOT_INFER_ARG_TYPE("Cannot infer the argument type as there is no expected type for the function expression", 0),
    NOT_ASSIGNABLE("The left-hand side of the assignment is not an var or field", 0),
    MUST_BE_ASSIGNED_TO_RESOURCE_TYPE("This new statement captures a resource and must be assigned to a resource type", 0),
    CANNOT_USE_METADATA_IN_SAME_FILE("Cannot use a TSL in the same file as the defining type", 0),
    NO_EXPECTED_TYPE("Cannot parse a DSL block without an expected type", 0),
    NO_METADATA_FROM_RESOURCE("Cannot load metadata from a resource module", 0),
    MUST_BE_A_RESOURCE("%ARG must be a resource type", 1),
    MUST_BE_A_RESOURCE_MODULE("%ARG must be a resource module", 1),
    NO_METADATA_WHEN_PARSING_TSL("Cannot parse TSL because type %ARG has no metadata", 1),
    METADATA_MUST_INCLUDE_PARSETSL("Metadata used to parse a TSL must include a parseTSL method", 0),
    METADATA_MUST_BE_AN_OBJECT("Metadata of type %ARG must be an object", 1),
    WRONG_NUMBER_OF_ARGUMENTS("Wrong number of arguments, expected %ARG, but found %ARG", 2),
    SYNTAX_FOR_NO_ARG_LAMBDA("Use \"() => <expression>\" rather than \"x => <expression>\" for a zero-argument function expression", 0),
    CANNOT_AVOID_VARIABLE("Cannot avoid variable %ARG in type of this expression.  "
            + "If this is the last line in the program, try ending the program with a value of built-in type, e.g. the integer 0.", 1),
    FORWARD_MUST_BE_VARIABLE("Expected variable in forward declaration, got %ARG", 1),
    QUALIFIED_TYPES_ONLY_FIELDS("Qualified types can only include val fields", 0),
    ILLEGAL_JUXTAPOSITION("Juxtaposed an additional argument to something that was not an application", 0),
    ILLEGAL_BINARY_JUXTAPOSITION("Cannot juxtapose an additional argument to a binary operation", 0),
    VAL_NEEDS_TYPE("val declaration %ARG is inside a new statement and thus needs a type annotation", 1),
    REC_NEEDS_TYPE("rec declaration %ARG is inside a new statement and thus needs a type annotation", 1),
    EFFECT_ANNOTATION_SEPARATION("Effect-annotated module depends on an effect-unannotated module", 0),
    EFFECT_ANNOTATION_DEF("Module definition has incorrect annotation", 0),
    PURE_MODULE_ANNOTATION("Pure module should always have empty effect annotation", 0),

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
    COMPRISES_EXCLUDES_TAG("%ARG cannot extend %ARG because it is not listed among the comprised types", 2),
    ILLEGAL_TAG_INSTANCE("Cannot instantiate type %ARG; must specify one of the types it is comprised of", 1),

    //For match expression
    BOUNDED_EXHAUSTIVE_WITH_DEFAULT("Default cannot be present in satisfied bounded match", 0),
    BOUNDED_INEXHAUSTIVE_WITHOUT_DEFAULT("Default must be present in inexhaustive bounded match", 0),
    DUPLICATE_TAG("Duplicate tag cannot be present", 0),
    DEFAULT_NOT_LAST("Default case is present, but not last", 0),
    MULTIPLE_DEFAULTS("More than 1 default case defined", 0),
    SUPERTAG_PRECEEDS_SUBTAG("Supertag %ARG preceeds subtag %ARG: unreachable case", 2),
    UNBOUNDED_WITHOUT_DEFAULT("Default must be present when matching over unbounded tag", 0),
    UNMATCHABLE_CASE("Case type %ARG is not a subtype of match expression type %ARG: %ARG", 3),
    UNMATCHED_CASE("Matched value with tag %ARG has no matching case arm", 1),
    CASE_TYPE_MISMATCH("The types of the case branches do not match: please have one return a common supertype of %ARG and %ARG", 2),

    // Evaluation errors
    VALUE_CANNOT_BE_APPLIED("The value %ARG cannot be applied to an argument", 1),
    CANNOT_INVOKE("Cannot invoke operations on the value %ARG", 1),
    JAVA_INVOCATION_ERROR("Invocation of java method %ARG failed with message %ARG", 2),
    STACK_OVERFLOW("Stack overflow", 0),

    //Verification errors
    IMPORT_CYCLE("Import cycles have been found, with cycles\n%ARG", 1),
    IMPORT_MUST_BE_STATIC_FIELD("Imported Java field %ARG must be static", 1),
    IMPORT_NOT_FOUND("Import %ARG not found", 1),

    ReaderError("An error has occured in import resolution of URI %ARG with exception:\n%ARG", 2), // end of error list

    MODULE_TYPE_ERROR("%ARG not a correct module type\n", 1),
    MODULE_NAME_ERROR("%ARG should be in a file of the same name", 1),

    PARSE_ERROR("Parse error: %ARG", 1),
    READ_FILE_ERROR("Could not read file %ARG", 1),
    MODULE_NOT_FOUND_ERROR("Could not find %ARG %ARG in either the current Wyvern path or the standard library", 2),
    SCRIPT_REQUIRED_MODULE_ONLY_JAVA("A module required by a top-level script must have exactly one argument, a platform such as java", 0),
    NOT_AN_FFI("Expected an FFI object as the schema in an import URI", 0),
    SCHEME_NOT_RECOGNIZED("import scheme %ARG not recognized; did you forget to \"require java\"?", 1),
    UNSAFE_JAVA_IMPORT("To import the %ARG object %ARG, make sure you \"require %ARG\" "
            + "(security experts only: if this object is harmless, you can add it to the built-in whitelist (see Globals.java)", 3),
    ILLEGAL_ESCAPE_SEQUENCE("Illegal escape sequence", 0),
    UNCLOSED_STRING_LITERAL("Unclosed string literal", 0),
    NO_ABSTRACT_TYPES_IN_OBJECTS("Abstract types may not be declared in objects or modules, only in type definitions", 0),
    METHODS_MUST_BE_INVOKED("Cannot access a method as if it were a field; use ()", 0),
    TSL_ERROR("Error in type-specific language: %ARG", 1),
    CANNOT_APPLY_GENERIC_ARGUMENTS("Cannot apply generic arguments: type %ARG is abstract", 1),
    NO_TYPE_MEMBER("Cannot find enough type members to apply type argument %ARG", 1),
    NO_EFFECT_MEMBER("Cannot find enough effect members to apply effect argument %ARG", 1),
    NON_TYPE_ARGUMENT("Cannot apply generic argument of kind %ARG to an abstract type", 1),
    NON_EFFECT_ARGUMENT("Cannot apply generic argument of kind %ARG to an abstract effect", 1),

    // effects errors
    MISTAKEN_DSL("Invalid characters for effect--should not be a DSL block: \"effect %ARG = {%ARG}\"", 2),
    UNDEFINED_EFFECT("Effect \"%ARG\" is undefined", 1),
    EFFECT_NOT_IN_SCOPE("Effect \"%ARG\" not found in scope", 1),
    //    EFFECT_OF_VAR_NOT_FOUND("Effect \"%ARG\" not found for variable \"%ARG\"", 2),
    RECURSIVE_EFFECT("Effect \"%ARG\" is being defined recursively", 1),
    UNKNOWN_EFFECT("Effects of method call \"%ARG\" are unknown", 1),

    //ARCHITECTURE ERRORS
    DUPLICATE_MEMBER_NAMES("multiple members declared with name \"%ARG\"", 1),
    DUPLICATE_TYPE_DEFINITIONS("multiple types defined with name \"%ARG\"", 1),
    COMPONENT_DEPENDENCY_INCONSISTENCY("module def and component \"%ARG\" have inconsistent dependencies/fields and ports", 1),
    MODULE_DEF_NOT_FOUND("module def \"%ARG\" not found in current Wyvern path or the standard library", 1),
    DUPLICATE_CONNECTOR_USE("connector \"%ARG\" has been used in multiple attachments", 1),
    MEMBER_NOT_DECLARED("%ARG \"%ARG\" has not been declared", 2),
    ARCH_TYPE_NOT_DEFINED("%ARG type \"%ARG\" is not defined", 2),
    CONNECTOR_VAL_INCONSISTENCY("type properties and connector \"%ARG\" have inconsistent val declarations", 1),
    INVALID_CONNECTOR_METADATA("properties of connector \"%ARG\" contain invalid metadata", 1),
    INVALID_CONNECTOR_PORTS("ports of connector\"%ARG\" are incompatible", 1),
    DUPLICATE_GENERATED_MODULES("multiple modules generated with name \"%ARG\"", 1);

    ErrorMessage(String message, int numArgs) {
        errorMessage = message;
        this.numArgs = numArgs;
    }

    public String getErrorMessage(String... args) {
        assert numArgs == args.length;

        String str = errorMessage;
        for (final String arg : args) {
            str = str.replaceFirst("%ARG", Matcher.quoteReplacement(arg));
        }
        return str;
    }

    public int numberOfArguments() {
        return numArgs;
    }

    private String errorMessage;
    private int numArgs;
}
