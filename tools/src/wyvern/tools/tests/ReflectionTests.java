package wyvern.tools.tests;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import wyvern.target.corewyvernIL.expression.*;
import wyvern.target.corewyvernIL.support.*;
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
        String [] fileList = {"Lists.wyv", "baseModule.wyv", "base.wyv"};
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
    public void testObjectEquals() throws ParseException {
        String [] fileList = {"Lists.wyv", "baseModule.wyv", "intObject.wyv", "objectEquals.wyv"};
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
    public void testObjectTypeOf() throws ParseException {
        String [] fileList = {"Lists.wyv", "baseModule.wyv", "objectTypeOf.wyv"};
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
    public void testLists() throws ParseException {
        String [] fileList = {"Lists.wyv", "listClient.wyv"};
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
}
