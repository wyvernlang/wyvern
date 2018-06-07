package wyvern.stdlib.support;
import java.util.ArrayList;

public class DynArrayList {
    private ArrayList<Object> list;

    DynArrayList() {
        list = new ArrayList<>();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public boolean add(Object o) {
        return list.add(o);
    }

    public int size() {
        return list.size();
    }

    public Object get(int i) {
        return list.get(i);
    }
}
