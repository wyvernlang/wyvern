package wyvern.tools.tests;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.VarBinding;
import wyvern.target.corewyvernIL.decl.DefDeclaration;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.Bind;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.FieldGet;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.expression.StringLiteral;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.modules.Module;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.EmptyGenContext;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.GenUtil;
import wyvern.target.corewyvernIL.support.InterpreterState;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.TypeGenContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.Interpreter;
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
	public void testLet() {
		NominalType Int = new NominalType("system", "Int");
		IntegerLiteral five = new IntegerLiteral(5);
		Expression letExpr = new Let(new VarBinding("x", Int, five), new Variable("x"));
		ValueType t = letExpr.typeCheck(Globals.getStandardTypeContext());
		Assert.assertEquals(Int, t);
		Value v = letExpr.interpret(EvalContext.empty());
		Assert.assertEquals(five, v);
	}

	@Test
	public void testLetOutside() {
		IntegerLiteral six = new IntegerLiteral(6);
		Expression letExpr = new Let(new VarBinding("x", Util.intType(), new IntegerLiteral(5)), new Variable("y"));
		ValueType t = letExpr.typeCheck(Globals.getStandardTypeContext().extend("y", Util.intType()));
		Assert.assertEquals(Util.intType(), t);
		Value v = letExpr.interpret(EvalContext.empty().extend("y", six));
		Assert.assertEquals(six, v);
	}

	@Test
	public void testBind() {
		NominalType Int = new NominalType("system", "Int");
		IntegerLiteral five = new IntegerLiteral(5);
		Expression bindExpr = new Bind(
				new ArrayList<VarBinding>(Arrays.asList(new VarBinding("x", Int, five))),
				new Variable("x"));
		ValueType t = bindExpr.typeCheck(Globals.getStandardTypeContext());
		Assert.assertEquals(Int, t);
		Value v = bindExpr.interpret(EvalContext.empty());
		Assert.assertEquals(five, v);
	}

	@Test
	public void testMultiVarBind() {
		NominalType Int = new NominalType("system", "Int");
		Expression bindExpr = new Bind(new ArrayList<VarBinding>(Arrays.asList(
				new VarBinding("x", Int, new IntegerLiteral(1)),
				new VarBinding("y", Int, new IntegerLiteral(2)),
				new VarBinding("z", Int, new IntegerLiteral(3)))),
				new Variable("y"));
		ValueType t = bindExpr.typeCheck(TypeContext.empty());
		Assert.assertEquals(Int, t);
		Value v = bindExpr.interpret(EvalContext.empty());
		Assert.assertEquals(new IntegerLiteral(2), v);
	}

	@Test
	public void testBindOutside() {
		NominalType Int = new NominalType("system", "Int");
		Expression bindExpr = new Bind(
				new ArrayList<VarBinding>(Arrays.asList(new VarBinding("x", Int, new IntegerLiteral(5)))),
				new Variable("y"));
		try {
			bindExpr.typeCheck(Globals.getStandardTypeContext().extend("y", Int));
			Assert.fail("Typechecking should have failed.");
		} catch (RuntimeException e) {
		}
	}

	@Test
    public void testLetValWithParse() throws ParseException {
        String input =
                  "val x = 5\n"
        		+ "x\n";
        doTestInt(input, 5);
    }

    @Test
    public void testLetValWithString() throws ParseException {
        String input =
                  "val x = \"five\"\n"
        		+ "x\n";
        doTest(input, Util.stringType(), new StringLiteral("five"));
    }

    @Test
    public void testLetValWithString3() throws ParseException {
        String input = "val identity = (x: system.Int) => x\n"
                     + "identity(5)";
        doTestInt(input, 5);
    }

    @Test(expected=ToolError.class)
    public void testLetValWithString4() throws ParseException {
        String input = "val identity = (x: system.Int) => x\n"
                     + "   identity(5)";
        ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input);
        Expression program = ast.generateIL(Globals.getStandardGenContext(), null, null);
        TypeContext ctx = Globals.getStandardTypeContext();
        ValueType t = program.typeCheck(ctx);
        Assert.assertEquals(Util.intType(), t);
        Value v = program.interpret(EvalContext.empty());
        IntegerLiteral five = new IntegerLiteral(5);
		Assert.assertEquals(five, v);
    }

	@Test
	public void testFieldRead() throws ParseException {
		String input = "val obj = new\n"
				     + "    val v = 5\n"
				     + "obj.v\n"
				     ;
        doTestInt(input, 5);
	}
	
	@Test
	public void testVarFieldRead() throws ParseException {
		String input = "val obj = new\n"
				     + "    var v : system.Int = 5\n"
				     + "obj.v\n"
				     ;
		doTest(input, Util.intType(), new IntegerLiteral(5));
	}
	
	@Test
	public void testVarFieldReadFromNonExistent() throws ParseException {
		String input = "val obj = new\n"
					 + "    var v : system.Int = 5\n"
					 + "obj.x\n";
		ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input);
		assertTypeCheckFails(ast, Globals.getStandardGenContext());
	}
	
	@Test
	public void testVarFieldWrite() throws ParseException {
		String input = "val obj = new\n"
				     + "    var v : system.Int = 2\n"
				     + "obj.v = 5\n"
				     + "obj.v\n"
				     ;
		doTestInt(input, 5);
	}
	
	@Test
	public void testVarFieldWriteToWrongType() throws ParseException {
		String input = "val obj = new\n"
					 + "    var v : system.Int = 3\n"
					 + "obj.v = \"hello\"\n";
		ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input);
		assertTypeCheckFails(ast, Globals.getStandardGenContext());
	}
	
	@Test
	public void testVarFieldWriteToNonExistent () throws ParseException {
		String input = "val obj = new\n"
					 + "    var v : system.Int = 3\n"
					 + "obj.x = 5\n";
		ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input);
		assertTypeCheckFails(ast, Globals.getStandardGenContext());
	}
	
	@Test
	public void testWriteFieldtoOtherField () throws ParseException {
		// Need to declare a structural type T, then declare firstObj as var firstObj : T = new ...
		String input = "val firstObj = new\n"
					 + "    var a : system.Int = 5\n"
				     + "val secondObj = new\n"
				     + "    var b : system.Int = 10\n"
				     + "firstObj.a = secondObj.b\n"
				     + "firstObj.a";
        doTestInt(input, 10);
	}
	
	@Test
	public void testWriteToValField () throws ParseException {
		String input = "val object = new \n"
					 + "    val field : system.Int = 5 \n"
					 + "object.field = 10\n";
		doTestTypeFail(input);
	}

	@Test
	public void testTypeDeclarations () throws ParseException {
		// TODO: when we have a standard library defined with multiplication, make this really double the number!
		String input = "resource type Doubler\n"
					 + "    def double(argument : system.Int) : system.Int\n"
					 + "val d : Doubler = new\n"
					 + "	def double (argument : system.Int) : system.Int\n"
					 + "		argument\n"
					 + "d.double(10)";
		doTestInt(input, 10);
	}
	
	@Test
    @Category(CurrentlyBroken.class)
	public void testTaggedClassParsing() throws ParseException {
		String input = "class List\n\n"
					 + "class Nil extends List\n\n"
					 + "class Cons extends List\n\n"
					 + "    val element:Int\n"
					 + "    val next:List\n\n"
					 + "val c = new Cons\n"
					 + "    val element = 5\n"
					 + "    val next:List = new Nil\n\n"
					 + "c.element\n\n"
					 + "val l : List = t\n\n"
					 + "match(l)\n"
					 + "    case Nil => 0\n"
					 + "    case c:Cons => c.element\n"
					 ;
		ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input);
	}
	
	@Test
	public void testDefDecl() throws ParseException {
		String input = "val obj = new\n"
				     + "    val v : system.Int = 5\n"
				     + "    def m() : system.Int = 5\n"
				     + "obj.v\n"
				     ;
        doTestInt(input, 5);
	}
	@Test
	public void testDefWithValInside() throws ParseException {
		String input = "def foo() : system.Int\n"
				     + "    val v : system.Int = 5\n"
				     + "    v\n"
				     + "foo()\n"
				     ;
        doTestInt(input, 5);
	}
	
	// TODO: add cast checks to make Dyn sound, and wrappers to make it capability-safe
	@Test
	public void testDyn() throws ParseException {
		String input = "val v : Dyn = 5\n"
				     + "val v2 : system.Int = v\n"
				     + "v2\n"
				     ;
		doTest(input, Util.intType(), new IntegerLiteral(5));
	}
	
	@Test
	public void testArrowSugar2() throws ParseException {
		String input = "val id : Int -> Int = (x:Int) => x\n"
				     + "def invoke(f:Int -> Int, x:Int) : Int = f(x)\n"
				     + "invoke(id, 5)\n"
				     ;
		doTest(input, Util.intType(), new IntegerLiteral(5));
	}
	
	@Test
	public void testDefWithVarInside() throws ParseException {
		String input = "def foo() : system.Int\n"
					 + "    var v : system.Int = 5\n"
					 + "    v = 10\n"
					 + "    v\n"
					 + "foo()\n";
		doTest(input, Util.intType(), new IntegerLiteral(10));
	}
	
    @Test
	public void testIdentityCall() throws ParseException {
		String input = "val obj = new\n"
				     + "    def id(x:system.Int) : system.Int = x\n"
				     + "obj.id(5)\n";
        doTestInt(input, 5);
	}

	@Test
	public void testIdentityCallString() throws ParseException {
		String input = "val obj = new\n"
				     + "    def id(x:system.String) : system.String = x\n"
				     + "obj.id(\"five\")\n"
				     ;
        doTest(input, Util.stringType(), new StringLiteral("five"));
	}

	@Test
	public void testType() throws ParseException {
		String input = "type IntResult\n"
					 + "    def getResult():system.Int\n\n"
					 + "val r : IntResult = new\n"
					 + "    def getResult():system.Int = 5\n\n"
					 + "r.getResult()\n"
				     ;
		doTestInt(input, 5);
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
			Expression program = ((Sequence) ast).generateIL(genCtx, null, null);
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
        doTest(input, null, new IntegerLiteral(5));
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
        doTestInt(input, 5);
	}
    
	@Test
	public void testSimpleParameterization() throws ParseException {
		doTestScriptModularly("modules.pclient", Util.intType(), new IntegerLiteral(5));
	}
	
	/*@Test
	public void testSingleModule() throws ParseException {
		String source = TestUtil.readFile(PATH + "example.wyv");
		TypedAST ast = TestUtil.getNewAST(source);
		
		GenContext genCtx = Globals.getStandardGenContext().extend("D",  new Variable("D"), Util.unitType());//new EmptyGenContext(new InterpreterState(null, null)).extend("system", new Variable("system"), new NominalType("", "system")).extend("D",  new Variable("D"), Util.unitType());
		wyvern.target.corewyvernIL.decl.Declaration decl = ((Declaration) ast).topLevelGen(genCtx, null);
    	TypeContext ctx = Globals.getStandardTypeContext().extend("D", Util.unitType());
    	
		DeclType t = decl.typeCheck(ctx, ctx);
		wyvern.target.corewyvernIL.decl.Declaration declValue = decl.interpret(EvalContext.empty());
	}


	@Test
	public void testMultipleModules() throws ParseException {
		
		String[] fileList = {"A.wyt", "B.wyt", "D.wyt", "A.wyv", "D.wyv", "B.wyv", "main.wyv"};
		GenContext genCtx = Globals.getStandardGenContext();
		//genCtx = new TypeGenContext("Int", "system", genCtx);
		
		List<wyvern.target.corewyvernIL.decl.Declaration> decls = new LinkedList<wyvern.target.corewyvernIL.decl.Declaration>();
		
		for(String fileName : fileList) {
			
			System.out.println(fileName);
			String source = TestUtil.readFile(PATH + fileName);
			TypedAST ast = TestUtil.getNewAST(source);
			wyvern.target.corewyvernIL.decl.Declaration decl = ((Declaration) ast).topLevelGen(genCtx, null);
			decls.add(decl);
			genCtx = GenUtil.link(genCtx, decl);
		}
		
		Expression mainProgram = GenUtil.genExp(decls, genCtx);
		// after genExp the modules are transferred into an object. We need to evaluate one field of the main object
		Expression program = new FieldGet(mainProgram, "x", null); 
		
    	TypeContext ctx = Globals.getStandardTypeContext();
		ValueType t = program.typeCheck(ctx);
		Value v = program.interpret(EvalContext.empty());
    	IntegerLiteral three = new IntegerLiteral(3);
		Assert.assertEquals(three, v);
	}*/
	
	@Test
	public void testRecursiveMethod() throws ParseException {
		doTestScriptModularly("modules.module.recursive", null, null);
	}
	
	
	
	@Test
	public void testRecursiveTypes() throws ParseException {
		doTestScriptModularly("modules.module.recursivetypes", null, null);
	}
	
	@Test
	public void testInterpreterOnScript() {
		String[] args = new String[] { TestUtil.BASE_PATH + "rosetta/hello.wyv" };
		Interpreter.wyvernHome.set("..");
		Interpreter.main(args);
	}
	
	
	@Test
	public void testRecursiveFunctions() throws ParseException {
		doTestScriptModularly("modules.module.recursivefunctions", Util.intType(), new IntegerLiteral(5));		
	}
	
	@Test
    @Category(CurrentlyBroken.class)
	public void testFact() throws ParseException {
		doTestScriptModularly("modules.module.bool-nat-fact", null, null);
		
        /*String source = TestUtil.readFile(PATH + "bool-nat-fact.wyv");
        ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(source);
        InterpreterState state = new InterpreterState(new File(TestUtil.BASE_PATH));

        GenContext genCtx = TestUtil.getGenContext(state);
        Expression program = ast.generateIL(genCtx, null);
        TypeContext ctx = TestUtil.getStandardTypeContext();
        ValueType t = program.typeCheck(ctx);
        // Assert.assertEquals(Util.intType(), t);
        Value v = program.interpret(EvalContext.empty());
        //IntegerLiteral five = new IntegerLiteral(5);
        //Assert.assertEquals(five, v);*/
	}

	@Test
	public void testLambda() throws ParseException {
		doTestScriptModularly("modules.module.lambdatest", Util.intType(), new IntegerLiteral(5));
	}

    @Test
    public void testSimpleLambda() throws ParseException {

        String input = "type UnitIntFn \n"
            + "     def apply():system.Int \n"
            + "val getFive:UnitIntFn = () => 5\n"
            + "getFive.apply()";

        doTestInt(input, 5);
    }

    @Test
    /**
     * Checks to see if the .apply() sugar on lambda fns recognizes
     */
    public void testSimpleLambda2() throws ParseException {

        String input = "type UnitIntFn \n"
            + "     def apply():system.Int \n"
            + "val getFive:UnitIntFn = () => 5\n"
            + "getFive()";

        doTestInt(input, 5);
    }

    @Test
    public void testLambdaInferredInValDeclaration() throws ParseException {

        String source = "type IntIntFn \n"
            + "     def apply(x:system.Int):system.Int \n"
            + "val getFive:IntIntFn = x => x\n"
            + "getFive(5)";
        
        doTestInt(source, 5);
    }
    
    @Test
    public void testLambdaInferredInApplication() throws ParseException {

        String source = "type IntIntFn \n"
            + "     def apply(x:system.Int):system.Int \n"
            + "type UseLambda\n"
            + "    def runLambda(x:IntIntFn):system.Int\n"
            + "val t = new\n"
            + "    def runLambda(x:IntIntFn):system.Int\n"
            + "        x(5)\n" 
            + "t.runLambda(x=>x)";
        doTestInt(source, 5);
    }
    
    @Test
    public void testLambdaInferredInDefDeclarationReturnValue() throws ParseException {

    	 String source = "type IntIntFn \n"
    	            + "     def apply(x:system.Int):system.Int \n"
    	            + "def getLambda():IntIntFn\n"
    	            + "    val t:system.Int = 1\n"
    	            + "    x => x\n"
    	            + "val lambda = getLambda()\n"
    	            + "lambda(5)";

    	 doTestInt(source, 5);
    }
    
    @Test
    @Category(CurrentlyBroken.class)
    public void testLambdaInferredInVarDeclaration() throws ParseException {

    	 String source = "type IntIntFn \n"
    	            + "     def apply(x:system.Int):system.Int \n"
    	            + "var t:IntIntFn = x=>x\n"
    	            + "t(5)";
    	 
    	 doTestInt(source, 5);
    }
    
    @Test
    @Category(CurrentlyBroken.class)
    public void testLambdaInferredInAssignment() throws ParseException {

    	 String source = "type IntIntFn \n"
    	            + "     def apply(x:system.Int):system.Int \n"
    	            + "var t:IntIntFn = x=>x\n"
    	            + "t = x=>5\n"
    	            + "t(4)";

    	doTestInt(source, 5);
    }

	@Test
	public void testTypeMembers() throws ParseException {
		String input = "type Numeric\n"
                     + "    val n:Int\n\n"
				
                     + "type Cell\n"
                     + "    type T = Numeric\n"
                     + "    val element:this.T\n\n"

                     + "type Holder\n"
                     + "    type U = Cell\n\n"

                     + "val h:Holder = new\n"
                     + "    type U = Cell\n\n"
                     
                     + "val num:Numeric = new\n"
                     + "    val n:Int = 5\n\n"
                     
                     + "val c:h.U = new\n"
                     + "    type T = Numeric\n"
                     + "    val element:this.T = num\n\n"
                     
                     + "c.element.n"
				     ;
		doTestInt(input, 5);
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
					 + "require java\n\n"
//					 + "import testcode/Adder\n\n"
//					 + "type Adder\n"
//					 + "    def addOne(i:system.Int):system.Int\n\n"
					 + "import java:wyvern.tools.tests.ILTests.importTest\n\n"
					 + "val x : Int = importTest.addOne(4)\n"
				     ;
		doTestModule(input, "x", Util.intType(), new IntegerLiteral(5));
	}

	private void doTestModule(String input, String fieldName, ValueType expectedType, Value expectedValue) throws ParseException {
		TypedAST ast = TestUtil.getNewAST(input);
		GenContext genCtx = Globals.getGenContext(new InterpreterState(null, null));
		TypeContext ctx = Globals.getStandardTypeContext();
		wyvern.target.corewyvernIL.decl.Declaration decl = ((Declaration) ast).topLevelGen(genCtx, new LinkedList<TypedModuleSpec>());
		Expression mainProgram = ((DefDeclaration)decl).getBody();
		Expression program = new FieldGet(mainProgram, fieldName, null); // slightly hacky		
        doChecks(program, expectedType, expectedValue);
	}

	@Test
	public void testBigInt() throws ParseException {
		doTestScriptModularly("modules.module.bigint", Util.intType(), new IntegerLiteral(5));
	}
	
	@Test
	public void testBool() throws ParseException {
		doTestScript("Bool.wyv", Util.intType(), new IntegerLiteral(5));
	}
	
	@Test
	public void testList() throws ParseException {
		doTestScript("List.wyv", Util.intType(), new IntegerLiteral(5));
	}
	
	@Test
	public void testListModularly() throws ParseException {
		doTestScriptModularly("modules.module.List", Util.intType(), new IntegerLiteral(5));
	}
	
	@Test
	public void testListClient() throws ParseException {
		doTestScriptModularly("modules.module.ListClient", Util.intType(), new IntegerLiteral(5));
	}
	
	private void doTestScript(String fileName, ValueType expectedType, Value expectedValue) throws ParseException {
        String source = TestUtil.readFile(PATH + fileName);
        ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(source);
        InterpreterState state = new InterpreterState(new File(TestUtil.BASE_PATH), null);
		GenContext genCtx = Globals.getGenContext(state);
        Expression program = ast.generateIL(genCtx, null, new LinkedList<TypedModuleSpec>());
        doChecks(program, expectedType, expectedValue);
	}

	public static void doTestInt(String input, int expectedIntResult) throws ParseException {
		doTest(input, Util.intType(), new IntegerLiteral(expectedIntResult));
	}

	// TODO: make other string tests call this function
	private static void doTest(String input, ValueType expectedType, Value expectedResult) throws ParseException {
		ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input);
		GenContext genCtx = Globals.getGenContext(new InterpreterState(new File(TestUtil.BASE_PATH), new File(TestUtil.LIB_PATH)));
		final LinkedList<TypedModuleSpec> dependencies = new LinkedList<TypedModuleSpec>();
		IExpr program = ast.generateIL(genCtx, null, dependencies);
		program = genCtx.getInterpreterState().getResolver().wrap(program, dependencies);
        doChecks(program, expectedType, expectedResult);
	}

	// TODO: make other script tests call this function
	public static void doTestScriptModularly(String qualifiedName, ValueType expectedType, Value expectedValue) throws ParseException {
        InterpreterState state = new InterpreterState(new File(TestUtil.BASE_PATH), new File(TestUtil.LIB_PATH));
        final Module module = state.getResolver().resolveModule(qualifiedName);
		IExpr program = state.getResolver().wrap(module.getExpression(), module.getDependencies());
        doChecks(program, expectedType, expectedValue);
	}
	
	private static void doChecks(IExpr program, ValueType expectedType, Value expectedValue) {
        // resolveModule already typechecked, but we'll do it again to verify the type
		TypeContext ctx = Globals.getStandardTypeContext();
        ValueType t = program.typeCheck(ctx);
        if (expectedType != null)
        	Assert.assertEquals(expectedType, t);
        
        // check the result
        Value v = program.interpret(Globals.getStandardEvalContext());
        if (expectedValue != null)
        	Assert.assertEquals(expectedValue, v);
	}

	@Test
	public void testOperatorPlus() throws ParseException {
		doTestScriptModularly("modules.module.operator-plus", Util.intType(), new IntegerLiteral(5));
	}
	
	@Test
	public void testJavaImport2() throws ParseException {
		String input = "resource module main\n\n"
					 + "require java\n\n"
//					 + "import testcode/Adder\n\n"
//					 + "type Adder\n"
//					 + "    def addOne(i:system.Int):system.Int\n\n"
					 + "import java:wyvern.tools.tests.ILTests.importTest\n\n"
					 + "val x : system.String = importTest.addOneString(\"4\")\n"
				     ;
		doTestModule(input, "x", Util.stringType(), new StringLiteral("5"));
	}
	
	@Test
	public void testResourceTypecheckingVar() throws ParseException {
		String input = "type Constant\n"
				     + "    def getConstant() : system.Int\n"
				     + "val c : Constant = new\n"
				     + "	var anotherConstant : system.Int = 7\n"
				     + "	def getConstant() : system.Int\n"
				     + "		42\n"
				     + "c.getConstant()";
		doTestTypeFail(input);
	}

	private void doTestTypeFail(String input) throws ParseException {
		ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input);
		GenContext genCtx = Globals.getGenContext(new InterpreterState(null, null));
		try {
			Expression program = ast.generateIL(genCtx, null, new LinkedList<TypedModuleSpec>());
			program.typeCheck(Globals.getStandardTypeContext());
			Assert.fail("Typechecking should have failed.");
		} catch (ToolError e) {
		}
	}
	
	@Test
	public void testDSLParsing() throws ParseException {
        String input = "def id(x:Int):Int = x\n"
                     + "val n : Int = id(~)\n"
                     + "    4 5 +\n"
                     ;
		ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input);
		GenContext genCtx = Globals.getGenContext(new InterpreterState(null, null));
		// IL generation doesn't work yet!
		//Expression program = ast.generateIL(genCtx, null);
	}

	@Test
	public void testTSL() throws ParseException {
		doTestScriptModularly("tsls.postfixClient", Util.intType(), new IntegerLiteral(7));
	}
	
	// tests import-dependent types
	@Test
	public void testIDT() throws ParseException {
		doTestScriptModularly("modules.IDT3", Util.intType(), new IntegerLiteral(3));
	}
	
	@Test
	public void testMetadataParsing() throws ParseException {
        String input = "type PostfixExpr\n"
                     + "    def eval():Int\n"
                     + "    metadata 3\n\n"
                     
                     ;
        TypedAST ast = TestUtil.getNewAST(input);
		GenContext genCtx = Globals.getGenContext(new InterpreterState(null, null));
		// IL generation doesn't work yet!
		wyvern.target.corewyvernIL.decl.Declaration decl = ((Declaration) ast).topLevelGen(genCtx, null);
	}

	@Test
	public void testResourceTypecheckingDef() throws ParseException {
		String input = "resource type Resource\n"
					 + "	var state : system.Int\n"
					 + "type PseudoPure\n"
					 + "	def saveState() : system.Int\n"
					 + "var a : Resource = new\n"
					 + "	var state : system.Int = 43\n"
					 + "var b : PseudoPure = new\n"
					 + "	def saveState() : system.Int\n"
					 + "		var c : Resource = a\n"
					 + "		0\n"
					 + "b.saveState()";
		doTestTypeFail(input);
	}

	@Test
	public void testVarMarkedResource() throws ParseException {
		String input = "resource type MarkedResource\n"
					 + "	def foo() : system.Int\n"
					 + "type PseudoPure\n"
					 + "	def bar() : system.Int\n"
					 + "var a : MarkedResource = new\n"
					 + "	def foo() : system.Int\n"
					 + "		var x : system.Int = 43\n"
					 + "		x\n"
					 + "var b : PseudoPure = new\n"
					 + "	def bar() : system.Int\n"
					 + "		var c : MarkedResource = a\n"
					 + "		0\n"
					 + "b.bar()";
		doTestTypeFail(input);
	}

	@Test
	public void testValMarkedResource() throws ParseException {
		String input = "resource type MarkedResource\n"
				 + "	def foo() : system.Int\n"
				 + "type PseudoPure\n"
				 + "	def bar() : system.Int\n"
				 + "val a : MarkedResource = new\n"
				 + "	def foo() : system.Int\n"
				 + "		var x : system.Int = 43\n"
				 + "		x\n"
				 + "var b : PseudoPure = new\n"
				 + "	def bar() : system.Int\n"
				 + "		var c : MarkedResource = a\n"
				 + "		0\n"
				 + "b.bar()";
		doTestTypeFail(input);
	}

	@Test
	public void testPureVar() throws ParseException {
		String input = "type Pure1\n"
					 + "	def foo() : system.Int\n"
					 + "type Pure2\n"
					 + "	def bar() : system.Int\n"
					 + "var a : Pure1 = new\n"
					 + "	def foo() : system.Int\n"
					 + "		var x : system.Int = 43\n"
					 + "		x\n"
					 + "var b : Pure2 = new\n"
					 + "	def bar() : system.Int\n"
					 + "		var c : Pure1 = a\n"
					 + "		0\n"
					 + "b.bar()";
		doTestTypeFail(input);
	}

	@Test
	public void testPureVal() throws ParseException {
		String input = "type Pure1\n"
				 + "	def foo() : system.Int\n"
				 + "type Pure2\n"
				 + "	def bar() : system.Int\n"
				 + "val a : Pure1 = new\n"
				 + "	def foo() : system.Int\n"
				 + "		var x : system.Int = 43\n"
				 + "		x\n"
				 + "var b : Pure2 = new\n"
				 + "	def bar() : system.Int\n"
				 + "		var c : Pure1 = a\n"
				 + "		0\n"
				 + "b.bar()";
		doTestInt(input, 0);
	}

	@Test
	public void testResourcenessOfTypesWithVars() throws ParseException {
		String input = "type TwoVars\n"
					 + "	var one : system.Int\n"
					 + "	var two : system.Int\n"
					 + "var x : TwoVars = new\n"
					 + "	var one : system.Int = 1\n"
					 + "	var two : system.Int = 2\n"
					 + "x.one";
		try {
			TestUtil.getNewAST(input);
			Assert.fail("AST creation should have failed.");
		} catch (RuntimeException e) {
		}
	}

	@Test
	public void testVarsInTypes() throws ParseException {
		String input = "resource type TwoVars\n"
					 + "	var one : system.Int\n"
					 + "	var two : system.Int\n"
					 + "var x : TwoVars = new\n"
					 + "	var one : system.Int = 1\n"
					 + "	var two : system.Int = 2\n"
					 + "x.one";
		doTest(input, Util.intType(), new IntegerLiteral(1));
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
			Expression program = ast.generateIL(genCtx, null, new LinkedList<TypedModuleSpec>());
			
			// not quite right, but works for now
			// TODO: replace this with a standard prelude
			program.typeCheck(TypeContext.empty().extend("system", Util.unitType()));
			Assert.fail("A type error should have been reported.");
		} catch (ToolError toolError) {
			System.err.println(toolError);
		}
	}
	
	@Test
	public void testIntAdd() throws ParseException {
        String source = ""
                + "val x : Int = 5\n"
                + "val y : Int = x + 5\n"
                + "y";		
		doTest(source, null, new IntegerLiteral(10));
	}

	@Test
	public void testIntOps() throws ParseException {
        String source = ""
                + "val x : Int = 2\n"
                + "val y : Int = 4 / 2 - x * 2\n"
                + "y";		
		doTest(source, null, new IntegerLiteral(-2));
	}

    @Test
    public void testArrowSugar() throws ParseException {

        String source = "val identity: system.Int->system.Int = (x: system.Int) => x\n"
            + "identity(10)";
		doTestInt(source, 10);
    }
    
    @Test
    public void testTypeMemberInFunction() throws ParseException {

        String source = ""
                      + "type IntHolder\n"
                      + "    type heldType = system.Int\n"
                      + "    val element: this.heldType\n\n"

                      + "def Identity(holder: IntHolder) : IntHolder\n"
                      + "    holder\n\n"

                      + "val five: IntHolder = new\n"
                      + "    type heldType = system.Int\n"
                      + "    val element: this.heldType = 5\n\n"

                      + "Identity(five)";

        doTest(source, null, null);
    }


    @Test
    public void testDependentType() throws ParseException {

        String input = ""
                      + "type IntHolder\n"
                      + "    type heldType = system.Int\n"
                      + "    val element: this.heldType\n\n"

                      + "def Identity(holder: IntHolder) : holder.heldType\n"
                      + "    holder.element\n\n"

                      + "val five: IntHolder = new\n"
                      + "    type heldType = system.Int\n"
                      + "    val element: this.heldType = 5\n\n"

                      + "Identity(five)";

        doTest(input, null, new IntegerLiteral(5));
    }

    @Test
    public void testDependentType2() throws ParseException {

        String source = ""
                      + "type IntHolder\n"
                      + "    type heldType = system.Int\n"
                      + "    val element: this.heldType\n\n"

                      + "def Identity(holder: IntHolder, passedType: holder.heldType) : holder.heldType\n"
                      + "    holder.element\n\n"

                      + "val five: IntHolder = new\n"
                      + "    type heldType = system.Int\n"
                      + "    val element: this.heldType = 5\n\n"

                      + "Identity(five, 5)";
        doTest(source, null, new IntegerLiteral(5));
    }

    @Test
    public void testIfStatement() throws ParseException {

        String source = ""
                      + "type Body\n"
                      + "    type T = system.Int\n"
                      + "    def apply(): this.T \n\n"

                      + "type Boolean\n"
                      + "   def iff(thenFn: Body, elseFn: Body) : thenFn.T \n\n"

                      + "val True = new \n"
                      + "    def iff(thenFn: Body, elseFn: Body): thenFn.T \n\n"
                      + "        thenFn.apply()\n\n"

                      + "val False = new \n"
                      + "    def iff(thenFn: Body, elseFn: Body): thenFn.T \n"
                      + "        elseFn.apply()\n\n"

                      + "def ifSt(bool: Boolean, thenFn: Body, elseFn: Body): thenFn.T \n"
                      + "    bool.iff(thenFn, elseFn) \n\n"

                      + "val IntegerFive = new \n"
                      + "   type T = system.Int \n"
                      + "   def apply(): this.T \n"
                      + "       5 \n\n"

                      + "val IntegerTen = new \n"
                      + "   type T = system.Int \n"
                      + "   def apply(): this.T \n"
                      + "       10 \n\n"

                      + "ifSt(True, IntegerTen, IntegerFive)";

        doTest(source, null, new IntegerLiteral(10));
    }

    @Test
    public void testIfStatement2() throws ParseException {

        String source = ""
                      + "type Body\n"
                      + "    type T = system.Int\n"
                      + "    def apply(): this.T \n\n"

                      + "type Boolean\n"
                      + "   def iff(thenFn: Body, elseFn: Body) : thenFn.T \n\n"

                      + "val True = new \n"
                      + "    def iff(thenFn: Body, elseFn: Body): thenFn.T \n\n"
                      + "        thenFn.apply()\n\n"

                      + "val False = new \n"
                      + "    def iff(thenFn: Body, elseFn: Body): thenFn.T \n"
                      + "        elseFn.apply()\n\n"

                      + "def ifSt(bool: Boolean, thenFn: Body, elseFn: Body): thenFn.T \n"
                      + "    bool.iff(thenFn, elseFn) \n\n"

                      + "val IntegerFive = new \n"
                      + "   type T = system.Int \n"
                      + "   def apply(): this.T \n"
                      + "       5 \n\n"

                      + "val IntegerTen = new \n"
                      + "   type T = system.Int \n"
                      + "   def apply(): this.T \n"
                      + "       10 \n\n"

                      + "ifSt(False, IntegerTen, IntegerFive)";

        doTest(source, null, new IntegerLiteral(5));
    }

    @Test
    public void testAbstractTypeMember() throws ParseException {
        String source = ""
            + "type TypeHolder\n"
            + "    type T\n"
            + "    val thing: this.T\n"
            + "    def giveThing(): this.T\n\n"

            + "val intHolder = new \n"
            + "    type T = system.Int\n"
            + "    val thing: system.Int = 10\n"
            + "    def giveThing(): this.T\n"
            + "        this.thing\n\n"

            + "intHolder.giveThing()";

        doTest(source, null, new IntegerLiteral(10));
    }

    @Test
    public void testSelfName() throws ParseException {
        String source = ""
          + "type Body (body) => \n"
          + "    val x: system.Int \n\n"

          + "val b: Body = new (body) => \n"
          + "    val x: system.Int = 3 \n\n"

          + "b.x \n";

        doTest(source, null, new IntegerLiteral(3));
    }

    @Test
    public void testSelfName2() throws ParseException {
        String source = ""
          + "type Body (body) => \n"
          + "    type T = system.Int \n"
          + "    val x: body.T \n\n"

          + "val b: Body = new (body) => \n"
          + "    type T = system.Int \n"
          + "    val x: body.T = 3 \n\n"

          + "b.x \n";

        doTest(source, null, new IntegerLiteral(3));
    }

    @Test
    public void testNestedDecl() throws ParseException {

        String source = ""
          + "type Body (body) => \n"
          + "    type T \n"
          + "    type ThisType \n"
          + "        type T = body.T \n"
          + "        type ThisType = body.ThisType \n"
          + "        def apply(): body.T \n"
          + "    def apply(): body.T \n\n"

          + "val body1: Body = new (body) => \n"
          + "    type T = system.Int \n"
          + "    type ThisType \n"
          + "        type T = body.T \n"
          + "        type ThisType = body.ThisType \n"
          + "        def apply(): body.T \n"
          + "    def apply(): body.T \n"
          + "        7 \n\n"

          + "body1.apply() \n"
          + "";
        doTest(source, null, new IntegerLiteral(7));
    }

    @Test
    public void testGenericIfStatement() throws ParseException {

        String source = ""
            + "type Body (body) => \n"
            + "    type T \n"
            + "    type ThisType \n"
            + "        type T = body.T \n"
            + "        type ThisType = body.ThisType \n"
            + "        def apply():body.T \n"
            + "    def apply():body.T \n\n"

            + "val body1: Body = new (body) => \n"
            + "    type T = Int \n"
            + "    type ThisType \n"
            + "        type T = body.T \n"
            + "        type ThisType = body.ThisType \n"
            + "        def apply():body.T \n"
            + "    def apply():body.T \n"
            + "        5 \n\n"

            + "val body2:body1.ThisType = new \n"
            + "    type T = body1.T \n"
            + "    type ThisType \n"
            + "        type T = body1.T \n"
            + "        type ThisType = body1.ThisType \n"
            + "        def apply():body1.T \n"
            + "    def apply():body1.T \n"
            + "        7 \n\n"

            + "type BooleanFn\n"
            + "    def iff(thenFn: Body, elseFn: thenFn.ThisType) : thenFn.T \n\n"

            + "val trueCase: BooleanFn = new \n"
            + "    def iff(thenFn: Body, elseFn: thenFn.ThisType): thenFn.T \n"
            + "        thenFn.apply()\n\n"

            + "def iff(b: BooleanFn, thenFn: Body, elseFn: thenFn.ThisType): thenFn.T \n"
            + "    b.iff(thenFn, elseFn) \n\n"

            + "iff(trueCase, body1, body2) \n\n"
            + "";

        doTest(source, null, new IntegerLiteral(5));
    }

    @Test
    public void testExplicitParameterization() throws ParseException {

        String source = ""
                      + "def identity[K](value: K): K \n"
                      + "    value \n\n"

                      + "val x = 15 \n"
                      + "identity[Int](x) \n"
                      + "";

        doTest(source, null, new IntegerLiteral(15));
    }

    @Test
    public void testExplicitTwoParams() throws ParseException {

        String source = ""
                      + "def identity[K, L](value: K, value2: L): L \n"
                      + "    val a: K = value \n"
                      + "    val b: L = value2 \n"
                      + "    value2 \n\n"

                      + "val x = 15 \n"
                      + "val y = 20 \n"
                      + "identity[Int, Int](x, y) \n"
                      + "";

        doTest(source, null, new IntegerLiteral(20));
    }

    @Test
    @Category(CurrentlyBroken.class)
    public void testParameterizationInferred() throws ParseException {

        String source = ""
                      + "def identity[K](value: K): K \n"
                      + "    value \n\n"

                      + "val x = 15 \n"
                      + "identity(x) \n"
                      + "";

        doTest(source, null, new IntegerLiteral(15));
    }

    @Test
    public void testJavaImportNamespace() throws ParseException {
        String source = "require java\n"
                      + "import java:java.util.ArrayList \n\n"

                      + "type Foo \n"
                      + "    val x: ArrayList \n\n"

                      + "val y = 7 \n"
                      + "";

        doTest(source, null, new IntegerLiteral(7));
    }
    
    @Test
    public void testTopLevelAnnotations1() throws ParseException {
    	this.doTestScript("TopLevelAnnotations1.wyv", Util.intType(), new IntegerLiteral(5));
    }
    
    @Test
    public void testTopLevelAnnotations2() throws ParseException {
    	this.doTestScript("TopLevelAnnotations2.wyv", Util.intType(), new IntegerLiteral(5));
    }
    
}
