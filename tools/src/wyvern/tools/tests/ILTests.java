package wyvern.tools.tests;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.decl.DefDeclaration;
import wyvern.target.corewyvernIL.decl.TypeDeclaration;
import wyvern.target.corewyvernIL.decl.ValDeclaration;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.FieldGet;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.expression.New;
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.GenUtil;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.TypeGenContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.EmitLLVMNative;
import wyvern.target.oir.EmitLLVMVisitor;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;
import wyvern.target.oir.OIRProgram;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.parsing.Wyvern;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.suites.CurrentlyBroken;
import wyvern.tools.tests.suites.RegressionTests;
import wyvern.tools.tests.tagTests.TestUtil;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.TypeVarDecl;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.transformers.ExpressionWriter;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.types.Environment;
import wyvern.tools.types.extensions.Int;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static wyvern.stdlib.Globals.getStandardEnv;

@Category(RegressionTests.class)
public class ILTests {
    	
	private static final String BASE_PATH = TestUtil.BASE_PATH;
	private static final String PATH = BASE_PATH + "shiyqw/module/";
	
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
		TypedAST ast = TestUtil.getNewAST(input);
		Expression program = ast.generateIL(GenContext.empty());
    	TypeContext ctx = TypeContext.empty();
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
		TypedAST ast = TestUtil.getNewAST(input);
		Expression program = ast.generateIL(GenContext.empty());
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
		TypedAST ast = TestUtil.getNewAST(input);
		// bogus "system" entry, but makes the text work for now
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
		Expression program = ast.generateIL(genCtx);
    	TypeContext ctx = TypeContext.empty();
		ValueType t = program.typeCheck(ctx);
		Assert.assertEquals(Util.intType(), t);
		Value v = program.interpret(EvalContext.empty());
    	IntegerLiteral five = new IntegerLiteral(5);
		Assert.assertEquals(five, v);
	}
	@Test
	@Category(CurrentlyBroken.class)
	public void testVarFieldWrite() throws ParseException {
		String input = "val obj = new\n"
				     + "    var v : system.Int = 2\n"
				     + "obj.v = 5\n"
				     + "obj.v\n"
				     ;
		TypedAST ast = TestUtil.getNewAST(input);
		// bogus "system" entry, but makes the text work for now
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
		Expression program = ast.generateIL(genCtx);
    	TypeContext ctx = TypeContext.empty();
		ValueType t = program.typeCheck(ctx);
		Assert.assertEquals(Util.intType(), t);
		Value v = program.interpret(EvalContext.empty());
    	IntegerLiteral five = new IntegerLiteral(5);
		Assert.assertEquals(five, v);
	}
	@Test
	public void testDefDecl() throws ParseException {
		String input = "val obj = new\n"
				     + "    val v : system.Int = 5\n"
				     + "    def m() : system.Int = 5\n"
				     + "obj.v\n"
				     ;
		TypedAST ast = TestUtil.getNewAST(input);
		// bogus "system" entry, but makes the text work for now
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
		Expression program = ast.generateIL(genCtx);
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
		TypedAST ast = TestUtil.getNewAST(input);
		// bogus "system" entry, but makes the text work for now
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
		Expression program = ast.generateIL(genCtx);
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
		TypedAST ast = TestUtil.getNewAST(input);
		// bogus "system" entry, but makes the text work for now
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
		Expression program = ast.generateIL(genCtx);
    	TypeContext ctx = TypeContext.empty();
		ValueType t = program.typeCheck(ctx);
		Assert.assertEquals(Util.intType(), t);
		Value v = program.interpret(EvalContext.empty());
    	IntegerLiteral five = new IntegerLiteral(5);
		Assert.assertEquals(five, v);
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
		Expression program = ((Sequence) ast).generateIL(genCtx);
    	TypeContext ctx = TypeContext.empty();
		ValueType t = program.typeCheck(ctx);
		Value v = program.interpret(EvalContext.empty());
				
		Assert.assertEquals(Util.intType(), t);
		
    	IntegerLiteral five = new IntegerLiteral(5);
		Assert.assertEquals(five, v);
	}
	
	@Test
	@Category(CurrentlyBroken.class)
	public void testTypeAbbrev() throws ParseException {
		String input = "type Int = system.Int\n\n"
					 + "val i : Int = 5\n\n"
					 + "i\n"
				     ;
		TypedAST ast = TestUtil.getNewAST(input);
		// bogus "system" entry, but makes the text work for now
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
		Expression program = ast.generateIL(genCtx);
    	TypeContext ctx = TypeContext.empty();
		ValueType t = program.typeCheck(ctx);
		Assert.assertEquals(Util.intType(), t);
		Value v = program.interpret(EvalContext.empty());
    	IntegerLiteral five = new IntegerLiteral(5);
		Assert.assertEquals(five, v);
	}
	
	@Test
	@Category(CurrentlyBroken.class)
	public void testSimpleDelegation() throws ParseException {
		String input = "type IntResult\n"
					 + "    def getResult():system.Int\n\n"
					 + "val r : IntResult = new\n"
					 + "    def getResult():system.Int = 5\n\n"
					 + "val r2 : IntResult = new\n"
					 + "    delegate IntResult to r\n\n"
					 + "r2.getResult()\n"
				     ;
		TypedAST ast = TestUtil.getNewAST(input);
		// bogus "system" entry, but makes the text work for now
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
		Expression program = ast.generateIL(genCtx);
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
		TypedAST ast = TestUtil.getNewAST(source);

		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
		Expression program = ast.generateIL(genCtx);
    	TypeContext ctx = TypeContext.empty();
		ValueType t = program.typeCheck(ctx);
		Assert.assertEquals(Util.intType(), t);
		Value v = program.interpret(EvalContext.empty());
    	IntegerLiteral five = new IntegerLiteral(5);
		Assert.assertEquals(five, v);
	}
	
	
	@Test
	public void testRecursiveFunctions() throws ParseException {
		
		String source = TestUtil.readFile(PATH + "recursivefunctions.wyv");
		TypedAST ast = TestUtil.getNewAST(source);

		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
		Expression program = ast.generateIL(genCtx);
    	TypeContext ctx = TypeContext.empty();
		ValueType t = program.typeCheck(ctx);
		Assert.assertEquals(Util.intType(), t);
		Value v = program.interpret(EvalContext.empty());
    	IntegerLiteral five = new IntegerLiteral(5);
		Assert.assertEquals(five, v);
	}
	
	@Test
	@Category(CurrentlyBroken.class)
	public void testLambda() throws ParseException {
		
		String source = TestUtil.readFile(PATH + "lambdatest.wyv");
		TypedAST ast = TestUtil.getNewAST(source);

		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
		Expression program = ast.generateIL(genCtx);
    	TypeContext ctx = TypeContext.empty();
		ValueType t = program.typeCheck(ctx);
		Assert.assertEquals(Util.intType(), t);
		Value v = program.interpret(EvalContext.empty());
    	IntegerLiteral five = new IntegerLiteral(5);
		Assert.assertEquals(five, v);
	}
}
