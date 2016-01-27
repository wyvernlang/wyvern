package wyvern.tools.tests.utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Assert;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import wyvern.stdlib.Globals;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.parsing.Wyvern;
import wyvern.tools.parsing.transformers.DSLTransformer;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Type;
import wyvern.tools.util.Pair;

public class ModuleTestCase implements TestCase {

	private final List<Pair<String, String>> modules;
	private final String name;

	private final String expectedType;
	private final String expectedValue;

	public ModuleTestCase(String name, Pair<String, String> expected, List<Pair<String,String>> modules) {
		this.name = name;
		this.modules = modules;
		expectedType = expected.second;
		expectedValue = expected.first;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void execute() throws IOException, CopperParserException {
		Map<String, String> mapped = modules.stream().collect(Collectors.toMap(pair->pair.first, pair->pair.second));
		String main = mapped.get("main");
		if (main == null)
			throw new RuntimeException("Cannot find a main file - did you forget to put in a \"main\" file?");

		WyvernResolver.clearFiles();
		for (Pair<String,String> pair : modules) {
			WyvernResolver.addFile(pair.first, pair.second+"\n");
		}

		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(main), "test input");
		Assert.assertEquals(expectedType, res.typecheck(Globals.getStandardEnv(), Optional.<Type>empty()).toString());
		res = new DSLTransformer().transform(res);
		Value finalV = res.evaluate(Globals.getStandardEvalEnv());
		Assert.assertEquals(expectedValue, finalV.toString());
	}
}
