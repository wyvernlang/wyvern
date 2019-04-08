package wyvern.stdlib.support;

public class ArrayWrapper {

    public static final ArrayWrapper arr = new ArrayWrapper();

    public ArrayWrapper() {
    }

    
    public Object[] create(int size) {
        Object[] newarray = new Object[size];
        return newarray;
    }


    public int length(Object array) {

        Object[] newarray = (Object[]) array;
        return newarray.length;
    }
    
    public void set(Object array, int index, Object value) {

        Object[] newarray = (Object[]) array;
        newarray[index] = value;

    }
    public Object get(Object array, int index) {
    // casts array to an Object[], then returns array[index]
        Object[] newarray = (Object[]) array;
        return newarray[index];
    }

}






