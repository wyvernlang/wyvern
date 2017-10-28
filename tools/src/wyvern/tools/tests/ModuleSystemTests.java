package wyvern.tools.tests;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.suites.CurrentlyBroken;
import wyvern.tools.tests.suites.RegressionTests;
import wyvern.tools.tests.TestUtil;
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

    /*
	@Test
    @Deprecated
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
    @Deprecated
	public void testRequire() throws ParseException {
		String program = TestUtil.readFile(PATH + "require.wyv");
		TypedAST ast = TestUtil.getNewAST(program, "test input");
		WyvernResolver.getInstance().setNewParser(true);
		ast.typecheck(Globals.getStandardEnv(), Optional.empty());
		ast.evaluate(Globals.getStandardEvalEnv());
	}

	@Test
    @Deprecated
	public void testRsType() throws ParseException {
		String program = TestUtil.readFile(PATH + "rsType.wyv");
		TypedAST ast = TestUtil.getNewAST(program, "test input");
		WyvernResolver.getInstance().setNewParser(true);
		ast.typecheck(Globals.getStandardEnv(), Optional.empty());
		ast.evaluate(Globals.getStandardEvalEnv());
	}

	@Test
    @Deprecated
	public void testWyt() throws ParseException {
		String program = TestUtil.readFile(PATH + "Log.wyt");
		TypedAST ast = TestUtil.getNewAST(program, "test input");
		WyvernResolver.getInstance().setNewParser(true);
		ast.typecheck(Globals.getStandardEnv(), Optional.empty());
		ast.evaluate(Globals.getStandardEvalEnv());
	}
	*/

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
	 *
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
	*/
	@Test
	public void testADT() throws ParseException {
		TestUtil.doTestScriptModularly("modules.listClient",
				Util.intType(),
				new IntegerLiteral(5));
	}
	
	@Test
	public void testTransitiveAuthorityGood() throws ParseException {
	    TestUtil.doTestScriptModularly("modules.databaseClientGood",
	            Util.intType(),
	            new IntegerLiteral(1));
	}
	
    @Test
    public void testTransitiveAuthorityBad() throws ParseException {
        TestUtil.doTestScriptModularlyFailing("modules.databaseClientBad",
                ErrorMessage.NO_SUCH_METHOD);
    }
	
    @Test
    public void testTopLevelVars() throws ParseException {
        TestUtil.doTestScriptModularly("modules.databaseUser",
                Util.intType(),
                new IntegerLiteral(10));
    }
    
    @Test
    public void testTopLevelVarsWithAliasing() throws ParseException {
        TestUtil.doTestScriptModularly("modules.databaseUserTricky",
                Util.intType(),
                new IntegerLiteral(10));
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
		TestUtil.doTestScriptModularly("modules.simpleADTdriver", Util.intType(), new IntegerLiteral(5));
	}
	
}
