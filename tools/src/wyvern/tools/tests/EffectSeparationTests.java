package wyvern.tools.tests;

import org.hamcrest.core.StringContains;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.suites.RegressionTests;

@Category(RegressionTests.class)
public class EffectSeparationTests {
    private static final String PATH = TestUtil.BASE_PATH;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testSeparation1() throws ParseException {
        /* Add empty effect set where annotation is missing */
        TestUtil.doTestScriptModularly(PATH, "effectSeparation.client1", Util.unitType(), Util.unitValue());
    }

    @Test
    public void testSeparation2() throws ParseException {
        /* Fully annotated module */
        TestUtil.doTestScriptModularly(PATH, "effectSeparation.client2", Util.unitType(), Util.unitValue());
    }

    @Test
    public void testSeparation3() throws ParseException {
        /* Unannotated module with type annotated */
        TestUtil.doTestScriptModularly(PATH, "effectSeparation.client3", Util.unitType(), Util.unitValue());
    }

    @Test
    public void testSeparation4() throws ParseException {
        /* Unannotated module */
        TestUtil.doTestScriptModularly(PATH, "effectSeparation.client4", Util.unitType(), Util.unitValue());
    }

    @Test
    public void testSeparation5() throws ParseException {
        /* Unannotated moduledef */
        TestUtil.doTestScriptModularly(PATH, "effectSeparation.client5", Util.unitType(), Util.unitValue());
    }

    @Test
    public void testSeparation6() throws ParseException {
        /* Unannotated moduledef with type annotation */
        TestUtil.doTestScriptModularly(PATH, "effectSeparation.client6", Util.unitType(), Util.unitValue());
    }

    @Test
    public void testSeparation7() throws ParseException {
        /* Annotated moduledef */
        TestUtil.doTestScriptModularly(PATH, "effectSeparation.client7", Util.unitType(), Util.unitValue());
    }

    @Test
    public void testSeparation8() throws ParseException {
        /* Annotated moduledef with type annotation */
        TestUtil.doTestScriptModularly(PATH, "effectSeparation.client8", Util.unitType(), Util.unitValue());
    }

    @Test
    public void testSeparation9() throws ParseException {
        // Import effect-unannotated module from annotated module
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString("Effect-annotated module"));
        TestUtil.doTestScriptModularly(PATH, "effectSeparation.client9", Util.unitType(), Util.unitValue());
    }

    @Test
    public void testSeparation10() throws ParseException {
        /* importing annotated module from annotated module */
        TestUtil.doTestScriptModularly(PATH, "effectSeparation.client10", Util.unitType(), Util.unitValue());
    }

    @Test
    public void testSeparation11() throws ParseException {
        /* import lifted unannotated module from annotated module */
        TestUtil.doTestScriptModularly(PATH, "effectSeparation.client11", Util.unitType(), Util.unitValue());
    }

    @Test
    public void testSeparation12() throws ParseException {
        /* Correctly annotated module def */
        TestUtil.doTestScriptModularly(PATH, "effectSeparation.client12", Util.unitType(), Util.unitValue());
    }

    @Test
    public void testSeparation13() throws ParseException {
        // Module Def has incorrect effect annotation
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString("not a subtype of effects that method produces"));
        TestUtil.doTestScriptModularly(PATH, "effectSeparation.client13", Util.unitType(), Util.unitValue());
    }

    @Test
    public void testSeparation14() throws ParseException {
        // Pure module having non-empty annotation
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString("Pure module should always have empty effect annotation "));
        TestUtil.doTestScriptModularly(PATH, "effectSeparation.client14", Util.unitType(), Util.unitValue());
    }


}
