package wyvern.tools.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.suites.RegressionTests;

@Category(RegressionTests.class)
public class ModuleSystemTests {

    private static final String BASE_PATH = TestUtil.BASE_PATH;
    private static final String PATH = BASE_PATH + "modules/";

    @BeforeClass
    public static void setupResolver() {
        TestUtil.setPaths();
        WyvernResolver.getInstance().addPath(PATH);
        Globals.setUsePrelude(false);
    }

    @AfterClass public static void teardown() {
        Globals.setUsePrelude(true);  // restore the default to use the prelude
    }


    @Test
    public void testInst() throws ParseException {
        String program = TestUtil.readFile(PATH + "inst.wyv");
        TestUtil.getNewAST(program, "test input");
    }

    @Test
    public void testADT() throws ParseException {
        TestUtil.doTestScriptModularly("modules.listClient", Util.intType(), new IntegerLiteral(5));
    }

    @Test
    public void testTransitiveAuthorityGood() throws ParseException {
        TestUtil.doTestScriptModularly("modules.databaseClientGood", Util.intType(), new IntegerLiteral(1));
    }

    @Test
    public void testTransitiveAuthorityBad() throws ParseException {
        TestUtil.doTestScriptModularlyFailing("modules.databaseClientBad", ErrorMessage.NO_SUCH_METHOD);
    }

    @Test
    public void testTopLevelVars() throws ParseException {
        TestUtil.doTestScriptModularly("modules.databaseUser", Util.intType(), new IntegerLiteral(10));
    }

    @Test
    public void testTopLevelVarsWithAliasing() throws ParseException {
        TestUtil.doTestScriptModularly("modules.databaseUserTricky", Util.intType(), new IntegerLiteral(10));
    }

    @Test
    public void testTopLevelVarGet() throws ParseException {
        String source = "var v : Int = 5\n" + "v\n";

        TestUtil.doTestInt(source, 5);
    }

    @Test
    public void testTopLevelVarSet() throws ParseException {

        String source = "var v : Int = 5\n" + "v = 10\n" + "v\n";
        TestUtil.doTestInt(source, 10);
    }

    @Test
    public void testSimpleADT() throws ParseException {
        TestUtil.doTestScriptModularly("modules.simpleADTdriver", Util.intType(), new IntegerLiteral(5));
    }

    @Test
    public void testSimpleADTWithRenamingImport() throws ParseException {
        TestUtil.doTestScriptModularly("modules.simpleADTdriver2", Util.intType(), new IntegerLiteral(5));
    }

    @Test
    public void testSimpleADTWithRenamingRequire() throws ParseException {
        TestUtil.doTestScriptModularly("modules.simpleADTdriver3", Util.intType(), new IntegerLiteral(5));
    }

    @Test
    public void testCyclicImports() throws ParseException {
        String errorMessage = "testCyclicImports should catch a ToolError of type ErrorMesasge.IMPORT_CYCLE";
        try {
            TestUtil.doTestScriptModularly("modules.cyclic.cyclic1a", Util.unitType(), Util.unitValue());
            fail(errorMessage);
        } catch (ToolError te) {
            assertEquals(errorMessage, ErrorMessage.IMPORT_CYCLE, te.getTypecheckingErrorMessage());
        }
    }

    @Test
    public void testLongCyclicImport() throws ParseException {
        String errorMessage = "testLongCyclicImports should catch a ToolError of type ErrorMesasge.IMPORT_CYCLE";
        try {
            TestUtil.doTestScriptModularly("modules.cyclic.cyclic2a", Util.unitType(), Util.unitValue());
            fail(errorMessage);
        } catch (ToolError te) {
            assertEquals(errorMessage, ErrorMessage.IMPORT_CYCLE, te.getTypecheckingErrorMessage());
        }
    }

    @Test
    public void testCircularImportNotInvolvingTopLevel() throws ParseException {
        String errorMessage = "testCyclicImports should catch a ToolError of type ErrorMesasge.IMPORT_CYCLE";
        try {
            TestUtil.doTestScriptModularly("modules.cyclic.cyclic3a", Util.unitType(), Util.unitValue());
            fail(errorMessage);
        } catch (ToolError te) {
            assertEquals(errorMessage, ErrorMessage.IMPORT_CYCLE, te.getTypecheckingErrorMessage());
        }
    }

     @Test
     public void testArrowModuleClient1() throws ParseException {
         TestUtil.doTestScriptModularly("modules.arrowModuleClient1", Util.unitType(), Util.unitValue());
     }

     @Test
     public void testArrowModuleClient2() throws ParseException {
        TestUtil.doTestScriptModularly("modules.arrowModuleClient2", Util.unitType(), Util.unitValue());
     }
}
