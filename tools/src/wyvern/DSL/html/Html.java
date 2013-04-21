package wyvern.DSL.html;

import wyvern.DSL.html.parsing.HtmlTagParser;
import wyvern.tools.parsing.extensions.TypeParser;
import wyvern.tools.typedAST.core.Keyword;
import wyvern.tools.typedAST.core.binding.KeywordNameBinding;
import wyvern.tools.types.Environment;

public class Html {
	public static Environment extend(Environment input) {
		Environment env = input;
		env = env.extend(new KeywordNameBinding("html", new Keyword(new HtmlTagParser(new HtmlTagParser.ParsingPrefs("html",true)))));
		return env;
	}
}
