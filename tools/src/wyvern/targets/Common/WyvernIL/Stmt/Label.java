package wyvern.targets.Common.WyvernIL.Stmt;

import wyvern.targets.Common.WyvernIL.visitor.StatementVisitor;

import java.util.concurrent.atomic.AtomicInteger;

public class Label implements Statement {
	public static void flushIdx() { idx.set(0); }
	private static AtomicInteger idx = new AtomicInteger(0);
	private int num = idx.getAndIncrement();
	@Override
	public <R> R accept(StatementVisitor<R> visitor) {
		return visitor.visit(this);
	}

	public int getIdx() {
		return num;
	}

	@Override
	public String toString() {
		return "label "+num;
	}
}
