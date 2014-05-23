package wyvern.tools.tests;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import wyvern.stdlib.Globals;
import wyvern.tools.parsing.Wyvern;
import wyvern.tools.parsing.transformers.DSLTransformer;
import wyvern.tools.tests.utils.TestCase;
import wyvern.tools.tests.utils.TestSuiteParser;
import wyvern.tools.typedAST.core.expressions.Invocation;
import wyvern.tools.typedAST.extensions.interop.java.Util;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Type;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.List;
import java.util.Optional;

@RunWith(Parameterized.class)
public class FileTestRunner {
	private static String filename = "wyvern/tools/tests/basic.test";

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		TestSuiteParser tsp = new TestSuiteParser();
		List<TestCase> cases = null;
		try {
			cases = (List<TestCase>) tsp.parse(new InputStreamReader(FileTestRunner.class.getClassLoader().getResourceAsStream(filename)),
					filename);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return cases.stream().map(caze->new Object[] {caze.getName(), caze.getCode(), caze.getExpectedValue(), caze.getExpectedType()})::iterator;
	}
	private final String name;
	private final String code;
	private final String expectedVal;
	private final String expectedType;

	public FileTestRunner(String name, String code, String expectedVal, String expectedType) {
		this.name = name;
		this.code = code;
		this.expectedVal = expectedVal;
		this.expectedType = expectedType;
	}

	@Test
	public void test() throws IOException, CopperParserException {
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(code), "test input");
		Assert.assertEquals(expectedType, res.typecheck(Globals.getStandardEnv(), Optional.<Type>empty()).toString());
		res = new DSLTransformer().transform(res);
		Value finalV = res.evaluate(Globals.getStandardEnv());
		Assert.assertEquals(expectedVal, finalV.toString());
	}
}
