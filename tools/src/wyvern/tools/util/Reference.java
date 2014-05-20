package wyvern.tools.util;

import wyvern.tools.typedAST.core.binding.LateBinder;

import java.util.function.Supplier;

public class Reference<T> {
	private Supplier<T> src;
	private T value;
	public Reference(T value) {
		this.value = value;
		this.src = () -> value;
	}
	public Reference(Supplier<T> src) {
		this.src = src;
	}
	public Reference() {
		this.src = () -> value;
	}

	public void set(T value) {
		this.src = () -> value;
	}

	public T get() {
		return src.get();
	}

	public LateBinder<T> getBinder() {
		return new LateBinder<T>() {
			@Override
			public T get() {
				return src.get();
			}
		};
	}
}
