package wyvern.tools.tests;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import wyvern.tools.tests.utils.SingleTestCase;
import wyvern.tools.tests.utils.TestCase;
import wyvern.tools.tests.utils.TestSuiteParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RunWith(Parameterized.class)
public class FileTestRunner {
	private static String fileRoot = "wyvern/tools/tests/embedded/";

	private static String[] files = new String[] { "basic.test", "class.test", "tsl.test", "module.test", "parselang.test", "typechecking.test"};


	private static Predicate<TestCase> casePredicate = (cas) -> true;

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		List<TestCase> cases = Arrays.asList(files).stream().map(file->fileRoot + file).map(file -> {
			try (InputStream is = FileTestRunner.class.getClassLoader().getResourceAsStream(file)) {
				try (InputStreamReader isr = new InputStreamReader(is)) {
					return (List<TestCase>)(new TestSuiteParser().parse(isr, file));
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}).flatMap(icases -> icases.stream()).collect(Collectors.toList());

		return cases.stream().filter(casePredicate)
				.map(caze -> new Object[]{caze.getName(), caze})::iterator;
	}

	private final String name;
	private final TestCase testCase;

	public FileTestRunner(String name, TestCase testCase) {
		this.name = testCase.getName();
		this.testCase = testCase;
	}

	@Test
	public void test() throws IOException, CopperParserException {
		testCase.execute();
	}
}
