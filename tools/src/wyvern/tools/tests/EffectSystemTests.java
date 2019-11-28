package wyvern.tools.tests;

import java.nio.file.Paths;

import org.hamcrest.core.StringContains;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.expression.StringLiteral;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.tools.errors.ToolError;
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
        Globals.setUsePrelude(false);
    }

    @AfterClass public static void teardown() {
        Globals.setUsePrelude(true);  // restore the default to use the prelude
    }


    @Rule
    public ExpectedException expectedException = ExpectedException.none();

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
    public void testDataProcessor3() throws Exception {
        /* Undefined effect is used in the right-hand side of an effect definition. */
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString("Effect \"gibberish\" is undefined at location file "
                + Paths.get(PATH, "effects", "dataProcessor3.wyv").toAbsolutePath().toString()));
        TestUtil.doTestScriptModularly(PATH, "effects.testDataProcessor3", Util.stringType(), new StringLiteral(""));
    }

    @Test
    public void testDataProcessor4() throws Exception {
        /* A recursive effect definition. */
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString("Effect \"process\" is being defined recursively at location file "
                + Paths.get(PATH, "effects", "dataProcessor4.wyv").toAbsolutePath().toString()));
        TestUtil.doTestScriptModularly(PATH, "effects.testDataProcessor4", Util.stringType(), new StringLiteral(""));
    }

    @Test
    public void testDataProcessor5() throws ParseException {
        /* A module has two effects that have the same name but different paths. */
        TestUtil.doTestScriptModularly(PATH, "effects.testDataProcessor5", Util.stringType(), new StringLiteral("From dataProcessor5"));
    }

    @Test
    public void testDataProcessor6() throws ParseException {
        /* Method effect annotation is missing an effect. */
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString("Effect annotation {"));
        expectedException.expectMessage(StringContains.containsString(".genN} on method processData is not a subtype of "
                + "effects that method produces, which are ["));
        expectedException.expectMessage(StringContains.containsString("net.receive"));
        expectedException.expectMessage(StringContains.containsString(".genN"));
        expectedException.expectMessage(StringContains.containsString("];  at location file "
                + Paths.get(PATH, "effects", "dataProcessor6.wyv").toAbsolutePath().toString()));
        TestUtil.doTestScriptModularly(PATH, "effects.testDataProcessor6", Util.stringType(), new StringLiteral(""));
    }

    @Test
    public void testDataProcessor7() throws ParseException {
        /* Method processData() is annotated with more effects than it actually produces
         * (and does not know that "net.send" is empty). */
        TestUtil.doTestScriptModularly(PATH, "effects.testDataProcessor7", Util.stringType(), new StringLiteral(""));
    }

    @Test
    public void testBasic() throws ParseException {
        /* Does not use any outside objects, types, or functions */
        TestUtil.doTestScriptModularly(PATH, "effects.testBasic", Util.stringType(), new StringLiteral("basic.m3()"));
    }

    @Test
    public void testBasic1() throws ParseException {
        /* Uses a module that doesn't use any outside objects, types, or functions. */
        TestUtil.doTestScriptModularly(PATH, "effects.testBasic1", Util.stringType(), new StringLiteral("basic1.m5()"));
    }

    @Test
    public void testFileIO() throws ParseException {
        /* Globally available effects (system.FFI) are used in effect definitions in module (only). */
        TestUtil.doTestScriptModularly(PATH, "effects.testFileIO", Util.intType(), new IntegerLiteral(3));
    }

    @Test
    public void testFileIO1() throws ParseException {
        /* Effects defined in a pure module are used in effect definitions in module (only). */
        TestUtil.doTestScriptModularly(PATH, "effects.testFileIO1", Util.intType(), new IntegerLiteral(3));
    }

    @Test
    public void testFileIO2() throws ParseException {
        /* Effects defined in a pure module are used in effect definitions in both type and module. */
        TestUtil.doTestScriptModularly(PATH, "effects.testFileIO2", Util.intType(), new IntegerLiteral(3));
    }

    @Test
    public void testFileIO3() throws ParseException {
        /* Effect names may be the same as method names. */
        TestUtil.doTestScriptModularly(PATH, "effects.testFileIO3", Util.intType(), new IntegerLiteral(3));
    }

    @Test
    public void testFileIO5() throws ParseException {
        /* Effects defined in a pure module are used in effect definitions in both type and module.
         * Pure module imported in type is renamed. */
        TestUtil.doTestScriptModularly(PATH, "effects.testFileIO5", Util.intType(), new IntegerLiteral(3));
    }

    @Test
    public void testLogger() throws ParseException {
        /* A method has an effect annotation involving a globally available effect (system.FFI)
         * in module but not in type. */
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString("Method body's type resource type"));
        expectedException.expectMessage(StringContains.containsString("is not a subtype of declared type "
                + "effects.Logger.Logger; declaration updateLog is not a subtype of the expected declaration at location file "
                + Paths.get(PATH, "effects", "logger.wyv").toAbsolutePath().toString()));
        TestUtil.doTestScriptModularly(PATH, "effects.testLogger", Util.intType(), new IntegerLiteral(5));
    }

    @Test
    public void testLogger1() throws ParseException {
        /* Globally available effect (system.FFI) is attempted to be used to annotate a method directly
         * without having exposed the effect definition in the type. */
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString("Effect annotation {system.FFI} "
                + "on method updateLog is not a subtype of effects that method produces, which are [fio.writeF];  at location file "
                + Paths.get(PATH, "effects", "logger1.wyv").toAbsolutePath().toString()));
        TestUtil.doTestScriptModularly(PATH, "effects.testLogger1", Util.intType(), new IntegerLiteral(5));
    }

    @Test
    public void testLogger2() throws ParseException {
        /* A method has an effect annotation, involving a passed in resource, in module but not in type. */
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString("Method body's type resource type"));
        expectedException.expectMessage(StringContains.containsString("is not a subtype of declared type "
                + "effects.Logger2.Logger2; declaration updateLog is not a subtype of the expected declaration at location file "
                + Paths.get(PATH, "effects", "logger2.wyv").toAbsolutePath().toString()));
        TestUtil.doTestScriptModularly(PATH, "effects.testLogger2", Util.intType(), new IntegerLiteral(5));
    }

    @Test
    public void testLogger3() throws ParseException {
        /* Uses a resource passed as a method argument in the method's effect annotation. */
        TestUtil.doTestScriptModularly(PATH, "effects.testLogger3", Util.intType(), new IntegerLiteral(2));
    }

    @Test
    public void testLogger4() throws ParseException {
        /* Globally available effect (system.FFI) is used to annotate a method directly
         * without defining any local effects. */
        TestUtil.doTestScriptModularly(PATH, "effects.testLogger4", Util.intType(), new IntegerLiteral(5));
    }

    @Test
    public void testLogger5() throws ParseException {
        /* Effect subtyping: accepting a module with fewer effects than in the expected type. */
        TestUtil.doTestScriptModularly(PATH, "effects.testLogger5", Util.intType(), new IntegerLiteral(6));
    }

    @Test
    public void testNested() throws ParseException {
        /* An object in a module's immutable field defines an effect
         * and uses it in a method annotation inside that object. */
        TestUtil.doTestScriptModularly(PATH, "effects.testNested", Util.intType(), new IntegerLiteral(1));
    }

    @Test
    @Category(CurrentlyBroken.class)
    public void testNested2() throws ParseException {
        /* An object in a module's immutable field defines an effect
         * and uses it in a method annotation inside that object.
         * Then that effect is used from outside the module. */
        TestUtil.doTestScriptModularly(PATH, "effects.testNested2", Util.intType(), new IntegerLiteral(1));
    }

    @Test
    public void testNetwork() throws ParseException {
        /* Declared in type and module; defined in module; method annotations in both. */
        TestUtil.doTestScriptModularly(PATH, "effects.testNetwork", Util.stringType(), new StringLiteral("Network with effects"));
    }

    @Test
    public void testNetwork1() throws ParseException {
        /* Type and module with no annotations. */
        TestUtil.doTestScriptModularly(PATH, "effects.testNetwork1", Util.stringType(), new StringLiteral("Network1 without effects"));
    }

    @Test
    public void testNetwork2() throws ParseException {
        /* No effect declarations. Undefined method annotations in module (doesn't match the valid type signature,
         * to isolate testing for just method-checking in module). */
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString("Effect \"something\" is undefined at location file "
                + Paths.get(PATH, "effects", "network2.wyv").toAbsolutePath().toString()));
        TestUtil.doTestScriptModularly(PATH, "effects.testNetwork2", Util.stringType(), new StringLiteral("Network2 with effects"));
    }

    @Test
    public void testNetwork3() throws ParseException {
        /* In addition to declarations (not defined) and method annotations in type,
         * additional declaration and definition in module. */
        TestUtil.doTestScriptModularly(PATH, "effects.testNetwork3", Util.stringType(), new StringLiteral("Network3 with effects"));
    }

    @Test
    public void testNetwork4() throws ParseException {
        /* Parse error due to an effect annotation not being enclosed in "{}" in a method annotation;
         * also the effect is undefined. */
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString("Parse error: Encountered \"String\""));
        expectedException.expectMessage(StringContains.containsString("Was expecting one of:"));
        expectedException.expectMessage(StringContains.containsString(
                Paths.get(PATH, "effects", "NetworkType4.wyt").toAbsolutePath().toString()));
        TestUtil.doTestScriptModularly(PATH, "effects.testNetwork4", Util.stringType(), new StringLiteral("Network4 with effects"));
    }

    @Test
    public void testNetwork5() throws ParseException {
        /* Parse error due to an effect annotation not being enclosed in "{}" in a method annotation in a module;
         * however, the effect is defined. */
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString("Parse error: Encountered \"Unit\""));
        expectedException.expectMessage(StringContains.containsString("Was expecting one of:"));
        expectedException.expectMessage(StringContains.containsString(
                Paths.get(PATH, "effects", "network5.wyv").toAbsolutePath().toString()));
        TestUtil.doTestScriptModularly(PATH, "effects.testNetwork5", Util.stringType(), new StringLiteral("Network5 with effects"));
    }

    @Test
    public void testNetwork6() throws ParseException {
        /* Parse error due to an effect in the right-hand side of an effect definition not being enclosed in "{}" in a module. */
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString("Parse error: Encountered \"something\""));
        expectedException.expectMessage(StringContains.containsString("Was expecting:"));
        expectedException.expectMessage(StringContains.containsString(
                Paths.get(PATH, "effects", "network6.wyv").toAbsolutePath().toString()));
        TestUtil.doTestScriptModularly(PATH, "effects.testNetwork6", Util.stringType(), new StringLiteral("Network6 with effects"));
    }

    @Test
    public void testNetwork7() throws ParseException {
        /* Two effect declarations, one of which is defined in type; method annotations in both type and module. */
        TestUtil.doTestScriptModularly(PATH, "effects.testNetwork7", Util.stringType(), new StringLiteral("Network7 with effects"));
    }

    @Test
    public void testNetwork8() throws ParseException {
        /* Invalid effect (actually DSL block instead) due to the module having "effect receive = {{}}". */
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString("Invalid characters for effect--should not be a DSL block: "
                + "\"effect receive = {{}}\" at location file "));
        expectedException.expectMessage(StringContains.containsString(
                Paths.get(PATH, "effects", "network8.wyv").toAbsolutePath().toString()));
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
    public void testNetwork12() throws ParseException {
        /* Parse error due to an effect being undefined in a module. */
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString("Parse error: Encountered \"\""));
        expectedException.expectMessage(StringContains.containsString("Was expecting:"));
        expectedException.expectMessage(StringContains.containsString(
                Paths.get(PATH, "effects", "network12.wyv").toAbsolutePath().toString()));
        TestUtil.doTestScriptModularly(PATH, "effects.testNetwork12", Util.stringType(), new StringLiteral("Network12 with effects"));
    }

    @Test
    public void testNetwork13() throws ParseException {
        /* Nonexistent effect in method annotation in type (not in module);
         * error should be reported before module is evaluated). */
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString("Effect \"unknown\" is undefined"));
        TestUtil.doTestScriptModularly(PATH, "effects.testNetwork13", Util.stringType(), new StringLiteral("Network13 with effects"));
    }

    @Test
    public void testNetwork14() throws ParseException {
        /* A field is used in a method effect annotation in a type. */
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString("Effect \"n\" is undefined"));
        TestUtil.doTestScriptModularly(PATH, "effects.testNetwork14", Util.stringType(), new StringLiteral("Network14 with effects"));
    }

    @Test
    public void testNetwork15() throws ParseException {
        /* Bogus right-hand side of an effect definition in a module;
         * the effect was left undefined in the type and isn't actually used
         * in any method annotation (so that we can be sure that the checking is happening upon definition). */
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString("Effect \"something\" is undefined at location file "
                + Paths.get(PATH, "effects", "network15.wyv").toAbsolutePath().toString()));
        TestUtil.doTestScriptModularly(PATH, "effects.testNetwork15", Util.stringType(), new StringLiteral("Network15 with effects"));
    }

    @Test
    public void testNetwork16() throws ParseException {
        /* Trying to use a nonexistent effect of an object in scope in a module;
         * the effect was left undefined in the type and is not actually used
         * in any method annotation (so that we can be sure that the checking is happening upon definition). */
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString("Effect \"stdout.hi\" not found in scope at location file "
                + Paths.get(PATH, "effects", "network16.wyv").toAbsolutePath().toString()));
        Globals.setUsePrelude(true);
        TestUtil.doTestScriptModularly(PATH, "effects.testNetwork16", Util.stringType(), new StringLiteral("Network16 with effects"));
        Globals.setUsePrelude(false);
    }

    @Test
    public void testNetwork17() throws ParseException {
        /* Incorrect effect definition (effect receive = {something}, should report error here)
         * and method annotation (def sendData(data : String) : {error} Unit) -- both in type signature *only*,
         * to isolate testing in type signature. */
        expectedException.expect(ToolError.class);
        expectedException.expectMessage(StringContains.containsString("Effect \"something\" is undefined at location file "
                + Paths.get(PATH, "effects", "NetworkType17.wyt").toAbsolutePath().toString()));
        TestUtil.doTestScriptModularly(PATH, "effects.testNetwork17", Util.stringType(), new StringLiteral("Network17 with effects"));
    }

    @Test
    public void testNetwork18() throws ParseException {
        /* An effect defined in a pure module is used in a method annotation in the same module. */
        TestUtil.doTestScriptModularly(PATH, "effects.testNetwork18", Util.stringType(), new StringLiteral("Network18 with effects"));
    }

    @Test
    public void testNetwork19() throws ParseException {
        /* An effect defined in a pure module is used in another effect definition in the same module. */
        TestUtil.doTestScriptModularly(PATH, "effects.testNetwork19", Util.stringType(), new StringLiteral("Network19 with effects"));
    }

    @Test
    public void testObjNetwork() throws ParseException {
        /* Object with effect annotations. */
        TestUtil.doTestScriptModularly(PATH, "effects.objNetwork", Util.stringType(), new StringLiteral("ObjNetwork with effects"));
    }


}
