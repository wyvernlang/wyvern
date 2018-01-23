package wyvern.tools.tests;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.suites.RegressionTests;

@Category(RegressionTests.class)
public class Illustrations {

    private static final String BASE_PATH = TestUtil.BASE_PATH;
    private static final String PATH = BASE_PATH + "illustrations/";

    @BeforeClass public static void setupResolver() {
        TestUtil.setPaths();
        WyvernResolver.getInstance().addPath(PATH);
    }

    @Test
    public void testFigure5Corrected() throws ParseException {
        // uses "FileIO.wyt", "fileIO.wyv", "Logger.wyt", "logger.wyv", "wavyUnderlineV3.wyv", "example5.wyv", "example5driver.wyv"
        TestUtil.doTestScriptModularly("illustrations.example5driver",
                Util.intType(),
                new IntegerLiteral(5));
    }

    /*@Test(expected=RuntimeException.class)
    public void testFigure5() throws ParseException {

        String[] fileList = {"FileIO.wyt", "fileIO.wyv", "Logger.wyt",
                "logger.wyv", "wavyUnderlineV3Fig5.wyv",
                "example5.wyv", "example5driver.wyv", };
        GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), new NominalType("", "system"));
        genCtx = new TypeOrEffectGenContext("Int", "system", genCtx);
        genCtx = new TypeOrEffectGenContext("Unit", "system", genCtx);

        List<wyvern.target.corewyvernIL.decl.Declaration> decls = new LinkedList<wyvern.target.corewyvernIL.decl.Declaration>();

        for(String fileName : fileList) {
            String source = TestUtil.readFile(PATH + fileName);
            TypedAST ast = TestUtil.getNewAST(source, "test input");
            wyvern.target.corewyvernIL.decl.Declaration decl = ((Declaration) ast).topLevelGen(genCtx, null);
            decls.add(decl);
            genCtx = GenUtil.link(genCtx, decl);
        }

    }

    @Test(expected=RuntimeException.class)
    public void testFigure3() throws ParseException {

        String[] fileList = {"FileIO.wyt", "fileIO.wyv", "Logger.wyt",
                "logger.wyv", "wavyUnderlineV1.wyv",
                "example5Fig3.wyv", "example5driver.wyv", };
        GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), new NominalType("", "system"));
        genCtx = new TypeOrEffectGenContext("Int", "system", genCtx);
        genCtx = new TypeOrEffectGenContext("Unit", "system", genCtx);

        List<wyvern.target.corewyvernIL.decl.Declaration> decls = new LinkedList<wyvern.target.corewyvernIL.decl.Declaration>();

        for(String fileName : fileList) {
            String source = TestUtil.readFile(PATH + fileName);
            TypedAST ast = TestUtil.getNewAST(source, "test input");
            wyvern.target.corewyvernIL.decl.Declaration decl = ((Declaration) ast).topLevelGen(genCtx, null);
            decls.add(decl);
            genCtx = GenUtil.link(genCtx, decl);
        }

    }*/

    @Test
    public void testFigure2() throws ParseException {
        // uses "lists.wyv", "UserInfo.wyt", "userInfo.wyv", "DocumentLock.wyv", "example2.wyv"
        TestUtil.doTestScriptModularly("illustrations.example2",
                Util.intType(),
                new IntegerLiteral(5));
    }

    /*@Test(expected=RuntimeException.class)
    public void testFigure4() throws ParseException {

        String[] fileList = {"lists.wyv", "UserInfo.wyt", "userInfo.wyv", "wavyUnderlineV2.wyv", "example4driver.wyv", };
        GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), new NominalType("", "system"));
        genCtx = new TypeOrEffectGenContext("Int", "system", genCtx);
        genCtx = new TypeOrEffectGenContext("Unit", "system", genCtx);

        List<wyvern.target.corewyvernIL.decl.Declaration> decls = new LinkedList<wyvern.target.corewyvernIL.decl.Declaration>();

        for(String fileName : fileList) {
            String source = TestUtil.readFile(PATH + fileName);
            TypedAST ast = TestUtil.getNewAST(source, "test input");
            wyvern.target.corewyvernIL.decl.Declaration decl = ((Declaration) ast).topLevelGen(genCtx, null);
            decls.add(decl);
            genCtx = GenUtil.link(genCtx, decl);
        }
    }*/

    public static final NativeFileIO nativeFileIO = new NativeFileIO();
    public static class NativeFileIO {
        public int write(int i) {
            return i + 1;
        }
        public int read() {
            return 3;
        }
    }
}
