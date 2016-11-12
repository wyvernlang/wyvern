package wyvern.tools.tests.suites;

import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Categories.ExcludeCategory;
import org.junit.experimental.categories.Categories.IncludeCategory;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

import wyvern.tools.tests.CoreParserTests;
import wyvern.tools.tests.DemoTests;
import wyvern.tools.tests.FFITests;
import wyvern.tools.tests.Figures;
import wyvern.tools.tests.ILTests;
import wyvern.tools.tests.Illustrations;
import wyvern.tools.tests.LexingTests;
import wyvern.tools.tests.ModuleSystemTests;
import wyvern.tools.tests.OIRTests;
import wyvern.tools.tests.RossettaCodeTests;
import wyvern.tools.tests.StdlibTests;

/**
 * This test suite includes all working tests (as of this writing) that
 * can be run successfully with Ant.  Some tests are commented out as
 * they are currently broken.
 * 
 * @author aldrich
 *
 */
@RunWith(Categories.class)
@IncludeCategory(RegressionTests.class)
@ExcludeCategory(CurrentlyBroken.class)
@SuiteClasses(
    {
        // CURRENTLY IMPORTANT TESTS HERE
        LexingTests.class,      // tests the new lexer
        CoreParserTests.class,  // tests the new parser
        ILTests.class,          // tests the new IL
        RossettaCodeTests.class,// a few of these are out of date, but some use new everything
        FFITests.class,
        OIRTests.class,
        Illustrations.class,        // tests the new IL
        Figures.class,        // tests the new IL
        DemoTests.class,        // tests demonstration code
        StdlibTests.class,      // tests the standard library with the new IL
        ModuleSystemTests.class,        // tests the new IL
    }
)
public class AntRegressionTestSuite {

}
