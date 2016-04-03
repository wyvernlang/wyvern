package wyvern.tools.tests.tagTests;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.TypeDeclaration;
import wyvern.target.corewyvernIL.decltype.AbstractTypeMember;
import wyvern.target.corewyvernIL.decltype.ConcreteTypeMember;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.ObjectValue;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.GenUtil;
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
	private static final String STDLIB_PATH = BASE_PATH + "stdlib/";
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
	
	public static GenContext getStandardGenContext() {
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), getSystemType());
		genCtx = new TypeGenContext("Int", "system", genCtx); // slightly weird
		genCtx = new TypeGenContext("Unit", "system", genCtx);
		genCtx = new TypeGenContext("String", "system", genCtx);
		genCtx = new TypeGenContext("Dyn", "system", genCtx);
		genCtx = GenUtil.ensureJavaTypesPresent(genCtx);
		return genCtx;
	}

	private static ValueType getSystemType() {
		// construct a type for the system object
		List<DeclType> declTypes = new LinkedList<DeclType>();
		declTypes.add(new AbstractTypeMember("Int"));
		declTypes.add(new ConcreteTypeMember("Unit", Util.unitType()));
		declTypes.add(new AbstractTypeMember("String"));
		declTypes.add(new ConcreteTypeMember("Dyn", new DynamicType()));
		ValueType systemType = new StructuralType("this", declTypes);
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
		try {
			StringBuffer b = new StringBuffer();
			
			for (String s : Files.readAllLines(new File(filename).toPath())) {
				//Be sure to add the newline as well
				b.append(s).append("\n");
			}
			
			return b.toString();
		} catch (IOException e) {
			Assert.fail("Failed opening file: " + filename);
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
