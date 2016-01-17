package wyvern.tools.tests;

import static java.util.Optional.empty;
import static wyvern.stdlib.Globals.getStandardEnv;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.FieldGet;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.GenUtil;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.TypeGenContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.parsing.coreparser.TokenManager;
import wyvern.tools.parsing.coreparser.WyvernParser;
import wyvern.tools.parsing.coreparser.WyvernTokenManager;
import wyvern.tools.tests.suites.CurrentlyBroken;
import wyvern.tools.tests.suites.RegressionTests;
import wyvern.tools.tests.tagTests.TestUtil;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.binding.evaluation.EvaluationBinding;
import wyvern.tools.typedAST.core.declarations.ModuleDeclaration;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Int;

@Category(RegressionTests.class)
public class ModuleSystemTests {

	private static final String BASE_PATH = TestUtil.BASE_PATH;
	private static final String PATH = BASE_PATH + "modules/";

    @BeforeClass public static void setupResolver() {
    	TestUtil.setPaths();
		WyvernResolver.getInstance().addPath(PATH);
    }

	@Test
	public void testResource() throws ParseException {
		String program = TestUtil.readFile(PATH + "testModule.wyv");
		TypedAST ast = TestUtil.getNewAST(program);
		WyvernResolver.getInstance().setNewParser(true);
		ast.typecheck(Globals.getStandardEnv(), Optional.empty());
		ast.evaluate(Globals.getStandardEvalEnv());
	}

	@Test
	public void testImport() throws ParseException {
		String program = TestUtil.readFile(PATH + "import.wyv");
		TypedAST ast = TestUtil.getNewAST(program);
		WyvernResolver.getInstance().setNewParser(true);
		typeCheckfailWith(ast, ErrorMessage.MODULE_TYPE_ERROR);
	}

	@Test
	public void testRequire() throws ParseException {
		String program = TestUtil.readFile(PATH + "require.wyv");
		TypedAST ast = TestUtil.getNewAST(program);
		WyvernResolver.getInstance().setNewParser(true);
		ast.typecheck(Globals.getStandardEnv(), Optional.empty());
		ast.evaluate(Globals.getStandardEvalEnv());
	}

	@Test
	public void testRsType() throws ParseException {
		String program = TestUtil.readFile(PATH + "rsType.wyv");
		TypedAST ast = TestUtil.getNewAST(program);
		WyvernResolver.getInstance().setNewParser(true);
		ast.typecheck(Globals.getStandardEnv(), Optional.empty());
		ast.evaluate(Globals.getStandardEvalEnv());
	}

	@Test
	public void testWyt() throws ParseException {
		String program = TestUtil.readFile(PATH + "Log.wyt");
		TypedAST ast = TestUtil.getNewAST(program);
		WyvernResolver.getInstance().setNewParser(true);
		ast.typecheck(Globals.getStandardEnv(), Optional.empty());
		ast.evaluate(Globals.getStandardEvalEnv());
	}

	@Test
	public void testInst() throws ParseException {
		String program = TestUtil.readFile(PATH + "inst.wyv");
		TypedAST ast = TestUtil.getNewAST(program);
	}

	@Test
	@Category(CurrentlyBroken.class)
	public void testDaryaModuleExample() throws ParseException {
		String program = TestUtil.readFile(PATH + "paper-module-example/Main.wyv");
		TypedAST ast = TestUtil.getNewAST(program);
		WyvernResolver.getInstance().setNewParser(true);
		ast.typecheck(Globals.getStandardEnv(), Optional.empty());
		ast.evaluate(Globals.getStandardEvalEnv());
	}

	/**
	 * Attempts to typecheck the given AST and catch the given ErrorMessage.
	 * This error being thrown indicates the test passed.
	 *
	 * If the error isn't thrown, the test fails.
	 *
	 * @param ast
	 * @param errorMessage
	 */
	private static void typeCheckfailWith(TypedAST ast, ErrorMessage errorMessage) {
		try {
			ast.typecheck(Globals.getStandardEnv(), Optional.empty());
		} catch (ToolError toolError) {
			// toolError.printStackTrace(); // FIXME:
			System.out.println(errorMessage);
			Assert.assertEquals(errorMessage, toolError.getTypecheckingErrorMessage());

			return;
		}

		Assert.fail("Should have failed with error: " + errorMessage);
	}

	@Test
	public void testADT() throws ParseException {
		
		String[] fileList = {"Lists.wyv", "ListClient.wyv"};
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), new NominalType("", "system"));
		genCtx = new TypeGenContext("Int", "system", genCtx);
		genCtx = new TypeGenContext("Unit", "system", genCtx);
		
		List<wyvern.target.corewyvernIL.decl.Declaration> decls = new LinkedList<wyvern.target.corewyvernIL.decl.Declaration>();
		
		for(String fileName : fileList) {
			System.out.println(fileName);
			String source = TestUtil.readFile(PATH + fileName);
			TypedAST ast = TestUtil.getNewAST(source);
			wyvern.target.corewyvernIL.decl.Declaration decl = ((Declaration) ast).topLevelGen(genCtx);
			decls.add(decl);
			genCtx = GenUtil.link(genCtx, decl);
		}
		
		Expression mainProgram = GenUtil.genExp(decls, genCtx);
		// after genExp the modules are transferred into an object. We need to evaluate one field of the main object
		Expression program = new FieldGet(mainProgram, "five"); 
		
    	TypeContext ctx = TypeContext.empty();
		ValueType t = program.typeCheck(ctx);
		wyvern.target.corewyvernIL.expression.Value v = program.interpret(EvalContext.empty());
    	IntegerLiteral five = new IntegerLiteral(5);
		Assert.assertEquals(five, v);
	}
	
	@Test
	@Category(CurrentlyBroken.class)
	public void testTransitiveAuthorityBad() throws ParseException {

		String[] fileList = {"Database.wyv", "DatabaseProxy.wyv", "DatabaseClientBad.wyv"};
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), new NominalType("", "system"));
		genCtx = new TypeGenContext("Int", "system", genCtx);
		genCtx = new TypeGenContext("Unit", "system", genCtx);
		
		List<wyvern.target.corewyvernIL.decl.Declaration> decls = new LinkedList<wyvern.target.corewyvernIL.decl.Declaration>();
		
		for(String fileName : fileList) {
			String source = TestUtil.readFile(PATH + fileName);
			TypedAST ast = TestUtil.getNewAST(source);
			wyvern.target.corewyvernIL.decl.Declaration decl = ((Declaration) ast).topLevelGen(genCtx);
			decls.add(decl);
			genCtx = GenUtil.link(genCtx, decl);
		}

		// Should give some compilation error, but top-level vars are not implemented yet.
		// (21/12/2015)
	}
	
	@Test
	@Category(CurrentlyBroken.class)
	public void testTransitiveAuthorityGood() throws ParseException {

		String[] fileList = {"Database.wyv", "DatabaseProxy.wyv", "DatabaseClientGood.wyv"};
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), new NominalType("", "system"));
		genCtx = new TypeGenContext("Int", "system", genCtx);
		genCtx = new TypeGenContext("Unit", "system", genCtx);
		
		List<wyvern.target.corewyvernIL.decl.Declaration> decls = new LinkedList<wyvern.target.corewyvernIL.decl.Declaration>();
		
		for(String fileName : fileList) {
			System.out.println(fileName);
			String source = TestUtil.readFile(PATH + fileName);
			TypedAST ast = TestUtil.getNewAST(source);
			wyvern.target.corewyvernIL.decl.Declaration decl = ((Declaration) ast).topLevelGen(genCtx);
			decls.add(decl);
			genCtx = GenUtil.link(genCtx, decl);
		}
		
		// Should compile OK, but top-level vars not implemented yet.
		// (21/12/2015)
	}

	@Test
	@Category(CurrentlyBroken.class)
	public void testTopLevelVars () throws ParseException {
		
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
		genCtx = new TypeGenContext("Int", "system", genCtx);
		genCtx = new TypeGenContext("Unit", "system", genCtx);
	
		// Load and link Database.wyv.
		TypedAST astDatabase = TestUtil.getNewAST(TestUtil.readFile(PATH + "Database.wyv"));
		wyvern.target.corewyvernIL.decl.Declaration decl = ((Declaration) astDatabase).topLevelGen(genCtx);
		genCtx = GenUtil.link(genCtx, decl);
		
		// Interpret DatabaseUser.wyv with Database.wyv in the context.
		String source = TestUtil.readFile(PATH + "DatabaseUser.wyv");
		ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(source);
		Expression program = ast.generateIL(genCtx);
		TypeContext ctx = TypeContext.empty();
		ValueType t = program.typeCheck(ctx);
		Assert.assertEquals(Util.intType(), t);
		wyvern.target.corewyvernIL.expression.Value result = program.interpret(EvalContext.empty());
		Assert.assertEquals(new IntegerLiteral(10), result);
		
	}
	
	@Test
	public void testTopLevelVarsSimple () throws ParseException {
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
		genCtx = new TypeGenContext("Int", "system", genCtx);
		genCtx = new TypeGenContext("Unit", "system", genCtx);
	
		String source = "var v : Int = 5\n"
					  + "v\n";
		
		// Generate code to be evaluated.
		ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(source);
		Sequence seq = (Sequence) ast;
		Expression program = seq.generateIL(genCtx);
		
		// Figure out the type of this code as a module, add to TypeContext.
		ValueType vt = seq.figureOutType(genCtx);
		TypeContext ctx = TypeContext.empty().extend("this", vt);
		
		// Typecheck. 
		ValueType t = program.typeCheck(ctx);
		Assert.assertEquals(Util.intType(), t);
		
		// Evaluate.
		wyvern.target.corewyvernIL.expression.Value result = program.interpret(EvalContext.empty());
		Assert.assertEquals(new IntegerLiteral(5), result);
		
	}
	
}
