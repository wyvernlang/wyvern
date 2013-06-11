package wyvern.DSL.html.parsing;

import java.util.HashMap;

import wyvern.DSL.html.typedAST.AttrAST;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.parsing.BodyParser;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.rawAST.LineSequence;
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
		private boolean empty = false;
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
		public ParsingPrefs(String tag, boolean injectEnv, boolean empty) {
			this.tag = tag;
			this.injectEnv = injectEnv;
			this.empty  = empty;
		}
	}
	
	private ParsingPrefs prefs;
	
	public HtmlTagParser(ParsingPrefs prefs) {
		this.prefs = prefs;
	}
	

	@Override
	public TypedAST parse(TypedAST first,
						  Pair<ExpressionSequence, Environment> ctx) {
		if (prefs.empty)
			return new StringConstant(String.format("</%s>\n",prefs.tag));
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
			htmlBodyEnv = htmlBodyEnv.extend(new KeywordNameBinding("br", new Keyword(new HtmlTagParser(new ParsingPrefs("br", false, true)))));
			htmlBodyEnv = htmlBodyEnv.extend(new KeywordNameBinding("button", new Keyword(new HtmlTagParser(new ParsingPrefs("button")))));
			htmlBodyEnv = htmlBodyEnv.extend(new KeywordNameBinding("form", new Keyword(new HtmlTagParser(new ParsingPrefs("form")))));
			htmlBodyEnv = htmlBodyEnv.extend(new KeywordNameBinding("input", new Keyword(new HtmlTagParser(new ParsingPrefs("input")))));
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
					attrs = ((AttrAST) elem).getVal(attrs);
					continue;
				} 
				
				if (insert == null) {
					insert = elem;
					continue;
				}
				insert = (new Invocation(insert,"+",elem,elem.getLocation()));
			}
		} else {
			if (result instanceof AttrAST) {
				attrs = ((AttrAST) result).getVal(attrs);
				insert = new StringConstant("");
			} else {
				insert = result;
			}
		}
		
		if (attrs == null)
			return reduce2(concat(insert,first.getLocation(),true));
		else
			return reduce2(concat(insert,attrs,first.getLocation(),true));
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
		return (new Invocation(
						(new Invocation(
								(new Invocation(
										new StringConstant("<"+prefs.tag),
										"+",
										attrs,
										location)),
								"+",
								new StringConstant(">"+((newline)?"\n":"")),
								location)),
						"+",
				(new Invocation(
						value,
						"+",
						new StringConstant("\n</"+prefs.tag+">\n"),
						location)),
				location));
	}
	
	private TypedAST concat(TypedAST value, FileLocation location, boolean newline) {
		return (new Invocation(new StringConstant("<"+prefs.tag+">"+((newline)?"\n":"")),"+",
				(new Invocation(value,"+",new StringConstant("</"+prefs.tag+">\n"),location)),location));
	}
	
	public static TypedAST enquote(TypedAST toQuote) {
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
	
	private TypedAST join(TypedAST left, TypedAST right) {
		if (left instanceof StringConstant && right instanceof StringConstant) {
			return new StringConstant(((StringConstant)left).getValue() + ((StringConstant)right).getValue());
		}
		return new Invocation(left,"+",right,FileLocation.UNKNOWN);
	}
	
	private TypedAST appendToRightmost(Invocation tree, TypedAST toAppend) {
		if (tree.getArgument() instanceof Invocation) {
			return join(tree.getReceiver(), appendToRightmost((Invocation)tree.getArgument(), toAppend));
		} else {
			return join(tree.getReceiver(), join(tree.getArgument(),toAppend));
		}
	}
	
	private TypedAST appendToLeftmost(Invocation tree, TypedAST toAppend) {
		if (tree.getReceiver() instanceof Invocation) {
			return join(appendToLeftmost((Invocation)tree.getReceiver(), toAppend), tree.getArgument());
		} else {
			return join(join(toAppend,tree.getReceiver()), tree.getArgument());
		}
	}
	
	private Pair<TypedAST,TypedAST> getAndRemoveLeftmost(Invocation tree) {
		if (tree.getReceiver() instanceof Invocation) {
			Pair<TypedAST,TypedAST> ret = getAndRemoveLeftmost((Invocation)tree.getReceiver());
			if (ret.first == null) {
				return new Pair<TypedAST,TypedAST>(tree.getArgument(), ret.second);
			} else {
				return new Pair<TypedAST,TypedAST>(join(ret.first,tree.getArgument()),ret.second);
			}
		} else {
			return new Pair<TypedAST,TypedAST>(tree.getArgument(), tree.getReceiver());
		}
	}
	
	private TypedAST reduce2(TypedAST input) {
		if (input instanceof Invocation) {
			Invocation inv = (Invocation)input;
			TypedAST left = reduce2(((Invocation) input).getReceiver()),
					right = reduce2(((Invocation)input).getArgument());
			
			if (left instanceof StringConstant && right instanceof StringConstant) {
				return new StringConstant(((StringConstant)left).getValue()+((StringConstant)right).getValue());
			}
			
			if (left instanceof Invocation && right instanceof StringConstant) {
				return appendToRightmost((Invocation)left, right);
			}
			
			if (left instanceof StringConstant && right instanceof Invocation) {
				return appendToLeftmost((Invocation)right, left);
			}
			
			if (left instanceof Invocation && right instanceof Invocation) {
				//return appendToLeftmost((Invocation)right, ((StringConstant)left).getValue());
			}
		}
		return input;
	}

}
