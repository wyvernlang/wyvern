package wyvern.tools.tests;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.FieldGet;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.expression.StringLiteral;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.GenUtil;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.TypeGenContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.Interpreter;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.interop.FObject;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.suites.CurrentlyBroken;
import wyvern.tools.tests.suites.RegressionTests;
import wyvern.tools.tests.tagTests.TestUtil;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;

@Category(RegressionTests.class)
public class ILTests {
    	
	private static final String BASE_PATH = TestUtil.BASE_PATH;
	private static final String PATH = BASE_PATH + "modules/module/";
	
    @BeforeClass public static void setupResolver() {
    	TestUtil.setPaths();
		WyvernResolver.getInstance().addPath(PATH);
    }

	@Test
    public void testLetVal() {
    	NominalType Int = new NominalType("system", "Int");
    	Variable x = new Variable("x");
    	IntegerLiteral five = new IntegerLiteral(5);
    	Expression letExpr = new Let("x", five, x);
    	
    	TypeContext ctx = TypeContext.empty();
		ValueType t = letExpr.typeCheck(ctx);
		Assert.assertEquals(Int, t);
		Value v = letExpr.interpret(EvalContext.empty());
		Assert.assertEquals(five, v);
    }
    
    @Test
    public void testLetValWithParse() throws ParseException {
        String input =
                  "val x = 5\n"
        		+ "x\n";
        ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input);
        Expression program = ast.generateIL(GenContext.empty(), null);
        TypeContext ctx = TypeContext.empty();
        ValueType t = program.typeCheck(ctx);
        Assert.assertEquals(Util.intType(), t);
        Value v = program.interpret(EvalContext.empty());
        IntegerLiteral five = new IntegerLiteral(5);
		Assert.assertEquals(five, v);
    }

    @Test
    public void testLetValWithString() throws ParseException {
        String input =
                  "val x = \"five\"\n"
        		+ "x\n";
        ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input);
        Expression program = ast.generateIL(GenContext.empty(), null);
        TypeContext ctx = TypeContext.empty();
        ValueType t = program.typeCheck(ctx);
        Assert.assertEquals(Util.stringType(), t);
        Value v = program.interpret(EvalContext.empty());
        StringLiteral five = new StringLiteral("five");
		Assert.assertEquals(five, v);
    }

    @Test
    public void testLetValWithString2() throws ParseException {
        String input =
                  "val x = \"five\"\n"
        		+ "x\n";
        ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input);
        Expression program = ast.generateIL(GenContext.empty(), null);
        TypeContext ctx = TypeContext.empty();
        ValueType t = program.typeCheck(ctx);
        Assert.assertEquals(Util.stringType(), t);
        Value v = program.interpret(EvalContext.empty());
        StringLiteral five = new StringLiteral("");
		Assert.assertNotEquals(five, v);
    }

	@Test
	public void testFieldRead() throws ParseException {
		String input = "val obj = new\n"
				     + "    val v = 5\n"
				     + "obj.v\n"
				     ;
		ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input);
		Expression program = ast.generateIL(GenContext.empty(), null);
    	TypeContext ctx = TypeContext.empty();
		ValueType t = program.typeCheck(ctx);
		Assert.assertEquals(Util.intType(), t);
		Value v = program.interpret(EvalContext.empty());
    	IntegerLiteral five = new IntegerLiteral(5);
		Assert.assertEquals(five, v);
	}
	
	@Test
	public void testVarFieldRead() throws ParseException {
		String input = "val obj = new\n"
				     + "    var v : system.Int = 5\n"
				     + "obj.v\n"
				     ;
		ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input);
		// bogus "system" entry, but makes the text work for now
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
		Expression program = ast.generateIL(genCtx, null);
    	TypeContext ctx = TypeContext.empty();
		ValueType t = program.typeCheck(ctx);
		Assert.assertEquals(Util.intType(), t);
		Value v = program.interpret(EvalContext.empty());
    	IntegerLiteral five = new IntegerLiteral(5);
		Assert.assertEquals(five, v);
	}
	
	@Test
	@Category(CurrentlyBroken.class)
	public void testVarFieldReadFromNonExistent() throws ParseException {
		String input = "val obj = new\n"
					 + "    var v : system.Int = 5\n"
					 + "obj.x\n";
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
		ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input);
		assertTypeCheckFails(ast, genCtx);
	}
	
	@Test
	public void testVarFieldWrite() throws ParseException {
		String input = "val obj = new\n"
				     + "    var v : system.Int = 2\n"
				     + "obj.v = 5\n"
				     + "obj.v\n"
				     ;
		ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input);
		// bogus "system" entry, but makes the text work for now
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
		Expression program = ast.generateIL(genCtx, null);
    	TypeContext ctx = TypeContext.empty();
		ValueType t = program.typeCheck(ctx);
		Assert.assertEquals(Util.intType(), t);
		Value v = program.interpret(EvalContext.empty());
    	IntegerLiteral five = new IntegerLiteral(5);
		Assert.assertEquals(five, v);
	}
	
	@Test
	public void testVarFieldWriteToWrongType() throws ParseException {
		String input = "val obj = new\n"
					 + "    var v : system.Int = 3\n"
					 + "obj.v = \"hello\"\n";
		ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input);
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
		assertTypeCheckFails(ast, genCtx);
	}
	
	@Test
	@Category(CurrentlyBroken.class)
	public void testVarFieldWriteToNonExistent () throws ParseException {
		String input = "val obj = new\n"
					 + "    var v : system.Int = 3\n"
					 + "obj.x = 5\n";
		ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input);
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
		assertTypeCheckFails(ast, genCtx);
	}
	
	@Test
	@Category(CurrentlyBroken.class)
	public void testWriteFieldtoOtherField () throws ParseException {
		// Need to declare a structural type T, then declare firstObj as var firstObj : T = new ...
		String input = "val firstObj = new\n"
					 + "    var a : system.Int = 5\n"
				     + "val secondObj = new\n"
				     + "    var b : system.Int = 10\n"
				     + "firstObj.a = secondObj.b\n"
				     + "firstObj.a";
		ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input);
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
		Expression program = ast.generateIL(genCtx, null);
		ValueType type = program.typeCheck(TypeContext.empty());
		Assert.assertEquals(Util.intType(), type);
		Value result = program.interpret(EvalContext.empty());
		IntegerLiteral ten = new IntegerLiteral(10);
		Assert.assertEquals(result, ten);
	}
	
	@Test
	public void testWriteToValField () throws ParseException {
		String input = "val object = new \n"
					 + "    val field : system.Int = 5 \n"
					 + "object.field = 10\n";
		ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input);
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
		assertTypeCheckFails(ast, genCtx);
	}

	@Test
	@Category(CurrentlyBroken.class)
	public void testTypeDeclarations () throws ParseException {
		String input = "resource type Doubler\n"
					 + "    def double(argument : system.Int) : system.Int\n"
					 + "val d : Doubler = new\n"
					 + "	def double (argument : system.Int) : system.Int\n"
					 + "		argument * 2\n"
					 + "d.double(5)";
		ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input);
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
		Expression program = ast.generateIL(genCtx, null);
		ValueType type = program.typeCheck(TypeContext.empty());
		Assert.assertEquals(Util.intType(), type);
		Value result = program.interpret(EvalContext.empty());
		IntegerLiteral ten = new IntegerLiteral(10);
		Assert.assertEquals(result, ten);
	}
	
	@Test
	public void testDefDecl() throws ParseException {
		String input = "val obj = new\n"
				     + "    val v : system.Int = 5\n"
				     + "    def m() : system.Int = 5\n"
				     + "obj.v\n"
				     ;
		ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input);
		// bogus "system" entry, but makes the text work for now
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
		Expression program = ast.generateIL(genCtx, null);
    	TypeContext ctx = TypeContext.empty();
		ValueType t = program.typeCheck(ctx);
		Assert.assertEquals(Util.intType(), t);
		Value v = program.interpret(EvalContext.empty());
    	IntegerLiteral five = new IntegerLiteral(5);
		Assert.assertEquals(five, v);
	}
	@Test
	public void testDefWithValInside() throws ParseException {
		String input = "def foo() : system.Int\n"
				     + "    val v : system.Int = 5\n"
				     + "    v\n"
				     + "foo()\n"
				     ;
		ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input);
		// bogus "system" entry, but makes the text work for now
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
		Expression program = ast.generateIL(genCtx, null);
    	TypeContext ctx = TypeContext.empty();
		ValueType t = program.typeCheck(ctx);
		Assert.assertEquals(Util.intType(), t);
		Value v = program.interpret(EvalContext.empty());
    	IntegerLiteral five = new IntegerLiteral(5);
		Assert.assertEquals(five, v);
	}

    @Test
	public void testIdentityCall() throws ParseException {
		String input = "val obj = new\n"
				     + "    def id(x:system.Int) : system.Int = x\n"
				     + "obj.id(5)\n"
				     ;
		ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input);
		// bogus "system" entry, but makes the text work for now
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
		Expression program = ast.generateIL(genCtx, null);
    	TypeContext ctx = TypeContext.empty();
		ValueType t = program.typeCheck(ctx);
		Assert.assertEquals(Util.intType(), t);
		Value v = program.interpret(EvalContext.empty());
    	IntegerLiteral five = new IntegerLiteral(5);
		Assert.assertEquals(five, v);
	}

	@Test
	public void testIdentityCallString() throws ParseException {
		String input = "val obj = new\n"
				     + "    def id(x:system.String) : system.String = x\n"
				     + "obj.id(\"five\")\n"
				     ;
		ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input);
		// bogus "system" entry, but makes the text work for now
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
		Expression program = ast.generateIL(genCtx, null);
    	TypeContext ctx = TypeContext.empty();
		ValueType t = program.typeCheck(ctx);
        Assert.assertEquals(Util.stringType(), t);
        Value v = program.interpret(EvalContext.empty());
        StringLiteral five = new StringLiteral("five");
		Assert.assertEquals(five, v);
	}

    @Test
	public void testIdentityCallString2() throws ParseException {
		String input = "val obj = new\n"
				     + "    def id(x:system.String) : system.String = x\n"
				     + "obj.id(\"five\")\n"
				     ;
		ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input);
		// bogus "system" entry, but makes the text work for now
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
		Expression program = ast.generateIL(genCtx, null);
    	TypeContext ctx = TypeContext.empty();
		ValueType t = program.typeCheck(ctx);
        Assert.assertEquals(Util.stringType(), t);
        Value v = program.interpret(EvalContext.empty());
        StringLiteral five = new StringLiteral("seven");
		Assert.assertNotEquals(five, v);
	}

	@Test
	public void testType() throws ParseException {
		String input = "type IntResult\n"
					 + "    def getResult():system.Int\n\n"
					 + "val r : IntResult = new\n"
					 + "    def getResult():system.Int = 5\n\n"
					 + "r.getResult()\n"
				     ;
		TypedAST ast = TestUtil.getNewAST(input);
		// bogus "system" entry, but makes the text work for now
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
		Expression program = ((Sequence) ast).generateIL(genCtx, null);
    	TypeContext ctx = TypeContext.empty();
		ValueType t = program.typeCheck(ctx);
		Value v = program.interpret(EvalContext.empty());
				
		Assert.assertEquals(Util.intType(), t);
		
    	IntegerLiteral five = new IntegerLiteral(5);
		Assert.assertEquals(five, v);
	}
	
	@Test()
	public void testBogusType() throws ParseException {
		try {
			String input = "val obj = new\n"
				     	 + "    def id(x:Foo) : Foo = x\n"
						 + "val i : Int = 5\n\n"
						 + "i\n"
				     	 ;
			TypedAST ast = TestUtil.getNewAST(input);
			// bogus "system" entry, but makes the text work for now
			GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
			Expression program = ((Sequence) ast).generateIL(genCtx, null);
			TypeContext ctx = TypeContext.empty();
			ValueType t = program.typeCheck(ctx);
			Assert.fail("typechecking should have failed");
		} catch (ToolError e) {
			Assert.assertEquals(2, e.getLine());
		}
	}
	
	@Test
	public void testTypeAbbrev() throws ParseException {
		String input = "type Int = system.Int\n\n"
					 + "val i : Int = 5\n\n"
					 + "i\n"
				     ;
		ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input);
		// bogus "system" entry, but makes the text work for now
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
		Expression program = ast.generateIL(genCtx, null);
    	TypeContext ctx = TypeContext.empty();
		ValueType t = program.typeCheck(ctx);
		Assert.assertEquals(Util.intType(), t);
		Value v = program.interpret(EvalContext.empty());
    	IntegerLiteral five = new IntegerLiteral(5);
		Assert.assertEquals(five, v);
	}
	
	@Test
	public void testSimpleDelegation() throws ParseException {
		String input = "type IntResult\n"
					 + "    def getResult():system.Int\n\n"
					 + "val r : IntResult = new\n"
					 + "    def getResult():system.Int = 5\n\n"
					 + "val r2 : IntResult = new\n"
					 + "    delegate IntResult to r\n\n"
					 + "r2.getResult()\n"
				     ;
		ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input);
		// bogus "system" entry, but makes the text work for now
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
		Expression program = ast.generateIL(genCtx, null);
    	TypeContext ctx = TypeContext.empty();
		ValueType t = program.typeCheck(ctx);
		Assert.assertEquals(Util.intType(), t);
		Value v = program.interpret(EvalContext.empty());
    	IntegerLiteral five = new IntegerLiteral(5);
		Assert.assertEquals(five, v);
	}
    
	@Test
	public void testSingleModule() throws ParseException {
		
		String source = TestUtil.readFile(PATH + "example.wyv");
		TypedAST ast = TestUtil.getNewAST(source);
		
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), new NominalType("", "system")).extend("D",  new Variable("D"), null);
		wyvern.target.corewyvernIL.decl.Declaration decl = ((Declaration) ast).topLevelGen(genCtx);
    	TypeContext ctx = TypeContext.empty().extend("D", null);
    	
		DeclType t = decl.typeCheck(ctx, ctx);
		wyvern.target.corewyvernIL.decl.Declaration declValue = decl.interpret(EvalContext.empty());
	}

	@Test
	public void testMultipleModules() throws ParseException {
		
		String[] fileList = {"A.wyt", "B.wyt", "D.wyt", "A.wyv", "D.wyv", "B.wyv", "main.wyv"};
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), new NominalType("", "system"));
		genCtx = new TypeGenContext("Int", "system", genCtx);
		
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
		Expression program = new FieldGet(mainProgram, "x"); 
		
    	TypeContext ctx = TypeContext.empty();
		ValueType t = program.typeCheck(ctx);
		Value v = program.interpret(EvalContext.empty());
    	IntegerLiteral three = new IntegerLiteral(3);
		Assert.assertEquals(three, v);
	}
	
	@Test
	public void testRecursiveMethod() throws ParseException {
		
		String source = TestUtil.readFile(PATH + "recursive.wyv");
		TypedAST ast = TestUtil.getNewAST(source);
		
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), new NominalType("", "system")).extend("D",  new Variable("D"), new NominalType("", "D"));
		wyvern.target.corewyvernIL.decl.Declaration decl = ((Declaration) ast).topLevelGen(genCtx);
    	TypeContext ctx = TypeContext.empty();
		DeclType t = decl.typeCheck(ctx, ctx);
		wyvern.target.corewyvernIL.decl.Declaration declValue = decl.interpret(EvalContext.empty());
	}
	
	
	
	@Test
	public void testRecursiveTypes() throws ParseException {
		
		String source = TestUtil.readFile(PATH + "recursivetypes.wyv");
		ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(source);

		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
		Expression program = ast.generateIL(genCtx, null);
    	TypeContext ctx = TypeContext.empty();
		ValueType t = program.typeCheck(ctx);
		Assert.assertEquals(Util.intType(), t);
		Value v = program.interpret(EvalContext.empty());
    	IntegerLiteral five = new IntegerLiteral(5);
		Assert.assertEquals(five, v);
	}
	
	@Test
	public void testInterpreterOnScript() {
		String[] args = new String[] { PATH + "recursivetypes.wyv" };
		Interpreter.main(args);
	}
	
	
	@Test
	public void testRecursiveFunctions() throws ParseException {
		
		String source = TestUtil.readFile(PATH + "recursivefunctions.wyv");
		ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(source);

		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
		Expression program = ast.generateIL(genCtx, null);
    	TypeContext ctx = TypeContext.empty();
		ValueType t = program.typeCheck(ctx);
		Assert.assertEquals(Util.intType(), t);
		Value v = program.interpret(EvalContext.empty());
    	IntegerLiteral five = new IntegerLiteral(5);
		Assert.assertEquals(five, v);
	}
	
	@Test
	public void testFib() throws ParseException {
		
        String source = TestUtil.readFile(PATH + "bool-nat-fib.wyv");
        ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(source);

        GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
        Expression program = ast.generateIL(genCtx, null);
        TypeContext ctx = TypeContext.empty();
        ValueType t = program.typeCheck(ctx);
        // Assert.assertEquals(Util.intType(), t);
        Value v = program.interpret(EvalContext.empty());
        //IntegerLiteral five = new IntegerLiteral(5);
        //Assert.assertEquals(five, v);
	}

	@Test
	public void testLambda() throws ParseException {
		
        String source = TestUtil.readFile(PATH + "lambdatest.wyv");
        ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(source);

        GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
        Expression program = ast.generateIL(genCtx, null);
        TypeContext ctx = TypeContext.empty();
        ValueType t = program.typeCheck(ctx);
        Assert.assertEquals(Util.intType(), t);
        Value v = program.interpret(EvalContext.empty());
        IntegerLiteral five = new IntegerLiteral(5);
        Assert.assertEquals(five, v);
	}

    @Test
    public void testSimpleLambda() throws ParseException {

        String source = "type UnitIntFn \n"
            + "     def apply():system.Int \n"
            + "val getFive:UnitIntFn = () => 5\n"
            + "getFive.apply()";

        ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(source);

        GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
        Expression program = ast.generateIL(genCtx, null);
        TypeContext ctx = TypeContext.empty();


        ValueType t = null;
        try {
            t = program.typeCheck(ctx);
        } catch(NullPointerException e) {
            e.printStackTrace(System.out);
            Assert.fail("Failed to typecheck. Null Pointer Exception");
        }

        Assert.assertEquals(Util.intType(), t);

        Value v = program.interpret(EvalContext.empty());
        IntegerLiteral five = new IntegerLiteral(5);
        Assert.assertEquals(five, v);
    }

    @Test
    public void testSimpleLambda2() throws ParseException {

        String source = "type UnitIntFn \n"
            + "     def apply():system.Int \n"
            + "val getFive:UnitIntFn = () => 5\n"
            + "getFive()";

        ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(source);

        GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
        Expression program = ast.generateIL(genCtx, null);
        TypeContext ctx = TypeContext.empty();


        ValueType t = null;
        try {
            t = program.typeCheck(ctx);
        } catch(NullPointerException e) {
            e.printStackTrace(System.out);
            Assert.fail("Failed to typecheck. Null Pointer Exception");
        }

        Assert.assertEquals(Util.intType(), t);

        Value v = program.interpret(EvalContext.empty());
        IntegerLiteral five = new IntegerLiteral(5);
        Assert.assertEquals(five, v);
    }

	@Test
	public void testJavaImportLibrary1() throws ReflectiveOperationException {
		FObject obj = wyvern.tools.interop.Default.importer().find("wyvern.tools.tests.ILTests.importTest");
		List<Object> args = new LinkedList<Object>();
		args.add(1);
		Object result = obj.invokeMethod("addOne", args);
		Assert.assertEquals(2, result);
	}
	
	@Test
	public void testJavaImportLibrary2() throws ReflectiveOperationException {
		FObject obj = wyvern.tools.interop.Default.importer().find("java.lang.System.out");
		List<Object> args = new LinkedList<Object>();
		args.add("Hello, world!");
		Object result = obj.invokeMethod("println", args);
	}
	
	@Test
	public void testJavaImport1() throws ParseException {
		String input = "resource module main\n\n"
//					 + "require ffi/java\n\n"
//					 + "import testcode/Adder\n\n"
//					 + "type Adder\n"
//					 + "    def addOne(i:system.Int):system.Int\n\n"
					 + "import java:wyvern.tools.tests.ILTests.importTest\n\n"
					 + "val x : Int = importTest.addOne(4)\n"
				     ;
		TypedAST ast = TestUtil.getNewAST(input);
		// bogus "system" entry, but makes the text work for now
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
		genCtx = new TypeGenContext("Int", "system", genCtx); // slightly weird
		wyvern.target.corewyvernIL.decl.Declaration decl = ((Declaration) ast).topLevelGen(genCtx);
		genCtx = GenUtil.link(genCtx, decl); // not sure this is necessary
		List<wyvern.target.corewyvernIL.decl.Declaration> decls = new LinkedList<wyvern.target.corewyvernIL.decl.Declaration>();
		decls.add(decl);
		Expression mainProgram = GenUtil.genExp(decls, genCtx);
		Expression program = new FieldGet(mainProgram, "x"); // slightly hacky		
    	TypeContext ctx = TypeContext.empty();
		ValueType t = program.typeCheck(ctx);
		Value v = program.interpret(EvalContext.empty());
		Assert.assertEquals(Util.intType(), t);		
    	IntegerLiteral five = new IntegerLiteral(5);
		Assert.assertEquals(five, v);
	}
	
	@Test
	public void testBigInt() throws ParseException {
        String source = TestUtil.readFile(PATH + "bigint.wyv");
		TypedAST ast = TestUtil.getNewAST(source);
		// bogus "system" entry, but makes the text work for now
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
		genCtx = new TypeGenContext("Int", "system", genCtx); // slightly weird
		wyvern.target.corewyvernIL.decl.Declaration decl = ((Declaration) ast).topLevelGen(genCtx);
		genCtx = GenUtil.link(genCtx, decl); // not sure this is necessary
		List<wyvern.target.corewyvernIL.decl.Declaration> decls = new LinkedList<wyvern.target.corewyvernIL.decl.Declaration>();
		decls.add(decl);
		Expression mainProgram = GenUtil.genExp(decls, genCtx);
		Expression program = new FieldGet(mainProgram, "x"); // slightly hacky		
    	TypeContext ctx = TypeContext.empty();
		ValueType t = program.typeCheck(ctx);
		Value v = program.interpret(EvalContext.empty());
		Value mainV = mainProgram.interpret(EvalContext.empty());
		Assert.assertEquals(Util.intType(), t);		
    	IntegerLiteral five = new IntegerLiteral(5);
		Assert.assertEquals(five, v);
	}
	
	@Test
	public void testOperatorPlus() throws ParseException {
        String source = TestUtil.readFile(PATH + "operator-plus.wyv");
		TypedAST ast = TestUtil.getNewAST(source);
		// bogus "system" entry, but makes the text work for now
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
		genCtx = new TypeGenContext("Int", "system", genCtx); // slightly weird
		wyvern.target.corewyvernIL.decl.Declaration decl = ((Declaration) ast).topLevelGen(genCtx);
		genCtx = GenUtil.link(genCtx, decl); // not sure this is necessary
		List<wyvern.target.corewyvernIL.decl.Declaration> decls = new LinkedList<wyvern.target.corewyvernIL.decl.Declaration>();
		decls.add(decl);
		Expression mainProgram = GenUtil.genExp(decls, genCtx);
		Expression program = new FieldGet(mainProgram, "x"); // slightly hacky		
    	TypeContext ctx = TypeContext.empty();
		ValueType t = program.typeCheck(ctx);
		Value v = program.interpret(EvalContext.empty());
		Value mainV = mainProgram.interpret(EvalContext.empty());
		Assert.assertEquals(Util.intType(), t);		
    	IntegerLiteral five = new IntegerLiteral(5);
		Assert.assertEquals(five, v);
	}
	
	@Test
	public void testJavaImport2() throws ParseException {
		String input = "resource module main\n\n"
//					 + "require ffi/java\n\n"
//					 + "import testcode/Adder\n\n"
//					 + "type Adder\n"
//					 + "    def addOne(i:system.Int):system.Int\n\n"
					 + "import java:wyvern.tools.tests.ILTests.importTest\n\n"
					 + "val x : system.String = importTest.addOneString(\"4\")\n"
				     ;
		TypedAST ast = TestUtil.getNewAST(input);
		// bogus "system" entry, but makes the text work for now
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
		genCtx = new TypeGenContext("Int", "system", genCtx); // slightly weird
		wyvern.target.corewyvernIL.decl.Declaration decl = ((Declaration) ast).topLevelGen(genCtx);
		genCtx = GenUtil.link(genCtx, decl); // not sure this is necessary
		List<wyvern.target.corewyvernIL.decl.Declaration> decls = new LinkedList<wyvern.target.corewyvernIL.decl.Declaration>();
		decls.add(decl);
		Expression mainProgram = GenUtil.genExp(decls, genCtx);
		Expression program = new FieldGet(mainProgram, "x"); // slightly hacky		
    	TypeContext ctx = TypeContext.empty();
		ValueType t = program.typeCheck(ctx);
		Assert.assertEquals(Util.stringType(), t);
		Value v = program.interpret(EvalContext.empty());
    	StringLiteral five = new StringLiteral("5");
		Assert.assertEquals(five, v);
	}
	
	@Test
	@Category(CurrentlyBroken.class)
	public void testResourceTypechecking() throws ParseException {
		try {
			String input = "type Constant\n"
						 + "    def getConstant() : system.Int\n"
						 + "val c : Constant = new\n"
						 + "	var anotherConstant : system.Int = 7\n"
						 + "	def getConstant () : system.Int\n"
						 + "		42\n"
						 + "c.getConstant()";
			ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input);
			GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
			Expression program = ast.generateIL(genCtx, null);
			ValueType type = program.typeCheck(TypeContext.empty());
			Assert.fail("Typechecking should have failed.");
		} catch (ToolError e) {
		}
	}


	public static ImportTestClass importTest = new ImportTestClass();
	public static class ImportTestClass {
		public int addOne(int i) {
			return i+1;
		}
		public String addOneString(String s) {
			return Integer.toString(Integer.parseInt(s)+1);
		}
	}
	
	public static IntLibrary intLibrary = new IntLibrary();
	public static class IntLibrary {
		public int add(int i, int j) {
			return i + j;
		}
		
		public int subtract(int i, int j) {
			return i - j;
		}
		
		public int multiply(int i, int j) {
			return i * j;
		}
		
		public int divide(int i, int j) {
			return i / j;
		}
	}

	/**
	 * Asserts that the given AST should not successfully typecheck and should throw 
	 * some kind of ToolError.
	 * @param ast: ast that should fail typechecking.
	 */
	private static void assertTypeCheckFails(ExpressionAST ast, GenContext genCtx) {
		try {
			Expression program = ast.generateIL(genCtx, null);
			
			// not quite right, but works for now
			// TODO: replace this with a standard prelude
			program.typeCheck(TypeContext.empty().extend("system", Util.unitType()));
			Assert.fail("A type error should have been reported.");
		} catch (ToolError toolError) {}
	}
	
}
