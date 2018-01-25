package wyvern.tools.tests;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.reflection.Mirror;
import wyvern.tools.tests.reflection.TestTools;
import wyvern.tools.tests.suites.CurrentlyBroken;
import wyvern.tools.tests.suites.RegressionTests;

@Category(RegressionTests.class)
public class ReflectionTests {

    public static final Mirror mirror = new Mirror();
    public static final TestTools tools = new TestTools();
    private static final String PATH = TestUtil.BASE_PATH + "reflection/";

    @BeforeClass
    public static void setupResolver() {
        TestUtil.setPaths();
        WyvernResolver.getInstance().addPath(PATH);
    }

    @Test
    public void testBase() throws ParseException {
        TestUtil.doTestScriptModularly("reflection.base", null, null);
    }

    @Test
    public void testObjectEquals() throws ParseException {
        TestUtil.doTestScriptModularly("reflection.objectEquals", null, null);
    }

    @Test
    public void testObjectTypeOf() throws ParseException {
        TestUtil.doTestScriptModularly("reflection.objectTypeOf", null, null);
    }


    @Test
    @Category(CurrentlyBroken.class)
    public void testObjectTypeOf2() throws ParseException {
        /*String [] fileList = {"modules/Bool.wyv",
                              "modules/Lists.wyv",
                              "modules/baseModule.wyv",
                              "objectTypeOf.wyv"};
        List<wyvern.target.corewyvernIL.decl.Declaration> decls = new LinkedList<wyvern.target.corewyvernIL.decl.Declaration>();
        GenContext genCtx = Globals.getStandardGenContext();
        genCtx = new TypeOrEffectGenContext("Boolean", "system", genCtx);

        for (String filename : fileList) {
            String source = TestUtil.readFile(PATH + filename);
            TypedAST ast = TestUtil.getNewAST(source, "test input");
            wyvern.target.corewyvernIL.decl.Declaration decl = ((Declaration) ast).topLevelGen(genCtx, null);
            decls.add(decl);
            genCtx = GenUtil.link(genCtx, decl);
        }
        Expression mainProgram = GenUtil.genExp(decls, genCtx);
        // after genExp the modules are transferred into an object. We need to evaluate one field of the main object

        TypeContext ctx = Globals.getStandardTypeContext();
        mainProgram.typeCheck(ctx, null);
        mainProgram.interpret(Globals.getStandardEvalContext());*/
    }

    @Test
    public void testLists() throws ParseException {
        TestUtil.doTestScriptModularly("reflection.listClient", null, null);
    }

    @Test
    public void testBools() throws ParseException {
        TestUtil.doTestScriptModularly("reflection.boolTests", Util.intType(), new IntegerLiteral(0));
    }

}
