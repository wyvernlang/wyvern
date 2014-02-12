package wyvern.tools.prsr.util;

import wyvern.tools.lex.ILexStream;
import wyvern.tools.lex.Token;

import java.io.InputStream;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by Ben Chung on 1/24/14.
 */
public class TransactionalStream implements ILexStream {
	private ILexStream source;
	private Stack<Integer> idxes = new Stack<>();
	private Stack<Integer> recidxes = new Stack<>();
	private Consumer<ILexStream> del;

	public static TransactionalStream transaction(ILexStream source) {
		if (source instanceof TransactionalStream) {
			TransactionalStream ts = (TransactionalStream) source;
			ts.newLevel();
			return ts;
		}
		return new TransactionalStream(source);
	}

	private TransactionalStream(ILexStream source) {
		this.source = source;
		idxes.push(0);
	}

	@Override
	public Token next() {
		Integer current = idxes.pop();
		idxes.push(current + 1);
		return source.lookAhead(current);
	}

	@Override
	public Token peek() {
		return source.lookAhead(idxes.peek());
	}

	@Override
	public Token lookAhead(int n) {
		return source.lookAhead(idxes.peek() + n);
	}

	@Override
	public ILexStream dslBlock(Token.Kind start, Token.Kind end) {
		return source.dslBlock(start, end);
	}

	@Override
	public InputStream asRawStream() {
		return source.asRawStream();
	}

	private Consumer<ILexStream> stepper(int steps) {
		return stream -> {
			for (int i = 0; i < steps; i++)
				stream.next();
		};
	}

	public Consumer<ILexStream> getDelta() {
		return del;
	}

	public void commit() {
		if (idxes.size() == 1) {
			int step = idxes.pop();
			for (; step > 0; step--) {
				source.next();
			}
			del = stepper(computeSteps(step));
			return;
		}
		int newIdx = idxes.pop();
		idxes.pop();
		idxes.push(newIdx);
		String stackv = idxes.stream().map(n -> source.lookAhead(n).location.globalChar).reduce("", (l, r) -> ((l.isEmpty()) ? "" : l + ",") + r, (l, r) -> l + "," + r);
		System.out.println(stackv);
		del = stepper(computeSteps(newIdx));
	}

	private int computeSteps(int step) {
		if (recidxes.size() > 0)
			return step-recidxes.pop();
		return step;
	}

	public void rollback() {
		recidxes.pop();
		idxes.pop();
	}

	private void newLevel() {
		recidxes.push(idxes.peek());
		idxes.push(idxes.peek());
	}
}
