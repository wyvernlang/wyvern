package wyvern.tools.tests;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import wyvern.target.corewyvernIL.expression.*;
import wyvern.target.corewyvernIL.support.*;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.interop.ObjectLinker;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.reflection.Mirror;
import wyvern.tools.tests.reflection.TestTools;
import wyvern.tools.tests.suites.CurrentlyBroken;
import wyvern.tools.tests.suites.RegressionTests;
import wyvern.tools.tests.tagTests.TestUtil;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.interfaces.TypedAST;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

@Category(RegressionTests.class)
public class ReflectionTests {

    public static Mirror mirror = new Mirror();
    public static TestTools tools = new TestTools();
    public static ObjectLinker linker = new ObjectLinker();
    private static final String PATH = TestUtil.BASE_PATH + "reflection/";

    @BeforeClass
    public static void setupResolver() {
        TestUtil.setPaths();
        WyvernResolver.getInstance().addPath(PATH);
    }

    @Test
    @Category(CurrentlyBroken.class)
    public void testBase() throws ParseException {
        InterpreterState state = new InterpreterState(new File(PATH));
        Expression program = state.getResolver().resolveModule("base").getExpression();
        program.interpret(TestUtil.getStandardEvalContext());
        /* String [] fileList = {"modules/Lists.wyv",
                              "modules/baseModule.wyv",
                              "base.wyv"};
        List<wyvern.target.corewyvernIL.decl.Declaration> decls = new LinkedList<wyvern.target.corewyvernIL.decl.Declaration>();
        GenContext genCtx = TestUtil.getStandardGenContext();
        genCtx = new TypeGenContext("Boolean", "system", genCtx);

        for (String filename : fileList) {
            String source = TestUtil.readFile(PATH + filename);
            TypedAST ast = TestUtil.getNewAST(source);
            wyvern.target.corewyvernIL.decl.Declaration decl = ((Declaration) ast).topLevelGen(genCtx, null);
            decls.add(decl);
            genCtx = GenUtil.link(genCtx, decl);
        }
        Expression mainProgram = GenUtil.genExp(decls, genCtx);
        // after genExp the modules are transferred into an object. We need to evaluate one field of the main object

        TypeContext ctx = TestUtil.getStandardTypeContext();
        mainProgram.typeCheck(ctx);
        mainProgram.interpret(TestUtil.getStandardEvalContext()); */
    }

    @Test
    @Category(CurrentlyBroken.class)
    public void testObjectEquals() throws ParseException {
        InterpreterState state = new InterpreterState(new File(PATH));
        Expression program = state.getResolver().resolveModule("objectEquals").getExpression();
        program.interpret(TestUtil.getStandardEvalContext());

        /* String [] fileList = {"modules/Bool.wyv",
                              "modules/Lists.wyv",
                              "modules/baseModule.wyv",
                              "modules/intObject.wyv",
                              "objectEquals.wyv"};
        List<wyvern.target.corewyvernIL.decl.Declaration> decls = new LinkedList<wyvern.target.corewyvernIL.decl.Declaration>();
        GenContext genCtx = TestUtil.getStandardGenContext();
        genCtx = new TypeGenContext("Boolean", "system", genCtx);

        for (String filename : fileList) {
            String source = TestUtil.readFile(PATH + filename);
            TypedAST ast = TestUtil.getNewAST(source);
            wyvern.target.corewyvernIL.decl.Declaration decl = ((Declaration) ast).topLevelGen(genCtx, null);
            decls.add(decl);
            genCtx = GenUtil.link(genCtx, decl);
        }
        Expression mainProgram = GenUtil.genExp(decls, genCtx);
        // after genExp the modules are transferred into an object. We need to evaluate one field of the main object

        TypeContext ctx = TestUtil.getStandardTypeContext();
        mainProgram.typeCheck(ctx);
        mainProgram.interpret(TestUtil.getStandardEvalContext()); */
    }

    @Test
    @Category(CurrentlyBroken.class)
    public void testObjectTypeOf() throws ParseException {
        String [] fileList = {"modules/Bool.wyv",
                              "modules/Lists.wyv",
                              "modules/baseModule.wyv",
                              "objectTypeOf.wyv"};
        List<wyvern.target.corewyvernIL.decl.Declaration> decls = new LinkedList<wyvern.target.corewyvernIL.decl.Declaration>();
        GenContext genCtx = TestUtil.getStandardGenContext();
        genCtx = new TypeGenContext("Boolean", "system", genCtx);

        for (String filename : fileList) {
            String source = TestUtil.readFile(PATH + filename);
            TypedAST ast = TestUtil.getNewAST(source);
            wyvern.target.corewyvernIL.decl.Declaration decl = ((Declaration) ast).topLevelGen(genCtx, null);
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
    public void testLists() throws ParseException {
        InterpreterState state = new InterpreterState(new File(PATH));
        Expression program = state.getResolver().resolveModule("listClient").getExpression();
        program.interpret(TestUtil.getStandardEvalContext());
    }

    @Test
    public void testBools() throws ParseException {
        InterpreterState state = new InterpreterState(new File(PATH));
        Expression program = state.getResolver().resolveModule("boolTests").getExpression();
        program.interpret(TestUtil.getStandardEvalContext());
    }
}
