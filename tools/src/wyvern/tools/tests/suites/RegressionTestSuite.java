package wyvern.tools.tests.suites;

import org.junit.experimental.categories.Categories.ExcludeCategory;
import org.junit.experimental.categories.Categories.IncludeCategory;
import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

import wyvern.targets.TargetManager;
import wyvern.targets.Common.wyvernIL.IL.tests.TestIL;
import wyvern.targets.Common.wyvernIL.interpreter.tests.TestBasics;
import wyvern.targets.Common.wyvernIL.interpreter.tests.TestClass;
import wyvern.targets.Common.wyvernIL.interpreter.tests.TestFunctions;
import wyvern.targets.Common.wyvernIL.interpreter.tests.TestOverall;
import wyvern.tools.tests.CopperTests;
import wyvern.tools.tests.FileTestRunner;
import wyvern.tools.tests.TypeCheckingTests;
import wyvern.tools.tests.perfTests.PerformanceTests;
import wyvern.tools.tests.tagTests.DynamicTagTests;
import wyvern.tools.tests.tagTests.ExecuteTagTests;
import wyvern.tools.tests.tagTests.ParseTagTests;
import wyvern.tools.tests.tagTests.TypeCheckMatch;
import wyvern.tools.tests.tagTests.TypeCheckTagTests;

@RunWith(Categories.class)
@IncludeCategory(RegressionTests.class)
@ExcludeCategory(CurrentlyBroken.class)
@SuiteClasses( { //DemoTests.class, RossettaCodeTests.class,
				//CoreParserTests.class, LexingTests.class,
				TypeCheckTagTests.class, TestOverall.class,
				TestClass.class, TargetManager.class,
				TestIL.class, TestFunctions.class,
				DynamicTagTests.class, FileTestRunner.class,
				ParseTagTests.class, ExecuteTagTests.class,
				CopperTests.class, PerformanceTests.class,
				TypeCheckMatch.class, TestBasics.class,
				TypeCheckingTests.class
				})
public class RegressionTestSuite {

}
