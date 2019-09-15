package wyvern.tools.tests;

import org.hamcrest.core.StringContains;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import wyvern.target.corewyvernIL.expression.IntegerLiteral;
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
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.basicManual", Util.stringType(), new StringLiteral("abc"));
    }

    @Test
    public void testBasicNaming() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.basicNaming", Util.stringType(), new StringLiteral("abc"));
    }

    @Test
    public void testBasicParameters1() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.basicParameters1", Util.stringType(), new StringLiteral("abc"));
    }

    @Test
    public void testBasicParameters2() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.basicParameters2", Util.stringType(), new StringLiteral("abc"));
    }

    @Test
    public void testBasicStructuralEquality() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.basicStructuralEquality", Util.stringType(), new StringLiteral("abc"));
    }

    @Test
    public void testBasicTypeInference() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.basicTypeInference", Util.stringType(), new StringLiteral("abc"));
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
    public void testEditor() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.editor", Util.stringType(), new StringLiteral("abcabc"));
    }

    @Test
    public void testFunction() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.function", Util.stringType(), new StringLiteral("abcabc"));
    }

    @Test
    public void testParametricModuleFunctor1() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.parametricModuleFunctor1", Util.intType(), new IntegerLiteral(3));
    }

    @Test
    public void testParametricModuleFunctor2() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.parametricModuleFunctor2", Util.stringType(), new StringLiteral("abc"));
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
    public void testAvoidEffects() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.avoidEffects", Util.stringType(), new StringLiteral("abc"));
    }

    @Test
    public void testSubtype3() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.subtype3", Util.stringType(), new StringLiteral("abc"));
    }

    @Test
    public void testSubtype4() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.subtype4", Util.stringType(), new StringLiteral("abc"));
    }

    @Test
    public void import0() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.client", Util.stringType(), new StringLiteral("abc"));
    }



    @Test
    public void import3empty() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.import3empty", Util.stringType(), new StringLiteral("abc"));
    }

    @Test
    public void go() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "import3.import3Client", Util.stringType(), new StringLiteral("abc"));
    }





    @Test
    public void import3Rejected() throws ParseException {
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString(
                "The callee method cannot accept actual arguments with types:"
        ));
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.import3Rejected", Util.stringType(), new StringLiteral("abc"));
    }

    @Test
    public void testRejectedAbstractTypeRefinement() throws ParseException {
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString(
                "Cannot apply generic arguments: type myLogger.T is abstract"
                ));
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.rejectedAbstractTypeRefinement", Util.stringType(), new StringLiteral("abcabc"));
    }

    @Test
    public void testRejectedCombination1() throws ParseException {
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString(
                "Effect annotation {s.E} on method run is not a subtype of effects that method produces, which are [q.E];"
                ));
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.rejectedCombination1", Util.stringType(), new StringLiteral("abc"));
    }

    @Test
    public void testRejectedCombination2() throws ParseException {
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString(
                "Effect annotation {s.E} on method run is not a subtype of effects that method produces, which are [r.E];"
                ));
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.rejectedCombination2", Util.stringType(), new StringLiteral("abc"));
    }

    @Test
    public void testRejectedCombination3() throws ParseException {
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString(
                "Effect annotation {s.E} on method run is not a subtype of effects that method produces, which are [t.E];"
                ));
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.rejectedCombination3", Util.stringType(), new StringLiteral("abc"));
    }

    @Test
    public void testRejectedEditor1() throws ParseException {
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString(String.format(
                "The callee method cannot accept actual arguments with types: 'Logger[{network.theEffect}]; "
                )));
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.rejectedEditor1", Util.stringType(), new StringLiteral("abcabc"));
    }

    @Test
    public void testRejectedEditor2() throws ParseException {
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString(
                "Effect annotation {logger2.log} on method main is not a subtype of effects that method produces, which are [logger1.log];"
                ));
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.rejectedEditor2", Util.stringType(), new StringLiteral("abcabc"));
    }

    @Test
    public void testRejectedFunctionSubtype() throws ParseException {
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString(
                "Effect annotation {} on method run is not a subtype of effects that method produces, which are [u.E];"
                ));
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.rejectedFunctionSubtype", Util.stringType(), new StringLiteral("abcabc"));
    }

    @Test
    public void testRejectedHiddenEffects() throws ParseException {
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString(
                "EffectNotInScope"
                ));
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.rejectedHiddenEffects", Util.stringType(), new StringLiteral("abc"));
    }

    @Test
    public void testRejectedNotInferrable() throws ParseException {
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString(
                "Generic argument(s) for the method id were not inferrable and must be provided at the call site"
                ));
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.rejectedNotInferrable", Util.stringType(), new StringLiteral("abc"));
    }

    @Test
    public void testRejectedParametricModuleFunctor1() throws ParseException {
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString(
                "The callee method cannot accept actual arguments with types: 'String; expected types Int"
                ));
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.rejectedParametricModuleFunctor1", Util.stringType(), new StringLiteral("abc"));
    }

    @Test
    public void testRejectedParametricModuleFunctor2() throws ParseException {
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString(
                "Effect annotation {v.E} on method run is not a subtype of effects that method produces, which are [u.E];"
                ));
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.rejectedParametricModuleFunctor2", Util.stringType(), new StringLiteral("abc"));
    }

    @Test
    public void testRejectedSubset() throws ParseException {
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString(
                "Effect annotation {} on method run is not a subtype of effects that method produces, which are [u.E];"
                ));
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.rejectedSubset", Util.stringType(), new StringLiteral("abc"));
    }

    @Test
    public void testRejectedSubtype() throws ParseException {
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString(
                "Generic[{}]; declaration E is not a subtype of the expected declaration"
                ));
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.rejectedSubtype", Util.stringType(), new StringLiteral("abc"));
    }

    @Test
    public void testImport2() throws ParseException {
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString(String.format(
                "The callee method cannot accept actual arguments with types"
        )));
        TestUtil.doTestScriptModularly(PATH, "polymorphicEffects.import2Client", Util.stringType(), new StringLiteral("abcabc"));
    }
}
