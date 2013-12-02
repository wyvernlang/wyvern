package wyvern.tools.util;

public class Reference<T> {
	private T value;
	public Reference(T value) {
		this.value = value;
	}
	public Reference() {
		this.value = null;
	}

	public void set(T value) {
		this.value = value;
	}

	public T get() {
		return value;
	}
}
