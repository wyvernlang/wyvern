package wyvern.DSL.html.parsing;

import java.util.HashMap;
import java.util.Map.Entry;

import wyvern.DSL.html.typedAST.AttrAST;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.parsing.BodyParser;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.rawAST.LineSequence;
import wyvern.tools.rawAST.Parenthesis;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.rawAST.Symbol;
import wyvern.tools.typedAST.core.Invocation;
import wyvern.tools.typedAST.core.Keyword;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.binding.KeywordNameBinding;
import wyvern.tools.typedAST.core.values.StringConstant;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.util.Pair;

public class HtmlTagParser implements LineParser {
	public static class ParsingPrefs {
		private boolean injectEnv = false;
		private String tag = null;
		public ParsingPrefs() {}
		public ParsingPrefs(String tag) {
			this.tag = tag;
		}
		public ParsingPrefs(boolean injectEnv) {
			this.injectEnv = injectEnv;
		}
		public ParsingPrefs(String tag, boolean injectEnv) {
			this.tag = tag;
			this.injectEnv = injectEnv;
		}
	}
	
	private ParsingPrefs prefs;
	
	public HtmlTagParser(ParsingPrefs prefs) {
		this.prefs = prefs;
	}
	

	@Override
	public TypedAST parse(TypedAST first,
			Pair<ExpressionSequence, Environment> ctx) {
		if (ctx.first == null)
			return new StringConstant(String.format("<%s>\n</%s>\n",prefs.tag, prefs.tag));
		
		if (!(ctx.first.getFirst() instanceof LineSequence)) {
			TypedAST ir = BodyParser.getInstance().visit(ctx.first, ctx.second);
			ctx.first = ctx.first.getRest();
			return concat(ir, first.getLocation(),false);
		}
		
		LineSequence lines = ParseUtils.extractLines(ctx);
		
		Environment htmlBodyEnv;
		if (prefs.injectEnv) {
			htmlBodyEnv = ctx.second;
			htmlBodyEnv = htmlBodyEnv.extend(new KeywordNameBinding("body", new Keyword(new HtmlTagParser(new ParsingPrefs("body")))));
			htmlBodyEnv = htmlBodyEnv.extend(new KeywordNameBinding("head", new Keyword(new HtmlTagParser(new ParsingPrefs("head")))));
			htmlBodyEnv = htmlBodyEnv.extend(new KeywordNameBinding("title", new Keyword(new HtmlTagParser(new ParsingPrefs("title")))));
			htmlBodyEnv = htmlBodyEnv.extend(new KeywordNameBinding("div", new Keyword(new HtmlTagParser(new ParsingPrefs("div")))));
			htmlBodyEnv = htmlBodyEnv.extend(new KeywordNameBinding("attrs", new Keyword(new AttributeParser())));
		} else {
			htmlBodyEnv = ctx.second;
		}
		
		TypedAST result = BodyParser.getInstance().visit(lines, htmlBodyEnv);

		TypedAST insert = null;
		TypedAST attrs = null;
		if (result instanceof Sequence) {
			for (TypedAST elem : (Sequence)result) {
				
				if (elem instanceof AttrAST) {
					HashMap<String,TypedAST> vars = ((AttrAST) elem).getAttrs();
					for (Entry<String, TypedAST> elems  : vars.entrySet()) {
						attrs = reduce(new Invocation(
								(attrs==null)?new StringConstant(""):attrs, 
								"+", 
								reduce(new Invocation(
										new StringConstant(" "+elems.getKey()+"="),
										"+",
										enquote(elems.getValue()),
										elems.getValue().getLocation())),
										elems.getValue().getLocation()
						));
					}
					continue;
				} 
				
				if (insert == null) {
					insert = elem;
					continue;
				}
				insert = reduce(new Invocation(insert,"+",elem,elem.getLocation()));
			}
		} else {
			insert = result;
		}
		
		if (attrs == null)
			return concat(insert,first.getLocation(),true);
		else
			return concat(insert,attrs,first.getLocation(),true);
	}
	
	private HashMap<String,TypedAST> parseAttrs(Pair<ExpressionSequence, Environment> ctx) {
		HashMap<String,TypedAST> output = new HashMap<String,TypedAST>();
		
		while (ctx.first != null) {
			Symbol name = ParseUtils.parseSymbol(ctx);
			ParseUtils.parseSymbol("=", ctx);
			TypedAST value = ParseUtils.parseExpr(ctx);
			output.put(name.name, value);
			if (ParseUtils.checkFirst(",", ctx))
				ParseUtils.parseSymbol(",",ctx);
		}
		
		return output;
	}
	
	private TypedAST concat(TypedAST value, TypedAST attrs, FileLocation location, boolean newline) {
		return reduce(new Invocation(
						reduce(new Invocation(
								reduce(new Invocation(
										new StringConstant("<"+prefs.tag),
										"+",
										attrs,
										location)),
								"+",
								new StringConstant(">"+((newline)?"\n":"")),
								location)),
						"+",
				reduce(new Invocation(
						value,
						"+",
						new StringConstant("\n</"+prefs.tag+">\n"),
						location)),
				location));
	}
	
	private TypedAST concat(TypedAST value, FileLocation location, boolean newline) {
		return reduce(new Invocation(new StringConstant("<"+prefs.tag+">"+((newline)?"\n":"")),"+",
				reduce(new Invocation(value,"+",new StringConstant("</"+prefs.tag+">\n"),location)),location));
	}
	
	private TypedAST enquote(TypedAST toQuote) {
		return new Invocation(
				new StringConstant("\""),
				"+",
				new Invocation(
						toQuote,
						"+",
						new StringConstant("\""), 
						toQuote.getLocation()),
				toQuote.getLocation());
	}
	
	private TypedAST reduce(Invocation invocation) {
		if ((invocation.getReceiver() instanceof StringConstant) 
				&& (invocation.getArgument() instanceof StringConstant)) {
			String receiver = ((StringConstant)invocation.getReceiver()).getValue();
			String argument = ((StringConstant)invocation.getArgument()).getValue();
			return new StringConstant(receiver+argument);
		} else if ((invocation.getReceiver() instanceof StringConstant) 
				&& (invocation.getArgument() instanceof Invocation)
				&& ((Invocation)invocation.getArgument()).getReceiver() instanceof StringConstant) {
			String receiver = ((StringConstant)invocation.getReceiver()).getValue();
			String argument = ((StringConstant)((Invocation)invocation.getArgument()).getReceiver()).getValue();
			return reduce(new Invocation(new StringConstant(receiver+argument),"+",
					((Invocation)invocation.getArgument()).getArgument(),
					invocation.getLocation()));
		} else if ((invocation.getReceiver() instanceof Invocation) 
				&& (invocation.getArgument() instanceof StringConstant)
				&& ((Invocation)invocation.getReceiver()).getArgument() instanceof StringConstant) {
			String a = ((StringConstant)((Invocation)invocation.getReceiver()).getArgument()).getValue();
			String b = ((StringConstant)invocation.getArgument()).getValue();
			return reduce(new Invocation(((Invocation)invocation.getReceiver()).getReceiver(),
					"+",
					new StringConstant(a+b),
					invocation.getLocation()));
		}
		return invocation;
	}

}
