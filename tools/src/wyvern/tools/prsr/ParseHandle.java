package wyvern.tools.prsr;

import wyvern.tools.lex.ILexStream;
import wyvern.tools.typedAST.interfaces.TypedAST;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by Ben Chung on 1/20/14.
 */
public interface ParseHandle {
	TypedAST apply(ILexStream stream);
	TypedAST back(ILexStream stream, ParserStage jt);

	public static ParseHandle create(Function<ILexStream, TypedAST> applier,
									 BiFunction<ILexStream, ParserStage, TypedAST> back) {
		return new ParseHandle() {
			@Override
			public TypedAST apply(ILexStream stream) {
				return applier.apply(stream);
			}

			@Override
			public TypedAST back(ILexStream stream, ParserStage jt) {
				return back.apply(stream, jt);
			}
		};
	}
}
