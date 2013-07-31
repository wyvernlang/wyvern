package wyvern.tools.tests;

import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;

public class ModuleTests {
	@Test
	public void testSimpleModule() {
		Reader in1 = new StringReader(
				"module M1\n" +
				"	import input://arg/2\n" +
				"	class C1\n" +
				"		class meth t() : M2.C2 = M2.C2.create()");
		Reader in2 = new StringReader("" +
				"module M2\n" +
				"	class C2\n" +
				"		class meth create() : C2 = new\n");

	}
}
