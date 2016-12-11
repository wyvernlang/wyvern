package wyvern.tools.tests;

import java.io.File;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.ASTNode;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.expression.StringLiteral;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.InterpreterState;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.transformers.DynCastsTransformer;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.suites.CurrentlyBroken;
import wyvern.tools.tests.tagTests.TestUtil;
import wyvern.tools.typedAST.interfaces.ExpressionAST;

public class TransformTests {

	public static final String LIB_PATH = "../stdlib/";

	private static final NominalType systemDotDyn = new NominalType("system", "Dyn");
	
    @BeforeClass public static void setupResolver() {
    	TestUtil.setPaths();
    }

	/**
	 * Typecheck a program. Asserts that the result is the expectedType.
	 * @param program: program to typecheck.
	 * @param expectedType: the type you should get from typechecking.l
	 */
	private static void typecheck(IExpr program, ValueType expectedType) {
		TypeContext ctx = Globals.getStandardTypeContext();
		ValueType actualType = program.typeCheck(ctx);
		if (expectedType != null)
			Assert.assertEquals(expectedType, actualType);
	}
	
	/**
	 * Run a program. Asserts that the result is the expectedOutput.
	 * @param program: program to run.
	 * @param expectedOutput: value you should get from running the program.
	 */
	private static void run(IExpr program, Value expectedOutput) {
		EvalContext ctx = Globals.getStandardEvalContext();
		Value actualOutput = program.interpret(ctx);
		if (expectedOutput != null)
			Assert.assertEquals(actualOutput, expectedOutput);
	}
	
	/**
	 * Run a program. Asserts that the given ErrorMessage is thrown during execution.
	 * @param program: program to run.
	 * @param expectedError: the error which should be thrown during execution.
	 */
	private static void runWithToolError(IExpr program, ErrorMessage expectedError) {
		try {
			run(program, null);
			Assert.fail("Program finished executing, but should have thrown the ToolError " + expectedError);
		}
		catch (ToolError toolErr) {
			Assert.assertEquals("Executing program gave wrong ToolError",
					toolErr.getTypecheckingErrorMessage(), expectedError);
		}
	}
	
	/**
	 * Compile a program from source code to IL code.
	 * @param input: source code of program to be compiled.
	 * @return a program in IL code.
	 * @throws ParseException: if the source code is malformed.
	 */
	private static IExpr compile(String input) throws ParseException {
		ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input, "Transformer Test");
		GenContext genCtx = Globals.getGenContext(new InterpreterState(InterpreterState.PLATFORM_JAVA,
                                                                   new File(TestUtil.BASE_PATH),
                                                                   new File(LIB_PATH)));
		final LinkedList<TypedModuleSpec> dependencies = new LinkedList<TypedModuleSpec>();
		IExpr program = ast.generateIL(genCtx, null, dependencies);
		program = genCtx.getInterpreterState().getResolver().wrap(program, dependencies);
        return program;
	}
	
	/**
	 * Compile a program from source code to IL code. Before IL code compilation, several transformations are applied.
	 * @param input: source code of program to be compiled.
	 * @param transformations: array of transformations to be applied.
	 * @return a program in IL code.
	 * @throws ParseException: if the source code is malformed.
	 */
	private static IExpr compile(String input, ASTVisitor<TypeContext, ASTNode>... transformations) throws ParseException {
		IExpr program = compile(input);
		for (ASTVisitor<TypeContext, ASTNode> transformer : transformations) {
			GenContext ctx = Globals.getStandardGenContext();
			program = (IExpr) program.acceptVisitor(transformer, ctx);
		}
        return program;
	}
	
	@Test
	public void testSafeDynCasting() throws ParseException {
		
		String code = "val x : Dyn = 5\n"
				    + "val y : Dyn = x\n"
				    + "x";
		IExpr program = compile(code);
		
		// What the program does without transformation...
		typecheck(program, systemDotDyn);
		run(program, new IntegerLiteral(5));
		
		// ...should be the same with transformation.
		program = compile(code, new DynCastsTransformer());
		typecheck(program, systemDotDyn);
		run(program, new IntegerLiteral(5));
		
	}
	
	@Test
	public void testUnsafeDynCasting() throws ParseException {
		
		String input = "val intToInt: Dyn = new\n"
				     + "    def app():system.Int = 5\n\n"
				     + "val anInt: system.Int = intToInt\n"
				     + "anInt";

		// Should compile and run OK without casts.
		IExpr program = compile(input);		
		typecheck(program, Util.intType());
		
		// But we should get a casting error, once we've added the casts.
		program = compile(input, new DynCastsTransformer());
		typecheck(program, Util.intType());
		runWithToolError(program, ErrorMessage.NOT_SUBTYPE);	
		
	}
	
	@Test
	public void testSafeFieldAccess() throws ParseException {
	    
	    String input = "val obj: Dyn = new\n"
	                 + "    val field: system.Int = 5\n"
	                 + "obj.field\n";
	    
	    // Should compile and run OK without casts.
	    IExpr program = compile(input);
	    typecheck(program, Util.dynType());
	    
	    // Should compile and run OK with casts.
	    program = compile(input, new DynCastsTransformer());
	    typecheck(program, Util.dynType());
	    run(program, new IntegerLiteral(5));
	    
	}
	
	@Test
	public void testUnsafeFieldAccess() throws ParseException {
	    
	    String input = "val obj: Dyn = new\n"
	                 + "    val field: system.String = \"hello\"\n"
	                 + "val x: system.Int = obj.field\n"
	                 + "x";
	    
	    // Should compile and run OK without casts.
	    IExpr program = compile(input);
	    typecheck(program, Util.intType());
	    run(program, new StringLiteral("hello"));
	                 
	    // Should throw a runtime type-error with casts.
	    program = compile(input, new DynCastsTransformer());
	    typecheck(program, Util.intType());
	    runWithToolError(program, ErrorMessage.NOT_SUBTYPE);    
        
	}
	
	@Test
	public void testUnsafeFieldWrite() throws ParseException {
	    
	    String input = "val obj: Dyn = new\n"
	                 + "    var field: system.Int = 5\n"
	                 + "obj.field = \"broken\"\n";
	    
	    // Should compile and run OK without casts.
	    IExpr program = compile(input);
	    typecheck(program, Util.unitType());
	    run(program, Util.unitValue());
	    
	    // Should throw a runtime type-error with casts.
	    program = compile(input, new DynCastsTransformer());
	    typecheck(program, Util.unitType());
	    runWithToolError(program, ErrorMessage.NOT_SUBTYPE);
	    
	}
	
	@Test
	public void testSafeFieldWrite() throws ParseException {
	    
	    String input = "val obj: Dyn = new\n"
	                 + "    var field: system.Int = 5\n"
	                 + "obj.field = 10\n";
	    
	    // Should compile and run OK without casts.
	    IExpr program = compile(input);
	    typecheck(program, Util.unitType());
	    run(program, Util.unitValue());
	    
	    // Should compile and run OK with casts.
	    program = compile(input, new DynCastsTransformer());
	    typecheck(program, Util.unitType());
	    run(program, Util.unitValue());
	    
	}
	
	@Test
	public void testSafeMethodCall() throws ParseException {
	    
	    String input = "val obj: Dyn = new\n"
	                 + "    def five():system.Int = 5\n"
	                 + "obj.five()\n";

        // Should compile and run OK without casts.
        IExpr program = compile(input);
        typecheck(program, Util.dynType());
        run(program, new IntegerLiteral(5));
        
        // Should compile and run OK with casts.
        program = compile(input, new DynCastsTransformer());
        typecheck(program, Util.dynType());
        run(program, new IntegerLiteral(5));
	    
	}
	
	@Test
	public void testUnsafeMethodCall() throws ParseException {
	    
	    String input = "val obj: Dyn = new\n"
	                 + "    def five():system.Int = 5\n"
	                 + "val str: system.String = obj.five()";
	    
	    // Should compile and run OK without casts.
	    IExpr program = compile(input);
	    typecheck(program, Util.unitType());
	    run(program, Util.unitValue());
	    
	    // Should give a run-time type error with casts.
	    program = compile(input, new DynCastsTransformer());
	    typecheck(program, Util.unitType());
        runWithToolError(program, ErrorMessage.NOT_SUBTYPE);
	    
	}
	
	@Test
	@Category(CurrentlyBroken.class)
	public void testSafeMethodCall2() throws ParseException {
	    
	    String input = "val ticker: Dyn = new\n"
	                 + "    val clock = new\n"
	                 + "        var count: Int = 0\n"
	                 + "    def tick(): Int\n"
	                 + "        val result: Int = this.clock.count\n"
	                 + "        this.clock.count = this.clock.count + 1\n"
	                 + "        result\n"
	                 + "ticker.tick()";
	                 
	    // Should compile and run OK without casts.
	    IExpr program = compile(input);
	    typecheck(program, Util.dynType());
	    run(program, new IntegerLiteral(0));
	    
	    // Should still compile and run OK with casts.
	    program = compile(input, new DynCastsTransformer());
	    typecheck(program, Util.dynType());
	    run(program, new IntegerLiteral(0));
	    
	}
	
}
