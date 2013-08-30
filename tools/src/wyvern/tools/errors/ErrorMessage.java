package wyvern.tools.errors;

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
	
	// Syntax errors
	LEXER_ERROR("Error dyring lexing (often caused by inconsistent whitespace for indentation)", 0),
	UNEXPECTED_INPUT("Unexpected input", 0),
	UNEXPECTED_INPUT_WITH_ARGS("Unexpected input: %ARG", 1),
	INDENT_DEDENT_MISMATCH("Expected dedent to match earlier indent", 0),
	EXPECTED_TOKEN_NOT_EOF("Expected an expression but reached end of file", 0),
	MISMATCHED_PARENTHESES("No matching close parenthesis", 0),
	UNEXPECTED_EMPTY_BLOCK("Indented block parsing error: nothing inside", 0),
	
	// Evaluation errors
	VALUE_CANNOT_BE_APPLIED("The value %ARG cannot be applied to an argument", 1),
	CANNOT_INVOKE("Cannot invoke operations on the value %ARG", 1),

	//Verification errors
	IMPORT_CYCLE("Import cycles have been found, with cycles\n%ARG", 1),

	ReaderError("An error has occured in import resolution of URI %ARG with exception:\n%ARG", 2);// end of error list
	
	private ErrorMessage(String message, int numArgs) {
		this.errorMessage = message;
		this.numArgs = numArgs;
	}
	
	public String getErrorMessage() {
		assert numArgs == 0;
		return errorMessage;
	}
	
	public String getErrorMessage(String argument) {
		assert numArgs == 1;
		return errorMessage.replaceFirst("%ARG", argument);
	}
	
	public String getErrorMessage(String arg1, String arg2) {
		assert numArgs == 2;
		String str = errorMessage.replaceFirst("%ARG", arg1);
		return str.replaceFirst("%ARG", arg2);
	}
	
	public String getErrorMessage(String arg1, String arg2, String arg3) {
		assert numArgs == 3;
		String str = errorMessage.replaceFirst("%ARG", arg1);
		str = str.replaceFirst("%ARG", arg2);
		return str.replaceFirst("%ARG", arg3);
	}
	
	public int numberOfArguments() {
		return numArgs;
	}
	
	private String errorMessage;
	private int numArgs;
}
