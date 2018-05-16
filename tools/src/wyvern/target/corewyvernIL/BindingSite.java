package wyvern.target.corewyvernIL;

public class BindingSite {
    private static int globalIndex = 0;
    private String name;
    private int index;

    public BindingSite(String name) {
        if (name == null || name.length() == 0) {
            throw new RuntimeException("bindinb name invariant violated");
        }

        index = globalIndex++;
        this.name = name;
    }

    @Override
    public String toString() {
        return name + '_' + index;
    }

    public String getName() {
        return name;
    }
}
