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
        super(message.getErrorMessage(args)
              + ((location != null) ? " at location " + location.toString() : ""));
        this.location = location;
        assert message.numberOfArguments() == args.length;
        this.errorMessage = message;
        this.arguments = args;
    }

    protected ToolError(ErrorMessage message, HasLocation errorLocation, String... args) {
        this(message, (errorLocation != null) ? errorLocation.getLocation() : null, args);
    }

    public ErrorMessage getTypecheckingErrorMessage() {
        return errorMessage;
    }

    private ErrorMessage errorMessage;
    private FileLocation location;
    private String[] arguments;

    public FileLocation getLocation() {
        return location;
    }

    public String[] getArguments() {
        return arguments;
    }

    public int getLine() {
        return (location == null) ? -1 : location.getLine();
    }

    /**
     * For serialization
     */
    private static final long serialVersionUID = -4348846559537743643L;

    public ToolError withNewLocation(FileLocation newLoc) {
        return new ToolError(errorMessage, newLoc, arguments);
    }

}
