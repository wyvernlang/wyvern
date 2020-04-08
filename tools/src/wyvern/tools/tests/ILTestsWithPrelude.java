package wyvern.tools.tests;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.tools.Interpreter;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.suites.RegressionTests;

@Category(RegressionTests.class)
public class ILTestsWithPrelude {

    @Before
    public void setup() {
        Globals.resetState();
    }
    
    @BeforeClass public static void setupResolver() {
        TestUtil.setPaths();
        WyvernResolver.getInstance().addPath(ILTests.PATH);
    }

    @Test
    public void testIf() throws ParseException {
        TestUtil.doTestScriptModularly("tsls.cleanIf", Util.intType(), new IntegerLiteral(5));
    }

    @Test
    public void testListLength() throws ParseException {
        String src
        = "import wyvern.collections.list\n"
                + "val x : list.List[Int] = list.make[Int]()\n"
                + "x.append(1)\n"
                + "x.append(2)\n"
                + "x.length()\n";
        TestUtil.doTest(src, Util.dynType(), new IntegerLiteral(2));
    }

    @Test
    public void testInterpreterOnScript() {
        String[] args = new String[] {TestUtil.EXAMPLES_PATH + "rosetta/hello.wyv"};
        Interpreter.wyvernHome.set("..");
        Interpreter.main(args);
    }

    @Test
    public void testPreviousTopLevelBug() throws ParseException {
        TestUtil.doTestScriptModularly("modules.topLevelBug", null, null);
    }

    @Test
    public void testListGet() throws ParseException {
        String src
        = "import wyvern.collections.list\n"
                + "val x : list.List[Int] = list.make[Int]()\n"
                + "x.append(1)\n"
                + "x.append(2)\n"
                + "x.get(0).getOrElse(() => -1)\n";
        TestUtil.doTest(src, Util.dynType(), new IntegerLiteral(1));
    }

    @Test
    public void testListTSL() throws ParseException {
        String src
        = "import metadata wyvern.collections.list\n"
                + "val l : list.List[Int] = {1, 2, 3, 4}\n"
                + "l.get(1).getOrElse(() => -1)\n";
        TestUtil.doTest(src, Util.dynType(), new IntegerLiteral(2));
    }

    @Test
    public void testTSL2() throws ParseException {
        TestUtil.doTestScriptModularly("tsls.identityClient", Util.intType(), new IntegerLiteral(5));
    }

    @Test
    public void testTSL3() throws ParseException {
        TestUtil.doTestScriptModularly("tsls.trivialClient", Util.intType(), new IntegerLiteral(5));
    }

    @Test
    public void testTSL4() throws ParseException {
        TestUtil.doTestScriptModularlyFailing("tsls.failingClient", ErrorMessage.TSL_ERROR);
    }

    @Test
    public void testPostTSLIndentation() throws ParseException {
        TestUtil.doTestScriptModularly("tsls.postTSLIndentation", Util.intType(), new IntegerLiteral(23));
    }

    @Test
    public void testFunctionInType() throws ParseException {
        TestUtil.doTestScriptModularlyFailing("errors.ReturnTypeBug", ErrorMessage.QUALIFIED_TYPES_ONLY_FIELDS);
    }

    @Test
    public void testMetadataInterpretation() throws ParseException {
        TestUtil.doTestScriptModularly("modules.importWithMetadata", null, null);
    }

}
