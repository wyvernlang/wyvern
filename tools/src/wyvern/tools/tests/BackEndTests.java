package wyvern.tools.tests;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.tools.BytecodeCompiler;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.suites.RegressionTests;

/**
 * Runs the Wyvern compiler on some of the source code in the wyvern/backend
 * directory tree
 *
 * @author aldrich
 *
 */
@Category(RegressionTests.class)
public class BackEndTests {
  private static final String PATH = TestUtil.BACKEND_PATH;

  @Before
  public void setup() {
    Globals.resetState();
  }

  @BeforeClass
  public static void setupResolver() {
    TestUtil.setPaths();
    WyvernResolver.getInstance().addPath(PATH);
  }

  @Test
  public void testVerifier() throws ParseException {
      // generate bytecode for examples/verification/verifyTest.wyv
      BytecodeCompiler.wyvernHome.set(TestUtil.WYVERN_HOME);
      BytecodeCompiler.main(new String[] {"../examples/verification/verifyTest.wyv"});
      // run the verifier
      TestUtil.doTestScriptModularly(PATH, "verifierTest", Util.intType(),
          Util.intValue(17));
  }
  
}
