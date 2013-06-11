package wyvern.DSL.html;

import wyvern.DSL.DSL;
import wyvern.DSL.html.parsing.HtmlTagParser;
import wyvern.tools.typedAST.core.Keyword;
import wyvern.tools.typedAST.core.binding.KeywordNameBinding;
import wyvern.tools.types.Environment;

public class Html implements DSL {
	public static Environment extend(Environment input) {
		Environment env = input;
		env = env.extend(new KeywordNameBinding("html", new Keyword(new HtmlTagParser(new HtmlTagParser.ParsingPrefs("html",true)))));
		return env;
	}

	@Override
	public Environment addToEnv(Environment in) {
		return extend(in);
	}
}
