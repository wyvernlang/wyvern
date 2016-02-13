package wyvern.tools.tests.suites;

import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

import wyvern.tools.tests.CoreParserTests;
import wyvern.tools.tests.DemoTests;
import wyvern.tools.tests.ILTests;
import wyvern.tools.tests.LexingTests;
import wyvern.tools.tests.RossettaCodeTests;

@RunWith(Categories.class)
@SuiteClasses( {
		DemoTests.class, RossettaCodeTests.class, CoreParserTests.class,
		LexingTests.class, ILTests.class,
	})
public class NewParserTestSuite {

}
