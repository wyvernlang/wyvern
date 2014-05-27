package wyvern.tools.parsing.parselang;

import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.logging.PrintCompilerLogHandler;
import edu.umn.cs.melt.copper.compiletime.skins.cup.CupSkinParser;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.*;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.visitors.CopperASTBeanVisitor;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;
import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import wyvern.tools.parsing.ExtParser;
import wyvern.tools.parsing.ParseBuffer;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Int;
import wyvern.tools.util.LangUtil;

import javax.lang.model.element.Name;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CopperTSL implements ExtParser {

	private static class IParseBuffer extends ParseBuffer {
		IParseBuffer(String str) {
			super(str);
		}
	}

	@Override
	public TypedAST parse(ParseBuffer input) throws Exception {
		StringReader isr = new StringReader(input.getSrcString());
		ArrayList<edu.umn.cs.melt.copper.runtime.auxiliary.Pair<String,Reader>> inp = new ArrayList<>();
		inp.add(new Pair<String, Reader>("TSL grammar", isr));

		ParserBean res = CupSkinParser.parseGrammar(inp, new CompilerLogger(new PrintCompilerLogHandler(System.out)));
		GrammarProductionTyper gpt = new GrammarProductionTyper();
		res.acceptVisitor(gpt);
		CopperGrammarVisitor cgv = new CopperGrammarVisitor(gpt.getNonterminalTypes());
		res.acceptVisitor(cgv);
		LinkedList<wyvern.tools.util.Pair<String,TypedAST>> generatedMethods = cgv.getGenerateds();

		String wyvClassName = res.getClassName();
		String javaClassName = wyvClassName + "$java";
		res.setClassName(javaClassName);

		String pic = res.getParserInitCode();
		TypedAST parserInitAST = LangUtil.splice(new IParseBuffer(pic));


		String ppc = res.getPostParseCode();
		TypedAST postParseAST = LangUtil.splice(new IParseBuffer(ppc));

		String pc = res.getPreambleCode();
		TypedAST pcAST = LangUtil.splice(new IParseBuffer(pc));

		String pcac = res.getParserClassAuxCode();

		return null;
	}

	private static class GrammarProductionTyper implements CopperASTBeanVisitor<Object, Exception> {
		private Map<CopperElementReference, Type> nonterminalTypes = new HashMap<>();
		Optional<Grammar> currentGrammar = Optional.empty();

		public Map<CopperElementReference, Type> getNonterminalTypes() {
			return nonterminalTypes;
		}

		@Override
		public Object visitDisambiguationFunction(DisambiguationFunction disambiguationFunction) throws Exception {
			return null;
		}

		@Override
		public Object visitGrammar(Grammar grammar) throws Exception {
			currentGrammar = Optional.of(grammar);
			grammar.getGrammarElements().stream().map(name -> {
				try {
					return grammar.getGrammarElement(name).acceptVisitor(this);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
			return null;
		}

		@Override
		public Object visitExtensionGrammar(ExtensionGrammar extensionGrammar) throws Exception {
			return null;
		}

		@Override
		public Object visitNonTerminal(NonTerminal nonTerminal) throws Exception {
			return null;
		}

		@Override
		public Object visitParserAttribute(ParserAttribute parserAttribute) throws Exception {
			return null;
		}

		@Override
		public Object visitParserBean(ParserBean parserBean) throws Exception {
			parserBean.getGrammars().stream().map(cen -> {
				try {
					return parserBean.getGrammar(cen).acceptVisitor(this);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
			return null;
		}

		@Override
		public Object visitExtendedParserBean(ExtendedParserBean extendedParserBean) throws Exception {
			return null;
		}

		@Override
		public Object visitProduction(Production production) throws Exception {
			String code = production.getCode();
			String src = code.substring(0, code.lastIndexOf(":"));
			String type = code.substring(code.lastIndexOf(":")+1, code.length());

			Type asc = null;
			try {
				asc = (Type)new TypeParser().parse(new StringReader(src), "TSL type");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			nonterminalTypes.put(CopperElementReference.ref(currentGrammar.get().getName(), production.getName(), production.getLocation()), asc);
			return null;
		}

		@Override
		public Object visitTerminal(Terminal terminal) throws Exception {
			return null;
		}

		@Override
		public Object visitTerminalClass(TerminalClass terminalClass) throws Exception {
			return null;
		}

		@Override
		public Object visitOperatorClass(OperatorClass operatorClass) throws Exception {
			return null;
		}
	}

	private static class CopperGrammarVisitor implements CopperASTBeanVisitor<Object, Exception> {
		private LinkedList<wyvern.tools.util.Pair<String, TypedAST>> generateds = new LinkedList<>();
		private int genIdx = 0;
		private Map<CopperElementReference, Type> ntTypes;

		private LinkedList<wyvern.tools.util.Pair<String,TypedAST>> getGenerateds() {
			return generateds;
		}

		private String getNextName() {
			return "gen$"+genIdx++;
		}

		public CopperGrammarVisitor(Map<CopperElementReference,Type> ntTypes) {
			this.ntTypes = ntTypes;
		}

		@Override
		public Object visitDisambiguationFunction(DisambiguationFunction disambiguationFunction) throws RuntimeException {
			List<NameBinding> bindings = disambiguationFunction.getMembers().stream()
					.map(mem -> new NameBindingImpl(mem.getName().toString(), Int.getInstance())).collect(Collectors.toList());
			//TypedAST result = LangUtil.spliceBinding(new IParseBuffer(disambiguationFunction.getCode()), bindings);

			String name = getNextName();
			//generateds.add(new wyvern.tools.util.Pair<String, TypedAST>(name, result));
			String args = disambiguationFunction.getMembers().stream().map(mem->mem.getName().toString()).reduce("",(a,b)->a+", "+b);
			disambiguationFunction.setCode("RESULT = Util.doInvokeVarargs(pairedObj, \""+name+"\", "+args+");");
			return null;
		}

		@Override
		public Object visitGrammar(Grammar grammar) throws Exception {
			try {
				grammar.getGrammarElements().stream().map(name -> {
					try {
						return grammar.getGrammarElement(name).acceptVisitor(this);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				});
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			return null;
		}

		@Override
		public Object visitExtensionGrammar(ExtensionGrammar extensionGrammar) throws RuntimeException {
			//TODO
			return null;
		}

		@Override
		public Object visitNonTerminal(NonTerminal nonTerminal) throws RuntimeException {
			//Unneeded
			throw new RuntimeException();
		}

		@Override
		public Object visitParserAttribute(ParserAttribute parserAttribute) throws RuntimeException {
			throw new RuntimeException();
		}

		@Override
		public Object visitParserBean(ParserBean parserBean) throws RuntimeException {
			try {
			parserBean.getGrammars().stream().map(cen -> {
				try {
					return parserBean.getGrammar(cen).acceptVisitor(this);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			return null;
		}

		@Override
		public Object visitExtendedParserBean(ExtendedParserBean extendedParserBean) throws RuntimeException {
			//TODO
			return null;
		}

		@Override
		public Object visitProduction(Production production) throws RuntimeException {
			String code = production.getCode();
			String src = code.substring(0, code.lastIndexOf(":"));
			String type = code.substring(code.lastIndexOf(":")+1, code.length());

			Iterator<String> names = production.getRhsVarNames().iterator();

			Type[] types = production.getRhs().stream().filter(cer->names.hasNext()&&names.next() != null).map(ntTypes::get).toArray(Type[]::new);
			String[] iNames = production.getRhsVarNames().stream().filter(name->name!=null).toArray(String[]::new);

			List<NameBinding> bindings = new LinkedList<>();
			for (int i = 0; i < types.length; i++) {
				bindings.add(new NameBindingImpl(iNames[i], types[i]));
			}

			//TypedAST result = LangUtil.spliceBinding(new IParseBuffer(src), bindings);

			String name = getNextName();
			//generateds.add(new wyvern.tools.util.Pair<>(name, result));
			String args = Arrays.asList(iNames).stream().map(nme->"(Value)"+nme).reduce("", (a, b) -> a + ", " + b);
			production.setCode("RESULT = Util.doInvokeVarargs(pairedObj, \"" + name + "\", " + args + ");");
			return null;
		}

		@Override
		public Object visitTerminal(Terminal terminal) throws RuntimeException {
			throw new RuntimeException();
		}

		@Override
		public Object visitTerminalClass(TerminalClass terminalClass) throws RuntimeException {
			throw new RuntimeException();
		}

		@Override
		public Object visitOperatorClass(OperatorClass operatorClass) throws RuntimeException {
			throw new RuntimeException();
		}
	}
}
