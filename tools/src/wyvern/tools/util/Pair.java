package wyvern.tools.util;

public class Pair<T1, T2> {
	public Pair(T1 f, T2 s) {
		first = f;
		second = s;
	}
	
	public T1 first;
	public T2 second;
	
	public String toString() {
		return "<" + first + "," + second + ">";
	}

	public int hashCode() {
		return 31*(31 + first.hashCode()) + second.hashCode();
	}

	public boolean equals(Object other) {
		return other instanceof Pair &&
				((Pair) other).first.equals(this.first) &&
				((Pair) other).second.equals(this.second);
	}
}
