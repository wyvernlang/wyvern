package wyvern.tools.util;

import java.util.function.Function;
import java.util.function.Supplier;

import wyvern.tools.typedAST.core.binding.LateBinder;

public class Reference<T> {
	private final String stackTrace = "";
	private Supplier<T> src;
	private T value;

	public Reference(T value) {
		this.value = value;
		this.src = () -> value;
		//stackTrace = Arrays.asList(new Exception().getStackTrace()).stream().reduce("", (a,b)->a+"\n"+b, (a,b)->a + "\n" + b);
	}
	public Reference(Supplier<T> src) {
		this.src = src;
		//stackTrace = Arrays.asList(new Exception().getStackTrace()).stream().reduce("", (a,b)->a+"\n"+b, (a,b)->a + "\n" + b);
	}
	public Reference() {
		this.src = () -> value;
		//stackTrace = Arrays.asList(new Exception().getStackTrace()).stream().reduce("", (a,b)->a+"\n"+b, (a,b)->a + "\n" + b);
	}

	public void set(T value) {
		this.src = () -> value;
	}
	public void setSrc(Function<Supplier<T>, Supplier<T>> srcGen) { this.src = srcGen.apply(src); }

	public <V> Reference<V> map(Function<T,V> mapper) {
		return new Reference<V>() {
			@Override
			public V get() {
				return mapper.apply(Reference.this.get());
			}
		};
	}

	public T get() {
		return src.get();
	}

	public LateBinder<T> getBinder() {
		return src::get;
	}
}
