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
public class EffectHierarchyTests {
    private static final String PATH = TestUtil.BASE_PATH;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testSuper() throws ParseException {
        /* Add empty effect set where annotation is missing */
        TestUtil.doTestScriptModularly(PATH, "hierarchy.supereffect", Util.unitType(), Util.unitValue());
    }

    @Test
    public void testSub() throws ParseException {
        /* Add empty effect set where annotation is missing */
        TestUtil.doTestScriptModularly(PATH, "hierarchy.subeffect", Util.unitType(), Util.unitValue());
    }

    @Test
    public void testSubtype() throws ParseException {
        /* Add empty effect set where annotation is missing */
        TestUtil.doTestScriptModularly(PATH, "hierarchy.subtyping", Util.unitType(), Util.unitValue());
    }

    @Test
    public void testSubtypeerror() throws ParseException {
        /* Add empty effect set where annotation is missing */

        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString("not a subtype of the expected declaration"));
        TestUtil.doTestScriptModularly(PATH, "hierarchy.subtypingerror", Util.unitType(), Util.unitValue());
    }

    @Test
    public void testAccumulate() throws ParseException {
        /* Add empty effect set where annotation is missing */
        TestUtil.doTestScriptModularly(PATH, "hierarchy.accumulateEffects", Util.unitType(), Util.unitValue());
    }

    @Test
    public void testAvoid1() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "hierarchy.avoidType", Util.unitType(), Util.unitValue());
    }

    @Test
    public void testAvoid2() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "hierarchy.avoidType2", Util.unitType(), Util.unitValue());
    }

    @Test
    public void testAvoid3() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "hierarchy.avoidTypeIncrease", Util.unitType(), Util.unitValue());
    }

    @Test
    public void testAvoid4() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "hierarchy.avoidTypeDecrease", Util.unitType(), Util.unitValue());
    }

    @Test
    public void testFile() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "hierarchy.main", Util.unitType(), Util.unitValue());
    }

    @Test
    public void testImport() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "hierarchy.import", Util.unitType(), Util.unitValue());
    }
}

