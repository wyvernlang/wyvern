package wyvern.stdlib.support;

public class CommandLineUtils {
    private static String[] arguments; // array of command line arguments.
    private static DynArrayList argumentsArrayList; // array list of command line arguments.
    public static final CommandLineUtils utils = new CommandLineUtils();

    private CommandLineUtils() {
    }

    public CommandLineUtils(String[] arguments) {
        this.arguments = arguments;
        this.convertToDynArrayList();
    }

    public DynArrayList getArgs() {
        return argumentsArrayList;
    }

    public int getLength() {
        return arguments.length;
    }

    private void convertToDynArrayList() {
        argumentsArrayList = new DynArrayList();
        for (String str : arguments) {
            argumentsArrayList.add(str);
        }
    }
}