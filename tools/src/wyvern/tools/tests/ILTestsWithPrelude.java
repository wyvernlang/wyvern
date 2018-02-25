package wyvern.tools.tests;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.suites.RegressionTests;

@Category(RegressionTests.class)
public class ILTestsWithPrelude {

    @BeforeClass public static void setupResolver() {
        TestUtil.setPaths();
        WyvernResolver.getInstance().addPath(ILTests.PATH);
    }

    @Test
    public void testIf() throws ParseException {
        TestUtil.doTestScriptModularly("tsls.cleanIf", Util.intType(), new IntegerLiteral(5));
    }
}
