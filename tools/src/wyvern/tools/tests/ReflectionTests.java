package wyvern.tools.tests;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import wyvern.target.corewyvernIL.expression.*;
import wyvern.target.corewyvernIL.support.*;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.reflection.Mirror;
import wyvern.tools.tests.reflection.TestTools;
import wyvern.tools.tests.suites.CurrentlyBroken;
import wyvern.tools.tests.suites.RegressionTests;
import wyvern.tools.tests.tagTests.TestUtil;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.interfaces.TypedAST;

import java.util.LinkedList;
import java.util.List;

@Category(RegressionTests.class)
public class ReflectionTests {

    public static Mirror mirror = new Mirror();
    public static TestTools tools = new TestTools();
    private static final String BASE_PATH = TestUtil.BASE_PATH;
    private static final String PATH = BASE_PATH + "reflection/";

    @BeforeClass
    public static void setupResolver() {
        TestUtil.setPaths();
        WyvernResolver.getInstance().addPath(PATH);
    }

    @Test
    public void testBase() throws ParseException {
        String input = TestUtil.readFile(PATH + "base.wyv");
        TypedAST ast = TestUtil.getNewAST(input);
        GenContext genCtx = TestUtil.getStandardGenContext();
        genCtx = new TypeGenContext("Boolean", "system", genCtx);
        TypeContext ctx = TestUtil.getStandardTypeContext();
        wyvern.target.corewyvernIL.decl.Declaration decl = ((Declaration) ast).topLevelGen(genCtx);
        genCtx = GenUtil.link(genCtx, decl); // not sure this is necessary
        List<wyvern.target.corewyvernIL.decl.Declaration> decls = new LinkedList<wyvern.target.corewyvernIL.decl.Declaration>();
        decls.add(decl);
        Expression mainProgram = GenUtil.genExp(decls, genCtx);
        //Expression program = new FieldGet(mainProgram, "x"); // slightly hacky
        mainProgram.typeCheck(ctx);
        mainProgram.interpret(EvalContext.empty());
    }

    @Test
    @Category(CurrentlyBroken.class)
    public void testObjectEquals() throws ParseException {
        String [] fileList = {"baseModule.wyv", "objectEquals.wyv"};
        List<wyvern.target.corewyvernIL.decl.Declaration> decls = new LinkedList<wyvern.target.corewyvernIL.decl.Declaration>();
        GenContext genCtx = TestUtil.getStandardGenContext();
        genCtx = new TypeGenContext("Boolean", "system", genCtx);

        for (String filename : fileList) {
            String source = TestUtil.readFile(PATH + filename);
            TypedAST ast = TestUtil.getNewAST(source);
            wyvern.target.corewyvernIL.decl.Declaration decl = ((Declaration) ast).topLevelGen(genCtx);
            decls.add(decl);
            genCtx = GenUtil.link(genCtx, decl);
        }
        Expression mainProgram = GenUtil.genExp(decls, genCtx);
        // after genExp the modules are transferred into an object. We need to evaluate one field of the main object

        TypeContext ctx = TestUtil.getStandardTypeContext();
        mainProgram.typeCheck(ctx);
        mainProgram.interpret(TestUtil.getStandardEvalContext());
    }

    @Test
    @Category(CurrentlyBroken.class)
    public void testStringEquals() throws ParseException {
        String [] fileList = {"base.wyv", "stringEquals.wyv"};
        List<wyvern.target.corewyvernIL.decl.Declaration> decls = new LinkedList<wyvern.target.corewyvernIL.decl.Declaration>();
        GenContext genCtx = TestUtil.getStandardGenContext();
        genCtx = new TypeGenContext("Boolean", "system", genCtx);

        for (String filename : fileList) {
            String source = TestUtil.readFile(PATH + filename);
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
        wyvern.target.corewyvernIL.expression.Value v = program.interpret(EvalContext.empty());
        IntegerLiteral one = new IntegerLiteral(1);
        Assert.assertEquals(one, v);
    }

    @Test
    public void testReflectModule() throws ParseException {
        String[] fileList = {"Lists.wyv", "base.wyv"};
        GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), new NominalType("", "system"));
        genCtx = new TypeGenContext("Int", "system", genCtx);
        genCtx = new TypeGenContext("Unit", "system", genCtx);
        genCtx = new TypeGenContext("String", "system", genCtx);

        List<wyvern.target.corewyvernIL.decl.Declaration> decls = new LinkedList<wyvern.target.corewyvernIL.decl.Declaration>();

        for (String fileName : fileList) {
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
}
