package wyvern.tools.tests.suites;

import org.junit.experimental.categories.Categories.ExcludeCategory;
import org.junit.experimental.categories.Categories.IncludeCategory;
import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

import wyvern.tools.tests.CopperTests;
import wyvern.tools.tests.FileTestRunner;
import wyvern.tools.tests.TypeCheckingTests;
import wyvern.tools.tests.CoreParserTests;
import wyvern.tools.tests.DemoTests;
import wyvern.tools.tests.LexingTests;
import wyvern.tools.tests.RossettaCodeTests;
import wyvern.tools.tests.CodegenTests;
import wyvern.tools.tests.ILTests;
import wyvern.tools.tests.TypeCheckingTests;
import wyvern.tools.tests.perfTests.PerformanceTests;
import wyvern.tools.tests.tagTests.DynamicTagTests;
import wyvern.tools.tests.tagTests.ExecuteTagTests;
import wyvern.tools.tests.tagTests.ParseTagTests;
import wyvern.tools.tests.tagTests.TypeCheckMatch;
import wyvern.tools.tests.tagTests.TypeCheckTagTests;

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
@SuiteClasses( { DemoTests.class, RossettaCodeTests.class,
				CoreParserTests.class, LexingTests.class,
				TypeCheckTagTests.class, 
				DynamicTagTests.class,
				ParseTagTests.class, ExecuteTagTests.class,
				CopperTests.class, PerformanceTests.class,
				TypeCheckMatch.class, 
				TypeCheckingTests.class,
				CodegenTests.class, ILTests.class,
				})
public class AntRegressionTestSuite {

}
