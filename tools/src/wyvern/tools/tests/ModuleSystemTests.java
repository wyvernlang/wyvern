package wyvern.tools.tests;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
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
import wyvern.tools.tests.suites.CurrentlyBroken;
import wyvern.tools.tests.suites.RegressionTests;
import wyvern.tools.tests.tagTests.TestUtil;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;

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
		TypedAST ast = TestUtil.getNewAST(program, "test input");
		WyvernResolver.getInstance().setNewParser(true);
		ast.typecheck(Globals.getStandardEnv(), Optional.empty());
		ast.evaluate(Globals.getStandardEvalEnv());
	}

	@Test
	public void testImport() throws ParseException {
		String program = TestUtil.readFile(PATH + "import.wyv");
		TypedAST ast = TestUtil.getNewAST(program, "test input");
		WyvernResolver.getInstance().setNewParser(true);
		typeCheckfailWith(ast, ErrorMessage.MODULE_TYPE_ERROR);
	}

	@Test
	public void testRequire() throws ParseException {
		String program = TestUtil.readFile(PATH + "require.wyv");
		TypedAST ast = TestUtil.getNewAST(program, "test input");
		WyvernResolver.getInstance().setNewParser(true);
		ast.typecheck(Globals.getStandardEnv(), Optional.empty());
		ast.evaluate(Globals.getStandardEvalEnv());
	}

	@Test
	public void testRsType() throws ParseException {
		String program = TestUtil.readFile(PATH + "rsType.wyv");
		TypedAST ast = TestUtil.getNewAST(program, "test input");
		WyvernResolver.getInstance().setNewParser(true);
		ast.typecheck(Globals.getStandardEnv(), Optional.empty());
		ast.evaluate(Globals.getStandardEvalEnv());
	}

	@Test
	public void testWyt() throws ParseException {
		String program = TestUtil.readFile(PATH + "Log.wyt");
		TypedAST ast = TestUtil.getNewAST(program, "test input");
		WyvernResolver.getInstance().setNewParser(true);
		ast.typecheck(Globals.getStandardEnv(), Optional.empty());
		ast.evaluate(Globals.getStandardEvalEnv());
	}

	@Test
	public void testInst() throws ParseException {
		String program = TestUtil.readFile(PATH + "inst.wyv");
		TypedAST ast = TestUtil.getNewAST(program, "test input");
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
		ILTests.doTestScriptModularly("modules.listClient",
				Util.intType(),
				new IntegerLiteral(5));
	}
	
	@Test
	@Category(CurrentlyBroken.class)
	public void testTransitiveAuthorityBad() throws ParseException {

		String[] fileList = {"database.wyv", "databaseProxy.wyv", "databaseClientBad.wyv"};
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), new NominalType("", "system"));
		genCtx = new TypeGenContext("Int", "system", genCtx);
		genCtx = new TypeGenContext("Unit", "system", genCtx);
		
		List<wyvern.target.corewyvernIL.decl.Declaration> decls = new LinkedList<wyvern.target.corewyvernIL.decl.Declaration>();
		
		for(String fileName : fileList) {
			String source = TestUtil.readFile(PATH + fileName);
			TypedAST ast = TestUtil.getNewAST(source, "test input");
			wyvern.target.corewyvernIL.decl.Declaration decl = ((Declaration) ast).topLevelGen(genCtx, null);
			decls.add(decl);
			genCtx = GenUtil.link(genCtx, decl);
		}

		// Should give some compilation error, but top-level vars are not implemented yet.
		// (21/12/2015)
	}
	
	@Test
	@Category(CurrentlyBroken.class)
	public void testTransitiveAuthorityGood() throws ParseException {

		String[] fileList = {"database.wyv", "databaseProxy.wyv", "databaseClientGood.wyv"};
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), new NominalType("", "system"));
		genCtx = new TypeGenContext("Int", "system", genCtx);
		genCtx = new TypeGenContext("Unit", "system", genCtx);
		
		List<wyvern.target.corewyvernIL.decl.Declaration> decls = new LinkedList<wyvern.target.corewyvernIL.decl.Declaration>();
		
		for(String fileName : fileList) {
			System.out.println(fileName);
			String source = TestUtil.readFile(PATH + fileName);
			TypedAST ast = TestUtil.getNewAST(source, "test input");
			wyvern.target.corewyvernIL.decl.Declaration decl = ((Declaration) ast).topLevelGen(genCtx, null);
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
	
		// Load and link database.wyv.
		TypedAST astDatabase = TestUtil.getNewAST(TestUtil.readFile(PATH + "database.wyv"), "test input");
		wyvern.target.corewyvernIL.decl.Declaration decl = ((Declaration) astDatabase).topLevelGen(genCtx, null);
		genCtx = GenUtil.link(genCtx, decl);
		
		// Interpret databaseUser.wyv with database.wyv in the context.
		String source = TestUtil.readFile(PATH + "databaseUser.wyv");
		ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(source, "test input");
		IExpr program = ast.generateIL(genCtx, Util.intType(), null);
		TypeContext ctx = TypeContext.empty();
		ValueType t = program.typeCheck(ctx);
		Assert.assertEquals(Util.intType(), t);
		wyvern.target.corewyvernIL.expression.Value result = program.interpret(EvalContext.empty());
		Assert.assertEquals(new IntegerLiteral(10), result);
		
	}
	
	@Test
	@Category(CurrentlyBroken.class)
	public void testTopLevelVarsWithAliasing () throws ParseException {
		
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
		genCtx = new TypeGenContext("Int", "system", genCtx);
		genCtx = new TypeGenContext("Unit", "system", genCtx);
	
		// Load and link database.wyv.
		TypedAST astDatabase = TestUtil.getNewAST(TestUtil.readFile(PATH + "database.wyv"), "test input");
		wyvern.target.corewyvernIL.decl.Declaration decl = ((Declaration) astDatabase).topLevelGen(genCtx, null);
		genCtx = GenUtil.link(genCtx, decl);
		
		// Interpret databaseUser.wyv with database.wyv in the context.
		String source = TestUtil.readFile(PATH + "databaseUserTricky.wyv");
		ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(source, "test input");
		IExpr program = ast.generateIL(genCtx, Util.intType(), new LinkedList<TypedModuleSpec>());
		TypeContext ctx = TypeContext.empty();
		ValueType t = program.typeCheck(ctx);
		Assert.assertEquals(Util.intType(), t);
		wyvern.target.corewyvernIL.expression.Value result = program.interpret(EvalContext.empty());
		Assert.assertEquals(new IntegerLiteral(10), result);
		
	}
	
	@Test
	public void testTopLevelVarGet () throws ParseException {
		GenContext genCtx = Globals.getStandardGenContext();
		/*GenContext.empty().extend("system", new Variable("system"), null);
		genCtx = new TypeGenContext("Int", "system", genCtx);
		genCtx = new TypeGenContext("Unit", "system", genCtx);*/
	
		String source = "var v : Int = 5\n"
					  + "v\n";
		
		// Generate code to be evaluated.
		ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(source, "test input");
		IExpr program = ast.generateIL(genCtx, Util.intType(), null);
		
		// Evaluate.
		wyvern.target.corewyvernIL.expression.Value result = program.interpret(Globals.getStandardEvalContext());
		Assert.assertEquals(new IntegerLiteral(5), result);
		
	}
	
	@Test
	public void testTopLevelVarSet () throws ParseException {
		GenContext genCtx = Globals.getStandardGenContext();
	
		String source = "var v : Int = 5\n"
					  + "v = 10\n"
					  + "v\n";
		
		// Generate code to be evaluated.
		ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(source, "test input");
		IExpr program = ast.generateIL(genCtx, Util.intType(), null);
		
		// Evaluate.
		wyvern.target.corewyvernIL.expression.Value result = program.interpret(Globals.getStandardEvalContext());
		Assert.assertEquals(new IntegerLiteral(10), result);
		
	}
	
	@Test
	public void testSimpleADT() throws ParseException {
		ILTests.doTestScriptModularly("modules.simpleADTdriver", Util.intType(), new IntegerLiteral(5));
	}
	
}
