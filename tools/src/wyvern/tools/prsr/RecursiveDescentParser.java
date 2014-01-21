package wyvern.tools.prsr;

import wyvern.tools.lex.ILexStream;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.util.Reference;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Ben Chung on 1/20/14.
 */
public class RecursiveDescentParser {

	private List<ParserStage> stages;
	private Map<ParserStage, ParserStage> stageMap;

	public RecursiveDescentParser(List<ParserStage> stages) {
		this.stages = stages;

		stageMap = new HashMap<>();
		ParserStage last = stages.get(0);
		for (ParserStage s : stages) {
			if (s == last)
				continue;
			stageMap.put(last, s);
			last = s;
		}

	}

	private ParseHandle mapper(ParserStage fn) {
		ParserStage next = stageMap.get(fn);
		return ParseHandle.create(
				(ls) -> next.parse(ls, next, mapper(next)),
				(str,to) -> to.parse(str, to, mapper(stageMap.get(to)))
		);
	}

	public TypedAST parse(ILexStream stream) {
		Iterator<ParserStage> iterator = stages.iterator();
		ParserStage current = iterator.next();
		return current.parse(stream, current, mapper(current));
	}
}
