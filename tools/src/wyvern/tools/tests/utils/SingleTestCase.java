package wyvern.tools.tests.utils;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import org.junit.Assert;
import wyvern.stdlib.Globals;
import wyvern.tools.parsing.Wyvern;
import wyvern.tools.parsing.transformers.DSLTransformer;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Type;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

public class SingleTestCase implements TestCase {
	public String getName() {
		return name;
	}

	public String getCode() {
		return code;
	}

	public String getExpectedValue() {
		return expectedValue;
	}

	public String getExpectedType() {
		return expectedType;
	}

	private final String name;
	private final String code;
	private final String expectedValue;
	private final String expectedType;

	public SingleTestCase(String name, String code, String expectedValue, String expectedType) {

		this.name = name;
		this.code = code;
		this.expectedValue = expectedValue;
		this.expectedType = expectedType;
	}

	@Override
	public void execute() throws IOException, CopperParserException {
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(code+"\n"), "test input");
		Assert.assertEquals(expectedType, res.typecheck(Globals.getStandardEnv(), Optional.<Type>empty()).toString());
		res = new DSLTransformer().transform(res);
		Value finalV = res.evaluate(Globals.getStandardEvalEnv());
		Assert.assertEquals(expectedValue, finalV.toString());
	}
}
