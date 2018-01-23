package wyvern.tools.tests.utils;

import java.io.IOException;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;

public interface TestCase {
    String getName();
    void execute() throws IOException, CopperParserException;
}
