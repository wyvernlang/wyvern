package wyvern.tools.util;

import wyvern.tools.typedAST.core.binding.LateBinder;

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

	public LateBinder<T> getBinder() {
		return new LateBinder<T>() {
			@Override
			public T get() {
				return value;
			}
		};
	}
}
