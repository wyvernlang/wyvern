package wyvern.tools.parsing.parselang;

import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.logging.PrintCompilerLogHandler;
import edu.umn.cs.melt.copper.compiletime.skins.cup.CupSkinParser;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.*;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.visitors.CopperASTBeanVisitor;
import edu.umn.cs.melt.copper.main.CopperIOType;
import edu.umn.cs.melt.copper.main.ParserCompiler;
import edu.umn.cs.melt.copper.main.ParserCompilerParameters;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;
import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import org.apache.tools.ant.BuildException;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.parsing.ExtParser;
import wyvern.tools.parsing.ParseBuffer;
import wyvern.tools.typedAST.core.binding.Binding;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.declarations.ClassDeclaration;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.DefDeclaration;
import wyvern.tools.typedAST.core.declarations.ValDeclaration;
import wyvern.tools.typedAST.extensions.SpliceBindExn;
import wyvern.tools.typedAST.extensions.interop.java.Util;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Int;
import wyvern.tools.types.extensions.Str;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.LangUtil;
import wyvern.tools.util.Reference;

import javax.lang.model.element.Name;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CopperTSL implements ExtParser {

	private static final String PAIRED_OBJECT_NAME = "innerObj$wyv";

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

		Environment ntEnv = res.getGrammars().stream().map(res::getGrammar)
				.flatMap(grm -> grm.getElementsOfType(CopperElementType.NON_TERMINAL).stream().map(grm::getGrammarElement))
				.map(this::parseType).map(pair->(Pair<String, Type>)pair)
				.collect(() -> new Reference<Environment>(Environment.getEmptyEnvironment()),
						(env, elem) -> env.set(env.get().extend(new NameBindingImpl(elem.first(), elem.second()))),
						(a, b) -> a.set(a.get().extend(b.get()))).get();
		
		final Environment savedNtEnv = ntEnv;
		ntEnv = res.getGrammars().stream().map(res::getGrammar)
				.flatMap(grm -> grm.getElementsOfType(CopperElementType.TERMINAL).stream().map(grm::getGrammarElement))
				.map(this::parseType).map(pair -> (Pair<String, Type>) pair)
				.collect(() -> new Reference<Environment>(savedNtEnv),
						(env, elem) -> env.set(env.get().extend(new NameBindingImpl(elem.first(), elem.second()))),
						(a, b) -> a.set(a.get().extend(b.get()))).get();


		HashMap<String, TypedAST> toGen = new HashMap<>();
		HashMap<String, TypedAST> toGenDefs = new HashMap<>();
		Reference<Integer> methNum = new Reference<>(0);

		final Environment savedNtEnv2 = ntEnv;
		res.getGrammars().stream().map(res::getGrammar)
				.flatMap(grm->grm.getElementsOfType(CopperElementType.PRODUCTION).stream().map(grm::getGrammarElement).map(el->(Production)el))
				.map(prod->new Pair<Production, List<NameBinding>>(prod, CopperTSL.<Type,String,Optional<NameBinding>>
						zip(prod.getRhs().stream().map(cer->savedNtEnv2.lookup(cer.getName().toString()).getType()), prod.getRhsVarNames().stream(),
						(type, name) -> (name == null)?Optional.empty():Optional.of(new NameBindingImpl(name, type)))
						.<NameBinding>flatMap(o -> o.isPresent() ? Stream.of(o.get()) : Stream.empty())
						.collect(Collectors.<NameBinding>toList()))
				).forEach(updateCode(toGen,methNum));


		String wyvClassName = res.getClassName();
		String javaClassName = wyvClassName + "$java";
		res.setClassName(javaClassName);

		String pic = res.getParserInitCode();
		TypedAST parserInitAST = LangUtil.splice(new IParseBuffer(pic));
		String defNamePIA = "initGEN" + methNum.get();
		toGenDefs.put(defNamePIA, parserInitAST);
		methNum.set(methNum.get()+1);
		res.setParserInitCode(String.format("Util.invokeValueVarargs(%s, %s)", PAIRED_OBJECT_NAME, defNamePIA));

		String ppc = res.getPostParseCode();
		TypedAST postParseAST = LangUtil.splice(new IParseBuffer(ppc));
		String defNameP = "postGEN" + methNum.get();
		toGenDefs.put(defNameP, postParseAST);
		methNum.set(methNum.get() + 1);
		res.setPostParseCode(String.format("Util.invokeValueVarargs(%s, %s)", PAIRED_OBJECT_NAME, defNameP));

		String pcac = res.getParserClassAuxCode();
		TypedAST aux = new DeclSequence();
		if (!pcac.trim().equals(""))
			try {
				aux = (TypedAST)new WyvernDecls().parse(new StringReader(pcac), "Parser aux code");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		res.setParserClassAuxCode("Value "+PAIRED_OBJECT_NAME+";");

		AtomicInteger cdIdx = new AtomicInteger();
		TypedAST[] classDecls = new TypedAST[toGen.size() + toGenDefs.size()];
		toGen.entrySet().stream().forEach(entry->classDecls[cdIdx.getAndIncrement()]
				= new ValDeclaration(entry.getKey(), entry.getValue(), FileLocation.UNKNOWN));
		toGenDefs.entrySet().stream().forEach(entry->classDecls[cdIdx.getAndIncrement()]
				= new DefDeclaration(entry.getKey(), Unit.getInstance(), new LinkedList<>(), entry.getValue(), false));


		TypedAST pairedObj = new ClassDeclaration(wyvClassName, "", "", new DeclSequence(classDecls), FileLocation.UNKNOWN);

		ParserCompilerParameters pcp = new ParserCompilerParameters();

		ByteArrayOutputStream target = new ByteArrayOutputStream();
		pcp.setOutputStream(new PrintStream(target));
		pcp.setOutputType(CopperIOType.STREAM);

		try {
			ParserCompiler.compile(res, pcp);
		} catch (CopperException e) {
			throw new BuildException(e);
		}


		return null;
	}

	private Pair<String, Type> parseType(NonTerminal elem) {
		Type parsedType = null;
		try {
			parsedType = (Type)new TypeParser().parse(new StringReader(((NonTerminal) elem).getReturnType()), elem.getName() + " type");
			((NonTerminal) elem).setReturnType("Value");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return new Pair<String, Type>(elem.getName().toString(), parsedType);
	}
	private Pair<String, Type> parseType(Terminal elem) {
		Type parsedType = null;
		try {
			parsedType = (Type)new TypeParser().parse(new StringReader((elem).getReturnType()), elem.getName() + " type");
			elem.setReturnType("Value");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return new Pair<String, Type>(elem.getName().toString(), parsedType);
	}
	private Pair<String, Type> parseType(GrammarElement elem) {
		if (elem instanceof Terminal)
			this.parseType((Terminal)elem);
		if (elem instanceof NonTerminal)
			this.parseType((NonTerminal)elem);
		throw new RuntimeException();
	}

	private Consumer<Pair<Production, List<NameBinding>>> updateCode(HashMap<String, TypedAST> toGen, Reference<Integer> methNum) {
		return (Pair<Production, List<NameBinding>> inp) -> {
			Production prod = inp.first();
			List<NameBinding> bindings = inp.second();
			bindings.add(new NameBindingImpl("lexeme", Str.getInstance()));

			//Generate the new Wyvern method name
			String newName = prod.getName().toString() + "GEN" + methNum.get();
			methNum.set(methNum.get()+1);

			//Parse the input code
			TypedAST spliced = LangUtil.spliceBinding(new IParseBuffer(prod.getCode()), bindings);

			//Save it to the external dict
			toGen.put(newName, spliced);

			String args = bindings.stream().map(nb->(nb.getName().equals("lexeme"))?"new StringConstant(lexeme)":nb.getName()
				).reduce((a,b)->a+", "+b).get();
			//Code to invoke the equivalent function
			String newCode = "RESULT = Util.invokeValueVarargs("+PAIRED_OBJECT_NAME+", "+newName+", "+
					args;

			prod.setCode(newCode);
		};
	}

	//Via stackoverflow and the old Java zip
	private static<A, B, C> Stream<C> zip(Stream<? extends A> a,
										 Stream<? extends B> b,
										 BiFunction<? super A, ? super B, ? extends C> zipper) {
		Objects.requireNonNull(zipper);
		@SuppressWarnings("unchecked")
		Spliterator<A> aSpliterator = (Spliterator<A>) Objects.requireNonNull(a).spliterator();
		@SuppressWarnings("unchecked")
		Spliterator<B> bSpliterator = (Spliterator<B>) Objects.requireNonNull(b).spliterator();

		// Zipping looses DISTINCT and SORTED characteristics
		int both = aSpliterator.characteristics() & bSpliterator.characteristics() &
				~(Spliterator.DISTINCT | Spliterator.SORTED);
		int characteristics = both;

		long zipSize = ((characteristics & Spliterator.SIZED) != 0)
				? Math.min(aSpliterator.getExactSizeIfKnown(), bSpliterator.getExactSizeIfKnown())
				: -1;

		Iterator<A> aIterator = Spliterators.iterator(aSpliterator);
		Iterator<B> bIterator = Spliterators.iterator(bSpliterator);
		Iterator<C> cIterator = new Iterator<C>() {
			@Override
			public boolean hasNext() {
				return aIterator.hasNext() && bIterator.hasNext();
			}

			@Override
			public C next() {
				return zipper.apply(aIterator.next(), bIterator.next());
			}
		};

		Spliterator<C> split = Spliterators.spliterator(cIterator, zipSize, characteristics);
		return (a.isParallel() || b.isParallel())
				? StreamSupport.stream(split, true)
				: StreamSupport.stream(split, false);
	}

}
