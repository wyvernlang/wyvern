package wyvern.tools.tests;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.suites.CurrentlyBroken;
import wyvern.tools.tests.suites.RegressionTests;

@Category(RegressionTests.class)
public class StdlibTests {
	@Test
	public void testRegex() throws ParseException {
		String input = ""
				 + "import wyvern.Int\n"
				 + "import wyvern.option\n"
				 + "import wyvern.util.matching.regex\n\n"
				 + "val r = regex(\"\\d\")\n"
				 + "val threeString = r.findPrefixOf(\"3 men in a tub\")\n"
				 + "Int.from(threeString.orElse(5))"
				 ;
		ILTests.doTestInt(input, 3);
	}

}
