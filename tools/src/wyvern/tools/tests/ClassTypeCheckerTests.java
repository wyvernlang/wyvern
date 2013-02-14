package wyvern.tools.tests;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.IOException;
import java.io.File;

import java.net.URL;

import java.util.Scanner;

import junit.framework.Assert;

import org.junit.Test;

import wyvern.tools.simpleParser.Phase1Parser;
import wyvern.tools.rawAST.RawAST;

public class ClassTypeCheckerTests {
	@Test
	public void testClassDeclaration() throws IOException {
		String testFileName = "wyvern/tools/tests/samples/SimpleClass.wyv";
		URL url = ClassTypeCheckerTests.class.getClassLoader().getResource(testFileName);
		if (url == null) {
			Assert.fail("Unable to open " + testFileName + " file.");
			return;
		}
		InputStream is = url.openStream();
		Reader reader = new InputStreamReader(is);

		testFileName = "wyvern/tools/tests/samples/parsedSimpleClass.prsd";
		url = ClassTypeCheckerTests.class.getClassLoader().getResource(testFileName);
		Scanner s = new Scanner(new File(url.getFile()));
		String parsedTestFileAsString = s.nextLine();
		s.close();
		
		RawAST parsedResult = Phase1Parser.parse(reader);
		Assert.assertEquals(parsedTestFileAsString, parsedResult.toString());
		
		// TODO: In progress by Alex.
	}
}
