package wyvern.tools.tests;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.expression.StringLiteral;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.suites.CurrentlyBroken;
import wyvern.tools.tests.suites.RegressionTests;

/**
 * Test suite for the effect system.
 */
@Category(RegressionTests.class)
public class EffectSystemTests {
    private static final String PATH = TestUtil.BASE_PATH;

    @BeforeClass public static void setupResolver() {
        TestUtil.setPaths();
        WyvernResolver.getInstance().addPath(PATH);
    }

    @Test
    public void testDataProcessor() throws ParseException {
        /* Involve effect abstraction ("effect process = {net.receive}"). */
        TestUtil.doTestScriptModularly(PATH, "effects.testDataProcessor", Util.stringType(), new StringLiteral(""));
    }

    @Test
    public void testDataProcessor2() throws ParseException {
        /* Involve even more effect abstractions: effect send = {net.send}, effect process = {net.receive, send}. */
        TestUtil.doTestScriptModularly(PATH, "effects.testDataProcessor2", Util.stringType(), new StringLiteral("From dataProcessor2"));
    }

    @Test
    @Category(CurrentlyBroken.class)
    public void testDataProcessor3() throws ParseException {
        /* Shorter version of dataProcessor2 with "effect process = {net.receive, gibberish}". */
        TestUtil.doTestScriptModularly(PATH, "effects.testDataProcessor3", Util.stringType(), new StringLiteral(""));
    }

    @Test
    @Category(CurrentlyBroken.class)
    public void testDataProcessor4() throws ParseException {
        /* Shorter version of dataProcessor2 with a recursive effect definition. */
        TestUtil.doTestScriptModularly(PATH, "effects.testDataProcessor4", Util.stringType(), new StringLiteral(""));
    }

    @Test
    public void testDataProcessor5() throws ParseException {
        /* A module has two effects that have the same name but different paths. */
        TestUtil.doTestScriptModularly(PATH, "effects.testDataProcessor5", Util.stringType(), new StringLiteral("From dataProcessor5"));
    }

    @Test
    @Category(CurrentlyBroken.class)
    public void testDataProcessor6() throws ParseException {
        /* Method processData() has "net.receive" from one of its method calls that it did not annotate. */
        TestUtil.doTestScriptModularly(PATH, "effects.testDataProcessor6", Util.stringType(), new StringLiteral(""));
    }

    @Test
    public void testDataProcessor7() throws ParseException {
        /* Method processData() is annotated with more effects than it actually produces
         * (and does not know that "net.send" is empty). */
        TestUtil.doTestScriptModularly(PATH, "effects.testDataProcessor7", Util.stringType(), new StringLiteral(""));
    }

    @Test
    public void testDummy() throws ParseException {
        /* Does not use any outside objects/types or functions */
        TestUtil.doTestScriptModularly(PATH, "effects.dummyTest", Util.stringType(), new StringLiteral("dummyDef.m3()"));
    }

    @Test
    public void testDummyTaker() throws ParseException {
        /* Does not use any outside objects/types or functions other than dummyDef, which itself doesn't use any
         * outside objects/types or functions. */
        TestUtil.doTestScriptModularly(PATH, "effects.dummyTakerTest", Util.stringType(), new StringLiteral("dummyTakerDef.m5()"));
    }

    @Test
    public void testFileIO1() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "effects.testFileIO1", Util.intType(), new IntegerLiteral(3));
    }

    @Test
    public void testFileIO2() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "effects.testFileIO2", Util.intType(), new IntegerLiteral(3));
    }

    @Test
    public void testImportTypeBug() throws ParseException {
        /* Object notation with no effect annotations. */
        TestUtil.doTestScriptModularly(PATH, "effects.dummyBug", Util.intType(), new IntegerLiteral(1));
    }

    @Test
    public void testNetwork() throws ParseException {
        /* Declared in type and module def; defined in module def; method annotations in both. */
        TestUtil.doTestScriptModularly(PATH, "effects.testNetwork", Util.stringType(), new StringLiteral("Network with effects"));
    }

    @Test
    public void testNetwork1() throws ParseException {
        /* Type and module def with no annotations. */
        TestUtil.doTestScriptModularly(PATH, "effects.testNetwork1", Util.stringType(), new StringLiteral("Network1 without effects"));
    }

    @Test
    @Category(CurrentlyBroken.class)
    public void testNetwork2() throws ParseException {
        /* No effect declarations. Undefined method annotations in module def (doesn't match the valid type signature,
         * to isolate testing for just method-checking in module def). */
        TestUtil.doTestScriptModularly(PATH, "effects.testNetwork2", Util.stringType(), new StringLiteral("Network2 with effects"));
    }

    @Test
    public void testNetwork3() throws ParseException {
        /* In addition to declarations (not defined) and method annotations in type,
         * additional declaration and definition in module def. */
        TestUtil.doTestScriptModularly(PATH, "effects.testNetwork3", Util.stringType(), new StringLiteral("Network3 with effects"));
    }

    @Test
    @Category(CurrentlyBroken.class)
    public void testNetwork4() throws ParseException {
        /* Parse error due to an effect annotation not being enclosed in "{}" in a method annotation;
         * also the effect is undefined. */
        TestUtil.doTestScriptModularly(PATH, "effects.testNetwork4", Util.stringType(), new StringLiteral("Network4 with effects"));
    }

    @Test
    @Category(CurrentlyBroken.class)
    public void testNetwork5() throws ParseException {
        /* Parse error due to an effect annotation not being enclosed in "{}" in a method annotation in a module;
         * however, the effect is defined. */
        TestUtil.doTestScriptModularly(PATH, "effects.testNetwork5", Util.stringType(), new StringLiteral("Network5 with effects"));
    }

    @Test
    @Category(CurrentlyBroken.class)
    public void testNetwork6() throws ParseException {
        /* Parse error due to an effect in the right-hand side of an effect definition not being enclosed in "{}" in a module. */
        TestUtil.doTestScriptModularly(PATH, "effects.testNetwork6", Util.stringType(), new StringLiteral("Network6 with effects"));
    }

    @Test
    public void testNetwork7() throws ParseException {
        /* Two effect declarations, one of which is defined in type; method annotations in both type and module. */
        TestUtil.doTestScriptModularly(PATH, "effects.testNetwork7", Util.stringType(), new StringLiteral("Network7 with effects"));
    }

    @Test
    @Category(CurrentlyBroken.class)
    public void testNetwork8() throws ParseException {
        /* Invalid effect (actually DSL block instead) due to the module having "effect receive = {{}}". */
        TestUtil.doTestScriptModularly(PATH, "effects.testNetwork8", Util.stringType(), new StringLiteral("Network8 with effects"));
    }

    @Test
    public void testNetwork9() throws ParseException {
        /* No method annotations despite effect declarations in type and module. */
        TestUtil.doTestScriptModularly(PATH, "effects.testNetwork9", Util.stringType(), new StringLiteral("Network9 with effects"));
    }

    @Test
    public void testNetwork10() throws ParseException {
        /* Globally available effects defined in a pure module. */
        TestUtil.doTestScriptModularly(PATH, "effects.testNetwork10", Util.stringType(), new StringLiteral("Network10 with effects"));
    }

    @Test
    public void testNetwork11() throws ParseException {
        /* All effects defined in type and module. */
        TestUtil.doTestScriptModularly(PATH, "effects.testNetwork11", Util.stringType(), new StringLiteral("Network11 with effects"));
    }

    @Test
    @Category(CurrentlyBroken.class)
    public void testNetwork12() throws ParseException {
        /* Parse error due to an effect being undefined in a module. */
        TestUtil.doTestScriptModularly(PATH, "effects.testNetwork12", Util.stringType(), new StringLiteral("Network12 with effects"));
    }

    @Test
    @Category(CurrentlyBroken.class)
    public void testNetwork13() throws ParseException {
        /* Nonexistent effect in method annotation in type (not in module,
         * but error should be reported before module is evaluated). */
        TestUtil.doTestScriptModularly(PATH, "effects.testNetwork13", Util.stringType(), new StringLiteral("Network13 with effects"));
    }

    @Test
    @Category(CurrentlyBroken.class)
    public void testNetwork14() throws ParseException {
        /* Int included as effect in module annotation of type (not in module,
         * but error should be reported before module is evaluated). */
        TestUtil.doTestScriptModularly(PATH, "effects.testNetwork14", Util.stringType(), new StringLiteral("Network14 with effects"));
    }

    @Test
    @Category(CurrentlyBroken.class)
    public void testNetwork15() throws ParseException {
        /* Bogus right-hand side of an effect definition in a module;
         * the effect was left undefined in the type and isn't actually used
         * in any method annotation (so that we can be sure that the checking is happening upon definition). */
        TestUtil.doTestScriptModularly(PATH, "effects.testNetwork15", Util.stringType(), new StringLiteral("Network15 with effects"));
    }

    @Test
    @Category(CurrentlyBroken.class)
    public void testNetwork16() throws ParseException {
        /* Trying to use a nonexistent effect of an object in scope in a module;
         * the effect was left undefined in the type and is not actually used
         * in any method annotation (so that we can be sure that the checking is happening upon definition). */
        TestUtil.doTestScriptModularly(PATH, "effects.testNetwork16", Util.stringType(), new StringLiteral("Network16 with effects"));
    }

    @Test
    @Category(CurrentlyBroken.class)
    public void testNetwork17() throws ParseException {
        /* Incorrect effect definition (effect receive = {something}, should report error here)
         * and method annotation (def sendData(data : String) : {error} Unit) -- both in type signature *only*,
         * to isolate testing in type signature. */
        TestUtil.doTestScriptModularly(PATH, "effects.testNetwork17", Util.stringType(), new StringLiteral("Network17 with effects"));
    }

    @Test
    public void testObjNetwork() throws ParseException {
        /* Object with effect annotations. */
        TestUtil.doTestScriptModularly(PATH, "effects.objNetwork", Util.stringType(), new StringLiteral("ObjNetwork with effects"));
    }
}
