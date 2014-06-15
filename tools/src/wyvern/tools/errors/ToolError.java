package wyvern.tools.errors;

public class ToolError extends RuntimeException {
	// may want to distinguish evaluation errors from type checking errors at some point
	public static void reportEvalError(ErrorMessage message, String arg1, HasLocation errorLocation) {
		reportError(message, errorLocation, arg1);
	}

    public static void reportError(ErrorMessage message, HasLocation errorLocation, String... args) {
        throw new ToolError(message, errorLocation, args);
    }
    public static void reportError(ErrorMessage message, FileLocation errorLocation, String... args) {
        throw new ToolError(message, errorLocation, args);
    }

    protected ToolError(ErrorMessage message, FileLocation location, String... args) {
        super(message.getErrorMessage(args) + " on line number " +
                ((location != null)?location.toString():"NULL"));
        assert message.numberOfArguments() == args.length;
        this.errorMessage = message;
    }

	protected ToolError(ErrorMessage message, HasLocation errorLocation, String... args) {
        super(message.getErrorMessage(args) + " on line number " +
                ((errorLocation != null)?errorLocation.getLocation() + "":"NULL"));
        assert message.numberOfArguments() == args.length;
        this.errorMessage = message;
	}

	public ErrorMessage getTypecheckingErrorMessage() {
		return errorMessage;
	}

	private ErrorMessage errorMessage;	
	
	/**
	 * For serialization
	 */
	private static final long serialVersionUID = -4348846559537743643L;

}
