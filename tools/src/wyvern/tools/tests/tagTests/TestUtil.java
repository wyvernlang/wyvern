package wyvern.tools.tests.tagTests;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.TypeDeclaration;
import wyvern.target.corewyvernIL.decltype.AbstractTypeMember;
import wyvern.target.corewyvernIL.decltype.ConcreteTypeMember;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.expression.ObjectValue;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.modules.Module;
import wyvern.target.corewyvernIL.support.EmptyGenContext;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.GenUtil;
import wyvern.target.corewyvernIL.support.InterpreterState;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.TypeGenContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.DynamicType;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.parsing.Wyvern;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.parsing.coreparser.ParseUtils;
import wyvern.tools.parsing.coreparser.WyvernParser;
import wyvern.tools.parsing.coreparser.WyvernParserConstants;
import wyvern.tools.typedAST.core.expressions.TaggedInfo;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Type;

public class TestUtil {
	public static final String BASE_PATH = "src/wyvern/tools/tests/";
	public static final String STDLIB_PATH = BASE_PATH + "stdlib/";
	public static final String LIB_PATH = "src/wyvern/lib/";
	private static final String PLATFORM_PATH = BASE_PATH + "platform/java/stdlib/";
	
	/** Sets up the standard library and platform paths in the Wyvern resolver
	 * 
	 */
	public static void setPaths() {
    	WyvernResolver.getInstance().resetPaths();
		WyvernResolver.getInstance().addPath(STDLIB_PATH);
		WyvernResolver.getInstance().addPath(PLATFORM_PATH);
	}

	/**
	 * Converts the given program into the AST representation.
	 * 
	 * @param program
	 * @return
	 * @throws IOException 
	 * @throws CopperParserException 
	 */
	public static TypedAST getAST(String program) throws CopperParserException, IOException {
		clearGlobalTagInfo();
		return (TypedAST)new Wyvern().parse(new StringReader(program), "test input");
	}
	
	/**
	 * Converts the given program into the TypedAST representation, using the
	 * new Wyvern parser.
	 * 
	 * @param program
	 * @return
	 * @throws IOException 
	 * @throws CopperParserException 
	 */
	public static TypedAST getNewAST(String program) throws ParseException {
		clearGlobalTagInfo();
		Reader r = new StringReader(program);
		WyvernParser<TypedAST,Type> wp = ParseUtils.makeParser("test input", r);
		TypedAST result = wp.CompilationUnit();
		Assert.assertEquals("Could not parse the entire file, last token ", WyvernParserConstants.EOF, wp.token_source.getNextToken().kind);
		return result;
	}
	
	/**
	 * Loads and parses the given file into the TypedAST representation, using the
	 * new Wyvern parser.
	 * 
	 * @param program
	 * @return
	 * @throws IOException 
	 * @throws CopperParserException 
	 */
	public static TypedAST getNewAST(File programLocation) throws ParseException {
		String program = readFile(programLocation);
		clearGlobalTagInfo();
		Reader r = new StringReader(program);
		WyvernParser<TypedAST,Type> wp = ParseUtils.makeParser(programLocation.getPath(), r);
		TypedAST result = wp.CompilationUnit();
		Assert.assertEquals("Could not parse the entire file, last token ", WyvernParserConstants.EOF, wp.token_source.getNextToken().kind);
		return result;
	}
	
	/**
	 * Completely evaluates the given AST, and compares it to the given value.
	 * Does typechecking first, then evaluation.
	 * 
	 * @param ast
	 * @param value
	 */
	public static void evaluateExpecting(TypedAST ast, int value) {
		ast.typecheck(Globals.getStandardEnv(), Optional.empty());
		Value v = ast.evaluate(Globals.getStandardEvalEnv());
		
		String expecting = "IntegerConstant(" + value + ")";

		Assert.assertEquals(expecting, v.toString());
	}
	
	public static GenContext getGenContext(InterpreterState state) {
		if (state.getGenContext() != null)
			return state.getGenContext();
		GenContext genCtx = new EmptyGenContext(state).extend("system", new Variable("system"), getSystemType());
		return addTypeAbbrevs(genCtx);
	}

	public static GenContext getStandardGenContext() {
		/*GenContext genCtx = getGenContext(new InterpreterState(null)).extend("system", new Variable("system"), getSystemType());
		return addTypeAbbrevs(genCtx);*/
		return getGenContext(new InterpreterState(new File(BASE_PATH), null));
	}

	private static GenContext addTypeAbbrevs(GenContext genCtx) {
		genCtx = new TypeGenContext("Int", "system", genCtx); // slightly weird
		genCtx = new TypeGenContext("Unit", "system", genCtx);
		genCtx = new TypeGenContext("String", "system", genCtx);
		genCtx = new TypeGenContext("Boolean", "system", genCtx);
		genCtx = new TypeGenContext("Dyn", "system", genCtx);
		//genCtx.getInterpreterState().setGenContext(genCtx);
		genCtx = GenUtil.ensureJavaTypesPresent(genCtx);
		return genCtx;
	}

	private static ValueType getSystemType() {
		List<FormalArg> ifTrueArgs = Arrays.asList(
				new FormalArg("trueBranch", Util.unitToDynType()),
				new FormalArg("falseBranch", Util.unitToDynType()));
		List<DeclType> boolDeclTypes = Arrays.asList(new DefDeclType("ifTrue", new DynamicType(), ifTrueArgs));
		// construct a type for the system object
		List<DeclType> declTypes = new LinkedList<DeclType>();
		//declTypes.add(new AbstractTypeMember("Int"));
		List<DeclType> intDeclTypes = new LinkedList<DeclType>();
		intDeclTypes.add(new DefDeclType("+", Util.intType(), Arrays.asList(new FormalArg("other", Util.intType()))));
		intDeclTypes.add(new DefDeclType("-", Util.intType(), Arrays.asList(new FormalArg("other", Util.intType()))));
		intDeclTypes.add(new DefDeclType("*", Util.intType(), Arrays.asList(new FormalArg("other", Util.intType()))));
		intDeclTypes.add(new DefDeclType("/", Util.intType(), Arrays.asList(new FormalArg("other", Util.intType()))));
		ValueType intType = new StructuralType("intSelf", intDeclTypes);
		ValueType boolType = new StructuralType("boolean", boolDeclTypes);
		declTypes.add(new ConcreteTypeMember("Int", intType));
		declTypes.add(new ConcreteTypeMember("Boolean", boolType));
		declTypes.add(new ConcreteTypeMember("Unit", Util.unitType()));
		declTypes.add(new AbstractTypeMember("String"));
		declTypes.add(new ConcreteTypeMember("Dyn", new DynamicType()));
		ValueType systemType = new StructuralType("system", declTypes);
		return systemType;
	}
	
	private static ObjectValue getSystemValue() {
		// construct a type for the system object
		List<Declaration> decls = new LinkedList<Declaration>();
		decls.add(new TypeDeclaration("Int", new NominalType("this", "Int"), FileLocation.UNKNOWN));
		decls.add(new TypeDeclaration("Unit", new NominalType("this", "Unit"), FileLocation.UNKNOWN));
		decls.add(new TypeDeclaration("String", new NominalType("this", "String"), FileLocation.UNKNOWN));
		decls.add(new TypeDeclaration("Dyn", new DynamicType(), FileLocation.UNKNOWN));
		ObjectValue systemVal = new ObjectValue(decls, "this", getSystemType(), null, EvalContext.empty());
		return systemVal;
	}
	
	public static EvalContext getStandardEvalContext() {
		EvalContext ctx = EvalContext.empty();
    	ctx = ctx.extend("system", getSystemValue());
		return ctx;
	}
	
	public static TypeContext getStandardTypeContext() {
    	GenContext ctx = GenContext.empty();
    	ctx = ctx.extend("system", new Variable("system"), getSystemType());
    	ctx = GenUtil.ensureJavaTypesPresent(ctx);
		return ctx;
	}
	
	public static void evaluateExpecting(TypedAST ast, String value) {
		ast.typecheck(Globals.getStandardEnv(), Optional.empty());
		Value v = ast.evaluate(Globals.getStandardEvalEnv());

		// System.out.println("Got value: " + v);
		
		String expecting = "StringConstant(\"" + value + "\")"; 

		Assert.assertEquals(expecting, v.toString());
	}

	/**
	 * Completely evaluates the given AST, and compares it to the given value.
	 * Does typechecking first, then evaluation.
	 * 
	 * @param ast
	 * @param value
	 */
	public static void evaluateExpectingPerf(TypedAST ast, int value) {
		Value v = ast.evaluate(Globals.getStandardEvalEnv());
		
		String expecting = "IntegerConstant(" + value + ")"; 

		Assert.assertEquals(expecting, v.toString());
	}
	
	public static void evaluatePerf(TypedAST ast) {
		Value v = ast.evaluate(Globals.getStandardEvalEnv());
		
		//String expecting = "IntegerConstant(" + value + ")"; 

		//Assert.assertEquals(expecting, v.toString());
	}
	
	/**
	 * First typechecks the AST, then executes it.
	 * 
	 * Any returned value is discarded, but anything printed to stdout will be visible.
	 * 
	 * @param ast
	 */
	public static void evaluate(TypedAST ast) {
		ast.typecheck(Globals.getStandardEnv(), Optional.empty());
		ast.evaluate(Globals.getStandardEvalEnv());
	}
	
	/**
	 * First typechecks the AST, then executes it.
	 * If any file is loaded, it is parsed by the new parser.
	 * 
	 * Any returned value is returned, and anything printed to stdout will be visible.
	 * 
	 * @param ast
	 */
	public static Value evaluateNew(TypedAST ast) {
		boolean oldParserFlag = WyvernResolver.getInstance().setNewParser(true);
		try {
			ast.typecheck(Globals.getStandardEnv(), Optional.empty());
			return ast.evaluate(Globals.getStandardEvalEnv());
		} finally {
			WyvernResolver.getInstance().setNewParser(oldParserFlag);
		}
	}
	
	public static String readFile(String filename) {
		return readFile(new File(filename));
	}
	
	public static String readFile(File file) {
		try {
			StringBuffer b = new StringBuffer();
			
			for (String s : Files.readAllLines(file.toPath())) {
				//Be sure to add the newline as well
				b.append(s).append("\n");
			}
			
			return b.toString();
		} catch (IOException e) {
			Assert.fail("Failed opening file: " + file.getPath());
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Removes the global tagged-type data.
	 */
	private static void clearGlobalTagInfo() {
		TaggedInfo.clearGlobalTaggedInfos();
	}
}
