package wyvern2.parsing.tests;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import org.junit.Test;
import wyvern2.parsing.Wyvern;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by Ben Chung on 2/26/14.
 */
public class CopperTests {
	@Test
	public void testVal() throws IOException, CopperParserException {
		String input = "1+1+1\n";
		new Wyvern().parse(new StringReader(input), "test input");
	}
}
