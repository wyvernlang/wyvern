package wyvern.tools.util;

public class Pair<T1, T2> {
    private T1 first;
    private T2 second;

    public Pair(T1 f, T2 s) {
        setFirst(f);
        setSecond(s);
    }

    public T1 getFirst() {
        return first;
    }

    public void setFirst(T1 first) {
        this.first = first;
    }

    public T2 getSecond() {
        return second;
    }

    public void setSecond(T2 second) {
        this.second = second;
    }

    public String toString() {
        return "<" + getFirst() + "," + getSecond() + ">";
    }
}
