package wyvern.tools.parsing.parselang;

import com.sun.org.apache.xpath.internal.compiler.OpCodes;
import edu.umn.cs.melt.copper.compiletime.logging.CompilerLogger;
import edu.umn.cs.melt.copper.compiletime.logging.PrintCompilerLogHandler;
import edu.umn.cs.melt.copper.compiletime.skins.cup.CupSkinParser;
import edu.umn.cs.melt.copper.compiletime.spec.grammarbeans.*;
import edu.umn.cs.melt.copper.main.CopperIOType;
import edu.umn.cs.melt.copper.main.ParserCompiler;
import edu.umn.cs.melt.copper.main.ParserCompilerParameters;
import edu.umn.cs.melt.copper.runtime.auxiliary.Pair;
import edu.umn.cs.melt.copper.runtime.engines.single.SingleDFAEngine;
import edu.umn.cs.melt.copper.runtime.logging.CopperException;
import org.apache.tools.ant.BuildException;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.CheckMethodAdapter;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.parsing.ExtParser;
import wyvern.tools.parsing.ParseBuffer;
import wyvern.tools.parsing.parselang.java.StoringClassLoader;
import wyvern.tools.parsing.parselang.java.StoringFileManager;
import wyvern.tools.parsing.parselang.java.StringFileObject;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.declarations.ClassDeclaration;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.DefDeclaration;
import wyvern.tools.typedAST.core.declarations.ValDeclaration;
import wyvern.tools.typedAST.core.expressions.*;
import wyvern.tools.typedAST.core.values.Obj;
import wyvern.tools.typedAST.core.values.StringConstant;
import wyvern.tools.typedAST.core.values.TupleValue;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.extensions.ExternalFunction;
import wyvern.tools.typedAST.extensions.SpliceBindExn;
import wyvern.tools.typedAST.extensions.interop.java.Util;
import wyvern.tools.typedAST.extensions.interop.java.typedAST.JavaClassDecl;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.UnresolvedType;
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.types.extensions.Str;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.LangUtil;
import wyvern.tools.util.Reference;

import javax.tools.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CopperTSL implements ExtParser {
	private int foo;
	public CopperTSL(int k) {
		foo = 0;
	}
	public CopperTSL() {

	}

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


		HashMap<String, Pair<Type,SpliceBindExn>> toGen = new HashMap<>();
		HashMap<String, TypedAST> toGenDefs = new HashMap<>();
		Reference<Integer> methNum = new Reference<>(0);

		final Environment savedNtEnv2 = ntEnv;
		res.getGrammars().stream().map(res::getGrammar)
				.flatMap(grm->grm.getElementsOfType(CopperElementType.PRODUCTION).stream().map(grm::getGrammarElement).<Production>map(el->(Production)el))
				.<Pair<Production, List<NameBinding>>>map(prod->new Pair<Production, List<NameBinding>>(prod, CopperTSL.<Type,String,Optional<NameBinding>>
						zip(prod.getRhs().stream().map(cer->savedNtEnv2.lookup(cer.getName().toString()).getType()), prod.getRhsVarNames().stream(),
						(type, name) -> (name == null)?Optional.empty():Optional.of(new NameBindingImpl(name, type)))
						.<NameBinding>flatMap(o -> o.isPresent() ? Stream.of(o.get()) : Stream.empty())
						.collect(Collectors.<NameBinding>toList()))
				).forEach(updateCode(toGen,ntEnv,methNum, res.getClassName()));

		res.getGrammars().stream().map(res::getGrammar)
				.flatMap(grm->grm.getElementsOfType(CopperElementType.TERMINAL).stream().map(grm::getGrammarElement)
						.<Terminal>map(el->(Terminal)el)).forEach(this.updateTerminalCode(toGen,ntEnv,methNum, res.getClassName()));


		String wyvClassName = res.getClassName();
		String javaClassName = wyvClassName + "$java";
		res.setClassName(javaClassName);

		String pic = res.getParserInitCode();
		TypedAST parserInitAST;
		if (pic == null)
			parserInitAST = new Sequence();
		else
			parserInitAST = LangUtil.splice(new IParseBuffer(pic));
		String defNamePIA = "initGEN" + methNum.get();
		toGenDefs.put(defNamePIA, parserInitAST);
		methNum.set(methNum.get()+1);
		res.setParserInitCode(String.format("Util.invokeValueVarargs(%s, \"%s\");\n", PAIRED_OBJECT_NAME, defNamePIA));

		String ppc = res.getPostParseCode();
		TypedAST postParseAST;
		if (ppc == null)
			postParseAST = new Sequence();
		else
			postParseAST = LangUtil.splice(new IParseBuffer(ppc));
		String defNameP = "postGEN" + methNum.get();
		toGenDefs.put(defNameP, postParseAST);
		methNum.set(methNum.get() + 1);
		res.setPostParseCode(String.format("Util.invokeValueVarargs(%s, \"%s\");\n", PAIRED_OBJECT_NAME, defNameP));

		res.setPreambleCode("import wyvern.tools.typedAST.interfaces.Value;\n" +
				"import wyvern.tools.typedAST.core.values.StringConstant;\n" +
				"import wyvern.tools.typedAST.extensions.interop.java.Util;\n" +
				"");

		res.setParserClassAuxCode("Value "+PAIRED_OBJECT_NAME+" = null;");


		AtomicInteger cdIdx = new AtomicInteger();
		TypedAST[] classDecls = new TypedAST[toGen.size() + toGenDefs.size() + 1];
		FileLocation unkLoc = FileLocation.UNKNOWN;
		toGen.entrySet().stream().forEach(entry->classDecls[cdIdx.getAndIncrement()]
				= new ValDeclaration(entry.getKey(), DefDeclaration.getMethodType(entry.getValue().second().getArgBindings(), entry.getValue().first()), entry.getValue().second(), unkLoc));

		toGenDefs.entrySet().stream().forEach(entry->classDecls[cdIdx.getAndIncrement()]
				= new DefDeclaration(entry.getKey(), new Arrow(Unit.getInstance(), Unit.getInstance()), new LinkedList<>(), entry.getValue(), false));

		classDecls[cdIdx.getAndIncrement()] = new DefDeclaration("create", new Arrow(Unit.getInstance(),
				new UnresolvedType(wyvClassName)),
				Arrays.asList(),
				new New(new DeclSequence(), unkLoc), true);


		ArrayList<TypedAST> pairedObjDecls = new ArrayList<>();
		pairedObjDecls.addAll(Arrays.asList(classDecls));
		TypedAST pairedObj = new ClassDeclaration(wyvClassName, "", "", new DeclSequence(pairedObjDecls), unkLoc);

		ParserCompilerParameters pcp = new ParserCompilerParameters();

		ByteArrayOutputStream target = new ByteArrayOutputStream();
		pcp.setOutputStream(new PrintStream(target));
		pcp.setOutputType(CopperIOType.STREAM);

		try {
			ParserCompiler.compile(res, pcp);
		} catch (CopperException e) {
			throw new BuildException(e);
		}

		JavaCompiler jc = javax.tools.ToolProvider.getSystemJavaCompiler();

		List<StringFileObject> compilationUnits = Arrays.asList(new StringFileObject(javaClassName, target.toString()));
		StoringClassLoader loader = new StoringClassLoader(this.getClass().getClassLoader());
		StoringFileManager sfm = new StoringFileManager(jc.getStandardFileManager(null, null, null),
				loader);

		StringFileObject sfo = new StringFileObject(javaClassName, target.toString());
		sfm.putFileForInput(StandardLocation.SOURCE_PATH, "", javaClassName, sfo);
		JavaCompiler.CompilationTask ct = jc.getTask(null, sfm, null, null, null, Arrays.asList(sfo));

		if (!ct.call())
			throw new RuntimeException();

		loader.applyTransformer(name->name.equals(javaClassName), cw -> new ClassVisitor(Opcodes.ASM5, new CheckClassAdapter(cw)) {
			@Override
			public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
				if (!name.equals("<init>"))
					return super.visitMethod(access, name, desc, signature, exceptions);


				String ndesc = org.objectweb.asm.Type.getMethodDescriptor(org.objectweb.asm.Type.VOID_TYPE,
						org.objectweb.asm.Type.getType(Value.class));
				org.objectweb.asm.Type thisType = org.objectweb.asm.Type.getType("L" + javaClassName + ";");

				MethodVisitor res = new CheckMethodAdapter(super.visitMethod(access, name, ndesc, null, exceptions));
				GeneratorAdapter generator = new GeneratorAdapter(
						res,
						Opcodes.ASM5,
						"<init>",
						ndesc);
				generator.visitCode();
				generator.loadThis();
				generator.invokeConstructor(org.objectweb.asm.Type.getType(SingleDFAEngine.class), Method.getMethod("void <init>()"));
				generator.loadThis();
				generator.loadArg(0);
				generator.putField(thisType, PAIRED_OBJECT_NAME,
						org.objectweb.asm.Type.getType(Value.class));
				generator.returnValue();
				generator.visitMaxs(2, 2);
				generator.visitEnd();

				return new MethodVisitor(Opcodes.ASM5) {};
			}
		});

		Class javaClass = sfm.getClassLoader().loadClass(javaClassName);

		JavaClassDecl jcd = Util.javaToWyvDecl(javaClass);

		Type parseBufferType = Util.javaToWyvType(ParseBuffer.class);


		Type javaClassType = Util.javaToWyvType(javaClass);
		TypedAST bufGet = new Application(
				new Invocation(new Variable(new NameBindingImpl("buf", null), unkLoc), "getSrcString", null, unkLoc),
				UnitVal.getInstance(unkLoc),
				unkLoc);
		ClassType emptyType =
				new ClassType(new Reference<>(Environment.getEmptyEnvironment()), new Reference<>(Environment.getEmptyEnvironment()), new LinkedList<>(), "empty");
		TypedAST javaObjInit = new Application(new ExternalFunction(new Arrow(emptyType, Util.javaToWyvType(Value.class)), (env,arg)->Util.toWyvObj(arg)),
				new Application(
						new Invocation(new Variable(new NameBindingImpl(wyvClassName, null), unkLoc), "create", null, unkLoc),
						UnitVal.getInstance(unkLoc), unkLoc), unkLoc);
		TypedAST body = new Application(new ExternalFunction(new Arrow(Util.javaToWyvType(Object.class), Util.javaToWyvType(TypedAST.class)),
					(env,arg)-> arg),
				new Application(new Invocation(new Application(
					new Invocation(new Variable(new NameBindingImpl(javaClassName, null), unkLoc), "create", null, unkLoc),
					javaObjInit, unkLoc), "parse", null, unkLoc),
					new TupleObject(new TypedAST[] {bufGet, new StringConstant("TSL code")}), unkLoc), unkLoc);

		DefDeclaration parseDef =
				new DefDeclaration("parse",
						new Arrow(parseBufferType, Util.javaToWyvType(TypedAST.class)),
						Arrays.asList(new NameBindingImpl("buf", parseBufferType)),
						body,
						false);
		return new New(new DeclSequence(Arrays.asList(pairedObj, jcd, parseDef)), unkLoc);
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
			return this.parseType((Terminal)elem);
		if (elem instanceof NonTerminal)
			return this.parseType((NonTerminal)elem);
		throw new RuntimeException();
	}

	private Consumer<Terminal> updateTerminalCode(HashMap<String, Pair<Type,SpliceBindExn>> toGen, Environment lhsEnv, Reference<Integer> methNum, String thisTypeName) {
		return (term) -> {
			String oCode = term.getCode();

			SpliceBindExn spliced = LangUtil.spliceBinding(new IParseBuffer(oCode), Arrays.asList(new NameBinding[] {
					new NameBindingImpl("lexeme", Str.getInstance())}));

			String newName = term.getName() + "GEN" + methNum.get();
			methNum.set(methNum.get() + 1);

			Type resType = lhsEnv.lookup(term.getName().toString()).getType();

			toGen.put(newName, new Pair<>(resType, spliced));

			String newCode = String.format("RESULT = Util.invokeValueVarargs(%s, \"%s\", %s);", PAIRED_OBJECT_NAME, newName, "new StringConstant(lexeme)");
			term.setCode(newCode);
		};
	}

	private Consumer<Pair<Production, List<NameBinding>>> updateCode(HashMap<String, Pair<Type,SpliceBindExn>> toGen, Environment lhsEnv, Reference<Integer> methNum, String thisTypeName) {
		return (Pair<Production, List<NameBinding>> inp) -> {
			Production prod = inp.first();
			List<NameBinding> bindings = inp.second();

			//Generate the new Wyvern method name
			String newName = prod.getName().toString() + "GEN" + methNum.get();
			methNum.set(methNum.get()+1);

			//Parse the input code
			SpliceBindExn spliced = LangUtil.spliceBinding(new IParseBuffer(prod.getCode()), bindings);

			Type resType = lhsEnv.lookup(prod.getLhs().getName().toString()).getType();

			//Save it to the external dict
			toGen.put(newName, new Pair<>(resType, spliced));

			String args = bindings.stream().map(nb->nb.getName()).reduce((a,b)->a+", "+b).get();
			//Code to invoke the equivalent function
			String newCode = "RESULT = Util.invokeValueVarargs("+PAIRED_OBJECT_NAME+", \""+newName+"\", "+args+");";

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
