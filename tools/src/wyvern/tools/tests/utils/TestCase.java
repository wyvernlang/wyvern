package wyvern.tools.tests.utils;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;

import java.io.IOException;

public interface TestCase {
	String getName();
	void execute() throws IOException, CopperParserException;
}
