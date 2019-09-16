package wyvern.stdlib.support;

public class CommandLineUtils {
    private static DynArrayList argumentList; // array list of command line arguments.
    public static final CommandLineUtils utils = new CommandLineUtils();

    public CommandLineUtils() {
    }

    public String get(int index) {
        return (String) argumentList.get(index);
    }

    public int size() {
        return argumentList.size();
    }

    public void setArgumentList(String[] arguments) {
        this.argumentList = new DynArrayList();
        for (String str : arguments) {
            argumentList.add(str);
        }
    }
}
