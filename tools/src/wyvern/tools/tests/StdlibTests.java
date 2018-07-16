package wyvern.tools.tests;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import wyvern.stdlib.Globals;
import wyvern.stdlib.support.WyvernAssertion;
import wyvern.target.corewyvernIL.expression.StringLiteral;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.suites.CurrentlyBroken;
import wyvern.tools.tests.suites.RegressionTests;
import wyvern.tools.errors.ToolError;

@Category(RegressionTests.class)
public class StdlibTests {
    /*@Before
    public void setup() {
        Globals.resetPrelude();
    }*/
    @Test
    public void testRegex() throws ParseException {
        String input = ""
                + "import wyvern.Int\n"
                + "import wyvern.option\n"
                + "import wyvern.util.matching.regex\n\n"
                + "val r = regex(\"\\\\d\")\n"
                + "val threeString = r.findPrefixOf(\"3 men in a tub\")\n"
                + "Int.from(threeString.getOrElse(() => \"5\"))";
        TestUtil.doTestInt(input, 3);
    }

    @Test
    public void testRegexLexer() throws ParseException {
        TestUtil.doTestScriptModularly("tsls.testLexer", Util.stringType(), new StringLiteral("*"));
    }

    @Test
    public void testParser() throws ParseException {
        TestUtil.doTestScriptModularly("tsls.testParser", null, null);
    }

    @Test
    public void testArrayList() throws ParseException {
        TestUtil.doTestScriptModularly("stdlib.platform.java.arraylistTest", null, null);
    }

    @Test(expected = ToolError.class)
    public void testArrayListTypeSafety() throws ParseException {
        TestUtil.doTestScriptModularly("stdlib.platform.java.arraylistTypeSafetyTest", null, null);
    }

    @Test(expected = WyvernAssertion.class)
    public void testAssertions() throws ParseException {
        TestUtil.doTestScriptModularly("stdlib.assertionTest", null, null);
    }

    @Test
    @Category(CurrentlyBroken.class)
    public void testLinkedList() throws ParseException {
        TestUtil.doTestScriptModularly("stdlib.collections.linkedListTest", null, null);
    }

}
