package wyvern.tools.tests;

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
import wyvern.target.corewyvernIL.expression.Bind;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.expression.StringLiteral;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.InterpreterState;
import wyvern.target.corewyvernIL.support.TypeContext;
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
import wyvern.tools.tests.TestUtil;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;

@Category(RegressionTests.class)
public class ILTests {
    	
	private static final String PATH = TestUtil.BASE_PATH;
	
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
        TestUtil.doTestInt(input, 5);
    }

    @Test
    public void testLetValWithString() throws ParseException {
        String input =
                  "val x = \"five\"\n"
        		+ "x\n";
        TestUtil.doTest(input, Util.stringType(), new StringLiteral("five"));
    }

    @Test
    public void testLetValWithString3() throws ParseException {
        String input = "val identity = (x: system.Int) => x\n"
                     + "identity(5)";
        TestUtil.doTestInt(input, 5);
    }

    @Test(expected=ToolError.class)
    public void testLetValWithString4() throws ParseException {
        String input = "val identity = (x: system.Int) => x\n"
                     + "   identity(5)";
        ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input, "test input");
        IExpr program = ast.generateIL(Globals.getStandardGenContext(), null, null);
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
        TestUtil.doTestInt(input, 5);
	}
	
	@Test
	public void testVarFieldRead() throws ParseException {
		String input = "val obj = new\n"
				     + "    var v : system.Int = 5\n"
				     + "obj.v\n"
				     ;
		TestUtil.doTest(input, Util.intType(), new IntegerLiteral(5));
	}
	
	@Test
	public void testVarFieldReadFromNonExistent() throws ParseException {
		String input = "val obj = new\n"
					 + "    var v : system.Int = 5\n"
					 + "obj.x\n";
		ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input, "test input");
		assertTypeCheckFails(ast, Globals.getStandardGenContext());
	}
	
	@Test
	public void testVarFieldWrite() throws ParseException {
		String input = "val obj = new\n"
				     + "    var v : system.Int = 2\n"
				     + "obj.v = 5\n"
				     + "obj.v\n"
				     ;
		TestUtil.doTestInt(input, 5);
	}
	
	@Test
	public void testVarFieldWriteToWrongType() throws ParseException {
		String input = "val obj = new\n"
					 + "    var v : system.Int = 3\n"
					 + "obj.v = \"hello\"\n";
		ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input, "test input");
		assertTypeCheckFails(ast, Globals.getStandardGenContext());
	}
	
	@Test
	public void testVarFieldWriteToNonExistent () throws ParseException {
		String input = "val obj = new\n"
					 + "    var v : system.Int = 3\n"
					 + "obj.x = 5\n";
		ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input, "test input");
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
        TestUtil.doTestInt(input, 10);
	}
	
	@Test
	public void testWriteToValField () throws ParseException {
		String input = "val object = new \n"
					 + "    val field : system.Int = 5 \n"
					 + "object.field = 10\n";
		TestUtil.doTestTypeFail(input);
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
		TestUtil.doTestInt(input, 10);
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
		ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input, "test input");
	}
	
	@Test
	public void testDefDecl() throws ParseException {
		String input = "val obj = new\n"
				     + "    val v : system.Int = 5\n"
				     + "    def m() : system.Int = 5\n"
				     + "obj.v\n"
				     ;
        TestUtil.doTestInt(input, 5);
	}
	@Test
	public void testDefWithValInside() throws ParseException {
		String input = "def foo() : system.Int\n"
				     + "    val v : system.Int = 5\n"
				     + "    v\n"
				     + "foo()\n"
				     ;
        TestUtil.doTestInt(input, 5);
	}
	
	// TODO: add cast checks to make Dyn sound, and wrappers to make it capability-safe
	@Test
	public void testDyn() throws ParseException {
		String input = "val v : Dyn = 5\n"
				     + "val v2 : system.Int = v\n"
				     + "v2\n"
				     ;
		TestUtil.doTest(input, Util.intType(), new IntegerLiteral(5));
	}
	
	@Test
	public void testArrowSugar2() throws ParseException {
		String input = "val id : Int -> Int = (x:Int) => x\n"
				     + "def invoke(f:Int -> Int, x:Int) : Int = f(x)\n"
				     + "invoke(id, 5)\n"
				     ;
		TestUtil.doTest(input, Util.intType(), new IntegerLiteral(5));
	}
	
	@Test
	public void testDefWithVarInside() throws ParseException {
		String input = "def foo() : system.Int\n"
					 + "    var v : system.Int = 5\n"
					 + "    v = 10\n"
					 + "    v\n"
					 + "foo()\n";
		TestUtil.doTest(input, Util.intType(), new IntegerLiteral(10));
	}
	
    @Test
	public void testIdentityCall() throws ParseException {
		String input = "val obj = new\n"
				     + "    def id(x:system.Int) : system.Int = x\n"
				     + "obj.id(5)\n";
        TestUtil.doTestInt(input, 5);
	}

	@Test
	public void testIdentityCallString() throws ParseException {
		String input = "val obj = new\n"
				     + "    def id(x:system.String) : system.String = x\n"
				     + "obj.id(\"five\")\n"
				     ;
        TestUtil.doTest(input, Util.stringType(), new StringLiteral("five"));
	}

	@Test
	public void testType() throws ParseException {
		String input = "type IntResult\n"
					 + "    def getResult():system.Int\n\n"
					 + "val r : IntResult = new\n"
					 + "    def getResult():system.Int = 5\n\n"
					 + "r.getResult()\n"
				     ;
		TestUtil.doTestInt(input, 5);
	}
	
	@Test()
	public void testBogusType() throws ParseException {
		try {
			String input = "val obj = new\n"
				     	 + "    def id(x:Foo) : Foo = x\n"
						 + "val i : Int = 5\n\n"
						 + "i\n"
				     	 ;
			TypedAST ast = TestUtil.getNewAST(input, "test input");
			// bogus "system" entry, but makes the text work for now
			GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
			IExpr program = ((Sequence) ast).generateIL(genCtx, null, null);
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
        TestUtil.doTest(input, null, new IntegerLiteral(5));
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
        TestUtil.doTestInt(input, 5);
	}
    
	@Test
	public void testSimpleParameterization() throws ParseException {
		TestUtil.doTestScriptModularly("modules.pclient", Util.intType(), new IntegerLiteral(5));
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
		
		String[] fileList = {"One.wyt", "Two.wyt", "Three.wyt", "one.wyv", "two.wyv", "three.wyv", "main.wyv"};
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
		TestUtil.doTestScriptModularly("modules.module.recursive", null, null);
	}
	
	
	
	@Test
	public void testRecursiveTypes() throws ParseException {
		TestUtil.doTestScriptModularly("modules.module.recursivetypes", null, null);
	}
	
	@Test
	public void testInterpreterOnScript() {
		String[] args = new String[] { TestUtil.EXAMPLES_PATH + "rosetta/hello.wyv" };
		Interpreter.wyvernHome.set("..");
		Interpreter.main(args);
	}
	
	
	@Test
	public void testRecursiveFunctions() throws ParseException {
		TestUtil.doTestScriptModularly("modules.module.recursivefunctions", Util.intType(), new IntegerLiteral(5));		
	}
	
	@Test
    @Category(CurrentlyBroken.class)
	public void testFact() throws ParseException {
		TestUtil.doTestScriptModularly("modules.module.bool-nat-fact", null, null);
		
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
		TestUtil.doTestScriptModularly("modules.module.lambdatest", Util.intType(), new IntegerLiteral(5));
	}

    @Test
    public void testSimpleLambda() throws ParseException {

        String input = "type UnitIntFn \n"
            + "     def apply():system.Int \n"
            + "val getFive:UnitIntFn = () => 5\n"
            + "getFive.apply()";

        TestUtil.doTestInt(input, 5);
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

        TestUtil.doTestInt(input, 5);
    }

    @Test
    public void testLambdaInferredInValDeclaration() throws ParseException {

        String source = "type IntIntFn \n"
            + "     def apply(x:system.Int):system.Int \n"
            + "val getFive:IntIntFn = x => x\n"
            + "getFive(5)";
        
        TestUtil.doTestInt(source, 5);
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
        TestUtil.doTestInt(source, 5);
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

    	 TestUtil.doTestInt(source, 5);
    }
    
    @Test
    @Category(CurrentlyBroken.class)
    public void testLambdaInferredInVarDeclaration() throws ParseException {

    	 String source = "type IntIntFn \n"
    	            + "     def apply(x:system.Int):system.Int \n"
    	            + "var t:IntIntFn = x=>x\n"
    	            + "t(5)";
    	 
    	 TestUtil.doTestInt(source, 5);
    }
    
    @Test
    @Category(CurrentlyBroken.class)
    public void testLambdaInferredInAssignment() throws ParseException {

    	 String source = "type IntIntFn \n"
    	            + "     def apply(x:system.Int):system.Int \n"
    	            + "var t:IntIntFn = x=>x\n"
    	            + "t = x=>5\n"
    	            + "t(4)";

    	TestUtil.doTestInt(source, 5);
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
		TestUtil.doTestInt(input, 5);
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
		String input = "module def main(java : Java)\n\n"
//					 + "import testcode/Adder\n\n"
//					 + "type Adder\n"
//					 + "    def addOne(i:system.Int):system.Int\n\n"
					 + "import java:wyvern.tools.tests.ILTests.importTest\n\n"
					 + "val x : Int = importTest.addOne(4)\n"
				     ;
		TestUtil.doTestModule(input, "x", Util.intType(), new IntegerLiteral(5));
	}

	@Test
	public void testBigInt() throws ParseException {
		TestUtil.doTestScriptModularly("modules.module.bigint", Util.intType(), new IntegerLiteral(5));
	}
	
	@Test
	public void testBool() throws ParseException {
		TestUtil.doTestScriptModularly("modules.module.bool", Util.intType(), new IntegerLiteral(5));
	}
	
	@Test
	public void testListModularly() throws ParseException {
		TestUtil.doTestScriptModularly("modules.module.list", Util.intType(), new IntegerLiteral(5));
	}
	
	@Test
	public void testListClient() throws ParseException {
		TestUtil.doTestScriptModularly("modules.module.listClient", Util.intType(), new IntegerLiteral(5));
	}	

	@Test
	public void testOperatorPlus() throws ParseException {
		TestUtil.doTestScriptModularly("modules.module.operator-plus", Util.intType(), new IntegerLiteral(5));
	}
	
	@Test
	public void testJavaImport2() throws ParseException {
		String input = "module def main(java : Java)\n\n"
//					 + "import testcode/Adder\n\n"
//					 + "type Adder\n"
//					 + "    def addOne(i:system.Int):system.Int\n\n"
					 + "import java:wyvern.tools.tests.ILTests.importTest\n\n"
					 + "val x : system.String = importTest.addOneString(\"4\")\n"
				     ;
		TestUtil.doTestModule(input, "x", Util.stringType(), new StringLiteral("5"));
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
		TestUtil.doTestTypeFail(input);
	}
	
	@Test
	public void testDSLParsing() throws ParseException {
        String input = "def id(x:Int):Int = x\n"
                     + "val n : Int = id(~)\n"
                     + "    4 5 +\n"
                     ;
		ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input, "test input");
		GenContext genCtx = Globals.getGenContext(new InterpreterState(InterpreterState.PLATFORM_JAVA, null, null));
		// IL generation doesn't work yet!
		//Expression program = ast.generateIL(genCtx, null);
	}

    @Test
    public void testTSL2() throws ParseException {
        TestUtil.doTestScriptModularly("tsls.identityClient", Util.intType(), new IntegerLiteral(5));
    }

    @Test
    public void testPostTSLIndentation() throws ParseException {
    	TestUtil.doTestScriptModularly("tsls.postTSLIndentation", Util.intType(), new IntegerLiteral(23));
    }
    
    // @Test
    // public void testTSLIndentation() throws ParseException {
    //     doTestScriptModularly("tsls.indentationTest", Util.intType(), new IntegerLiteral(5));
    // }
	
	// tests import-dependent types
	@Test
	public void testIDT() throws ParseException {
		TestUtil.doTestScriptModularly("modules.idtDriver", Util.intType(), new IntegerLiteral(3));
	}
	
	@Test
	public void testMetadataParsing() throws ParseException {
        String input = "type PostfixExpr\n"
                     + "    def eval():Int\n"
                     + "    metadata 3\n\n"
                     
                     ;
        TypedAST ast = TestUtil.getNewAST(input, "test input");
        GenContext genCtx = Globals.getGenContext(new InterpreterState(InterpreterState.PLATFORM_JAVA, null, null));
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
		TestUtil.doTestTypeFail(input);
	}

	@Test
	public void testWidthAndDepthSubtyping() throws ParseException {
		String input = "type SuperType\n"
					 + "    val x:Int\n"
					 + "type SubType\n"
					 + "    val x:Int\n"
					 + "    val y:Int\n"
					 + "//check width subtyping\n"
					 + "val aSub : SubType = new\n"
					 + "	val x : Int = 1\n"
					 + "	val y : Int = 2\n"
					 + "val aSuper : SuperType = aSub\n"
					 + "// types to use in depth subtyping\n"
					 + "type DepthSuperType\n"
					 + "    val m:SuperType\n"
					 + "type DepthSubType\n"
					 + "    val m:SubType\n"
					 + "val aDepthSub : DepthSubType = new\n"
					 + "	val m : SubType = aSub\n"
					 + "val aDepthSuper : DepthSuperType = aDepthSub\n"
					 + "";
        TestUtil.doTest(input, null, null);
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
		TestUtil.doTestTypeFail(input);
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
		TestUtil.doTestTypeFail(input);
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
		TestUtil.doTestTypeFail(input);
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
		TestUtil.doTestInt(input, 0);
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
			TestUtil.getNewAST(input, "test input");
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
		TestUtil.doTest(input, Util.intType(), new IntegerLiteral(1));
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
			IExpr program = ast.generateIL(genCtx, null, new LinkedList<TypedModuleSpec>());
			
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
		TestUtil.doTest(source, null, new IntegerLiteral(10));
	}

	@Test
	public void testIntOps() throws ParseException {
        String source = ""
                + "val x : Int = 2\n"
                + "val y : Int = 4 / 2 - x * 2\n"
                + "y";		
		TestUtil.doTest(source, null, new IntegerLiteral(-2));
	}

    @Test
    public void testArrowSugar() throws ParseException {

        String source = "val identity: system.Int->system.Int = (x: system.Int) => x\n"
            + "identity(10)";
		TestUtil.doTestInt(source, 10);
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

        TestUtil.doTest(source, null, null);
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

        TestUtil.doTest(input, null, new IntegerLiteral(5));
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
        TestUtil.doTest(source, null, new IntegerLiteral(5));
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

        TestUtil.doTest(source, null, new IntegerLiteral(10));
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

        TestUtil.doTest(source, null, new IntegerLiteral(5));
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

        TestUtil.doTest(source, null, new IntegerLiteral(10));
    }

    @Test
    public void testSelfName() throws ParseException {
        String source = ""
          + "type Body (body) => \n"
          + "    val x: system.Int \n\n"

          + "val b: Body = new (body) => \n"
          + "    val x: system.Int = 3 \n\n"

          + "b.x \n";

        TestUtil.doTest(source, null, new IntegerLiteral(3));
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

        TestUtil.doTest(source, null, new IntegerLiteral(3));
    }

    // TODO: the "right fix" for this bug is to allow an expression at the top level to refer
    // to the type of a variable in scope.  In other words, top-level expressions shouldn't
    // be turned into let expressions whose type must be interpretable without the bindings
    // above.
    @Test
    @Category(CurrentlyBroken.class)
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
        TestUtil.doTest(source, null, new IntegerLiteral(7));
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

            + "val body1 = new (body) => \n"
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
            + "    def iff(thnFn: Body, elsFn: thnFn.ThisType) : thnFn.T \n\n"

            + "val trueCase: BooleanFn = new \n"
            + "    def iff(thenFn: Body, elseFn: thenFn.ThisType): thenFn.T \n"
            + "        thenFn.apply()\n\n"

            + "def iff(b: BooleanFn, thenFn: Body, elseFn: thenFn.ThisType): thenFn.T \n"
            + "    b.iff(thenFn, elseFn) \n\n"

            + "iff(trueCase, body1, body2) \n\n"
            + "";

        TestUtil.doTest(source, null, new IntegerLiteral(5));
    }

    @Test
    public void testExplicitParameterization() throws ParseException {

        String source = ""
                      + "def identity[K](value: K): K \n"
                      + "    value \n\n"

                      + "val x = 15 \n"
                      + "identity[Int](x) \n"
                      + "";

        TestUtil.doTest(source, null, new IntegerLiteral(15));
    }

    @Test
    public void testTypeParameterization() throws ParseException {

        String source = ""
                      + "type Box\n"
                      + "    type T\n"
                      + "    val contents: this.T\n\n"

                      + "type IntBoxDecl\n"
                      + "    type T=Int\n"
                      + "    val contents: this.T\n\n"
                      
                      + "type IntBox = Box[Int]\n"
                      
                      + "val intBox:IntBox = new\n"
                      + "    type T=Int\n"
                      + "    val contents: this.T = 5\n\n"
                      
                      + "val b:Box = intBox \n"
                      + "intBox.contents \n"
                      + "";

        TestUtil.doTest(source, null, new IntegerLiteral(5));
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

        TestUtil.doTest(source, null, new IntegerLiteral(20));
    }

    @Test
    public void testParameterizationInferred() throws ParseException {

        String source = ""
                      + "def identity[K](value: K): K \n"
                      + "    value \n\n"

                      + "val x = 15 \n"
                      + "identity(x) \n"
                      + "";

        TestUtil.doTest(source, null, new IntegerLiteral(15));
    }

    @Test
    public void testInferredTupleParams() throws ParseException {

        String source = ""
                      + "def identity[K](value: Int, ignored: K): Int \n"
                      + "    value \n\n"

                      + "val x = 15 \n"
                      + "val y = 20 \n"
                      + "identity(x, y) \n"
                      + "";

        TestUtil.doTest(source, null, new IntegerLiteral(15));
    }

    @Test
    public void testJavaImportNamespace() throws ParseException {
        String source = "require java\n"
                      + "import java:java.util.ArrayList \n\n"

                      + "type Foo \n"
                      + "    val x: ArrayList \n\n"

                      + "val y = 7 \n"
                      + "y";

        TestUtil.doTest(source, null, new IntegerLiteral(7));
    }
    
    
    @Test
    public void testDeclarationReturn1() throws ParseException {
    	String src = "val something : Dyn = 5\n"
    			      + "val five : Int = something";
    	TestUtil.doTest(src, Util.unitType(), Util.unitValue());
    }
    
    @Test
    public void testDeclarationReturn2() throws ParseException {
    	String src = "val something : Dyn = 5\n"
    			   + "val five : Int = something\n"
    			   + "five";
    	TestUtil.doTest(src, Util.intType(), new IntegerLiteral(5));
    }
    
    @Test
    public void testDynamicObjectMethods() throws ParseException {
    	
    	String src = "val obj : Dyn = new\n"
    			   + "    def method(): Int = 5\n"
    			   + "obj.method()";
    	TestUtil.doTest(src, Util.dynType(), new IntegerLiteral(5));
    	
    }
    
    @Test
    public void testDynamicObjectValField() throws ParseException {
    	String src = "val obj: Dyn = new\n"
    			   + "    val field: Int = 5\n"
    			   + "obj.field";
    	TestUtil.doTest(src, Util.dynType(), new IntegerLiteral(5));
    }
    
    @Test
    public void testDynamicObjectVarField() throws ParseException {
    	String src = "val obj: Dyn = new\n"
    			   + "    var field: Int = 5\n"
    			   + "obj.field";
    	TestUtil.doTest(src, Util.dynType(), new IntegerLiteral(5));
    }
    
    @Test
    public void testDynamicObjectVarFieldWithUpdate() throws ParseException {
    	String src = "val obj: Dyn = new\n"
    			   + "    var field: Int = 5\n"
    			   + "obj.field = 10\n"
    			   + "obj.field";
    	TestUtil.doTest(src, Util.dynType(), new IntegerLiteral(10));
    }
    
    @Test
    public void testDynamicObjectMethodWithArgs() throws ParseException {
    	String src = "val obj: Dyn = new\n"
    			   + "    def method(x: Int): Int = x\n"
    			   + "obj.method(5)";
    	TestUtil.doTest(src, Util.dynType(), new IntegerLiteral(5));
    }
    
    @Test
    public void testListLength() throws ParseException {
        String src
            = "import wyvern.collections.list\n"
            + "val x : list.List = list.make()\n"
            + "x.append(1)\n"
            + "x.append(2)\n"
            + "x.length()\n";
        TestUtil.doTest(src, Util.dynType(), new IntegerLiteral(2));
    }

    @Test
    public void testListGet() throws ParseException {
        String src
            = "import wyvern.collections.list\n"
            + "val x : list.List = list.make()\n"
            + "x.append(1)\n"
            + "x.append(2)\n"
            + "x.get(0).getOrElse(() => -1)\n";
        TestUtil.doTest(src, Util.dynType(), new IntegerLiteral(1));
    }
}
