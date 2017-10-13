package wyvern.tools.tests.suites;

import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Categories.ExcludeCategory;
import org.junit.experimental.categories.Categories.IncludeCategory;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;


/**
 * This test suite includes all working tests (as of this writing).  It
 * adds to the Ant test suites those that work in Eclipse.
 * 
 * @author aldrich
 *
 */
@RunWith(Categories.class)
@IncludeCategory(RegressionTests.class)
@ExcludeCategory(CurrentlyBroken.class)
@SuiteClasses( { AntRegressionTestSuite.class,
				})
public class RegressionTestSuite {

}
