package wyvern.tools.util;

import java.util.LinkedList;

public class ListConstructor<T> {
	private final T el;
	private final ListConstructor<T> next;

	public ListConstructor(T el) {
		this.el = el;
		next = null;
	}

	public ListConstructor(T el, Object next) {
		this.el = el;
		this.next = (ListConstructor<T>)next;
	}

	public LinkedList<T> toList() {
		if (next == null) {
			LinkedList<T> res = new LinkedList<>();
			res.add(el);
			return res;
		}
		LinkedList<T> pnt = next.toList();
		pnt.addFirst(el);
		return pnt;
	}
}
