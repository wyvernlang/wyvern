package wyvern.tools.tests;

import org.hamcrest.core.StringContains;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.expression.BooleanLiteral;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.expression.StringLiteral;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.tools.errors.ToolError;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.suites.RegressionTests;

/**
 * Runs the Wyvern compiler on the example source code in the wyvern/examples
 * directory tree
 *
 * @author aldrich
 *
 */
@Category(RegressionTests.class)
public class ExamplesNoPrelude {
  private static final String PATH = TestUtil.EXAMPLES_PATH;

  /*@Before
  public void setup() {
    Globals.resetPrelude();
  }*/

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

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
  public void testDatatypes() throws ParseException {
    TestUtil.doTestScriptModularly(PATH, "introductory.datatypes",
        Util.stringType(), new StringLiteral("(x => x) unit"));
  }

  @Test
  public void testImmutability() throws ParseException {
      expectedException.expect(ToolError.class);
      expectedException.expectMessage(StringContains.containsString("This type must be a resource type"));

        TestUtil.doTestScriptModularly(PATH, "introductory.immutability", Util.intType(),
                new IntegerLiteral(15));
  }

  @Test
  public void testBox() throws ParseException {
    TestUtil.doTestScriptModularly(PATH, "introductory.box", Util.intType(),
        new IntegerLiteral(15));
  }

  @Test
  public void testKeys() throws ParseException {
    TestUtil.doTestScriptModularly(PATH, "introductory.keys", Util.stringType(),
        new StringLiteral("m1"));
  }

  @Test
  public void testObjects() throws ParseException {
    TestUtil.doTestScriptModularly(PATH, "introductory.objects", Util.intType(),
        new IntegerLiteral(7));
  }

  @Test
  public void testStrings() throws ParseException {
    TestUtil.doTestScriptModularly(PATH, "introductory.strings",
        Util.booleanType(), new BooleanLiteral(true));
  }

  @Test
  public void testCell() throws ParseException {
    TestUtil.doTestScriptModularly(PATH, "introductory.cell", Util.intType(),
        new IntegerLiteral(3));
  }

  @Test
  public void testCore() throws ParseException {
    TestUtil.doTestScriptModularly(PATH, "introductory.core", Util.intType(),
        new IntegerLiteral(3));
  }

  @Test
  public void testMaybe() throws ParseException {
    TestUtil.doTestScriptModularly(PATH, "introductory.maybe", Util.intType(),
        new IntegerLiteral(15));
  }

  @Test
  public void testDebug() throws ParseException {
    TestUtil.doTestScriptModularly(PATH, "introductory.testDebug",
        Util.unitType(), Util.unitValue());
  }

  @Test
  public void testCodeCompletion() throws ParseException {
    TestUtil.doTestScriptModularly(PATH, "effects.codeCompletion", null, null);
  }

  @Test
  public void testCodeCompletion2() throws ParseException {
    TestUtil.doTestScriptModularly(PATH, "effects.codeCompletion2", null, null);
  }

  @Test
  public void testLogger() throws ParseException {
    TestUtil.doTestScriptModularly(PATH, "effects.logger", null, null);
  }

  @Test
  public void testLogger2() throws ParseException {
    TestUtil.doTestScriptModularly(PATH, "effects.logger2", null, null);
  }

  @Test
  public void testRemoteLogger() throws ParseException {
    TestUtil.doTestScriptModularly(PATH, "effects.remoteLogger", null, null);
  }

  @Test
  public void testUserStats() throws ParseException {
    TestUtil.doTestScriptModularly(PATH, "effects.userStats", null, null);
  }

  @Test
  public void testUserStats2() throws ParseException {
    TestUtil.doTestScriptModularly(PATH, "effects.userStats2", null, null);
  }

  @Test
  public void testTwice() throws ParseException {
    TestUtil.doTestScriptModularly(PATH, "effects.twice", null, null);
  }

}
