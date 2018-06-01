package wyvern.stdlib.support;

public class ArrayListWrapper {
    public static final ArrayListWrapper arraylist = new ArrayListWrapper();
    public ArrayListWrapper() { }

    public DynArrayList makeArrayList() {
        return new DynArrayList();
    }
}

