package wyvern.tools.tests;

import java.io.IOException;
import java.io.StringReader;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import wyvern.stdlib.Globals;
import wyvern.tools.parsing.Wyvern;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.extensions.Int;

public class TypeCheckingTests {
	@Test
	public void testTypeMembers() throws IOException, CopperParserException {
		String input =
				"type T\n" +
				"  def bar():Int\n" +
				"";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		
		System.out.println(res.typecheck(Globals.getStandardEnv(), Optional.empty()));
		
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv(), Optional.empty()), Int.getInstance());
		
		System.out.println(res.evaluate(Globals.getStandardEnv()).toString());
		
		Assert.assertEquals(res.evaluate(Globals.getStandardEnv()).toString(), "IntegerConstant(5)");		
	}
}
