package wyvern.tools.tests;

import org.hamcrest.core.StringContains;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import wyvern.target.corewyvernIL.expression.StringLiteral;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.tools.errors.ToolError;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.suites.RegressionTests;

/**
 * Test suite for polymorphic effects.
 */
@Category(RegressionTests.class)
public class PolymorphicEffectTests {
    private static final String PATH = TestUtil.BASE_PATH;

    @BeforeClass
    public static void setupResolver() {
        TestUtil.setPaths();
        WyvernResolver.getInstance().addPath(PATH);
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    // Accepted examples

    @Test
    public void testBasicManual() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.basic-manual", Util.stringType(), new StringLiteral("abc"));
    }

    @Test
    public void testBasicNaming() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.basic-naming", Util.stringType(), new StringLiteral("abc"));
    }

    @Test
    public void testBasicParameters1() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.basic-parameters1", Util.stringType(), new StringLiteral("abc"));
    }

    @Test
    public void testBasicParameters2() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.basic-parameters2", Util.stringType(), new StringLiteral("abc"));
    }

    @Test
    public void testBasicStructuralEquality() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.basic-structural-equality", Util.stringType(), new StringLiteral("abc"));
    }

    @Test
    public void testBasicTypeInference() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.basic-type-inference", Util.stringType(), new StringLiteral("abc"));
    }

    @Test
    public void testCombination1() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.combination1", Util.stringType(), new StringLiteral("abc"));
    }

    @Test
    public void testCombination2() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.combination2", Util.stringType(), new StringLiteral("abc"));
    }

    @Test
    public void testFunction() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.function", Util.stringType(), new StringLiteral("abcabc"));
    }

    @Test
    public void testSubtype1() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.subtype1", Util.stringType(), new StringLiteral("abc"));
    }

    @Test
    public void testSubtype2() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.subtype2", Util.stringType(), new StringLiteral("abc"));
    }

    @Test
    public void testSubtype3() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.subtype3", Util.stringType(), new StringLiteral("abc"));
    }

    @Test
    public void testSubtype4() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.subtype4", Util.stringType(), new StringLiteral("abc"));
    }

    // Rejected examples

    @Test
    public void testRejectedAbstractTypeRefinement() throws ParseException {
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString(
                "Cannot apply generic arguments: type myLogger.T is abstract"
        ));
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.rejected-abstract-type-refinement", Util.stringType(), new StringLiteral("abcabc"));
    }

    @Test
    public void testRejectedCombination1() throws ParseException {
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString(
                "Effect annotation {s.E} on method run is not a subtype of effects that method produces, which are [q.E];"
        ));
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.rejected-combination1", Util.stringType(), new StringLiteral("abc"));
    }

    @Test
    public void testRejectedCombination2() throws ParseException {
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString(
                "Effect annotation {s.E} on method run is not a subtype of effects that method produces, which are [r.E];"
        ));
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.rejected-combination2", Util.stringType(), new StringLiteral("abc"));
    }

    @Test
    public void testRejectedCombination3() throws ParseException {
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString(
                "Effect annotation {s.E} on method run is not a subtype of effects that method produces, which are [t.E];"
        ));
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.rejected-combination3", Util.stringType(), new StringLiteral("abc"));
    }

    @Test
    public void testRejectedFunctionSubtype() throws ParseException {
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString(
                "Effect annotation {} on method run is not a subtype of effects that method produces, which are [u.E];"
        ));
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.rejected-function-subtype", Util.stringType(), new StringLiteral("abcabc"));
    }

    @Test
    public void testRejectedHiddenEffects() throws ParseException {
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString(
                "Effect \"v.hiddenEffect\" not found in scope"
        ));
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.rejected-hidden-effects", Util.stringType(), new StringLiteral("abc"));
    }

    @Test
    public void testRejectedNotInferrable() throws ParseException {
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString(
                "Generic argument(s) for the method id were not inferrable and must be provided at the call site"
        ));
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.rejected-not-inferrable", Util.stringType(), new StringLiteral("abc"));
    }

    @Test
    public void testRejectedSubset() throws ParseException {
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString(
                "Effect annotation {} on method run is not a subtype of effects that method produces, which are [u.E];"
        ));
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.rejected-subset", Util.stringType(), new StringLiteral("abc"));
    }

    @Test
    public void testRejectedSubtype() throws ParseException {
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString(
                "Generic[{}]; declaration E is not a subtype of the expected declaration"
        ));
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.rejected-subtype", Util.stringType(), new StringLiteral("abc"));
    }

}
