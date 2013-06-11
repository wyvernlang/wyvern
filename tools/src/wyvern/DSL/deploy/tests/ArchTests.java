package wyvern.DSL.deploy.tests;

import junit.framework.Assert;
import org.junit.Test;
import wyvern.DSL.DSL;
import wyvern.DSL.deploy.Deploy;
import wyvern.stdlib.Util;
import wyvern.tools.typedAST.interfaces.TypedAST;

import java.util.ArrayList;
import java.util.List;

public class ArchTests {

	@Test
	public void testArchitecture() {
		String test = "architecture Foo";
		TypedAST output = compile(test);
		Assert.assertEquals("[]",output.toString());
	}

	@Test
	public void testEndpoints() {
		String test = "architecture Foo\n" +
				"	endpoint Client\n" +
				"class WbesClient\n";
		TypedAST output = compile(test);
		Assert.assertEquals("[[[MutableTypeDeclaration()], MutableClassDeclaration()]]", output.toString());
	}

	@Test
	public void testEndpoints2() {
		String test =
				"architecture Foo\n" +
				"	endpoint Client\n" +
				"	endpoint Server\n" +
				"class WbesClient\n" +
				"class WbesServer\n";
		TypedAST output = compile(test);
		Assert.assertEquals("[[[MutableTypeDeclaration(), MutableTypeDeclaration()], MutableClassDeclaration(), MutableClassDeclaration()]]", output.toString());
	}
	@Test
	public void testConnectionVia() {
		String test =
				"architecture Foo\n" +
						"	endpoint Client\n" +
						"	endpoint Server\n" +
						"	domain Client -> Server\n" +
						"		via TCPConnection\n" +
						"			connection First(x:Int):Int\n" +
						"class WbesClient\n" +
						"class WbesServer\n" +
						"class TCPConnection\n";
		TypedAST output = compile(test);
		Assert.assertEquals("" +
				"[[[MutableTypeDeclaration(), MutableTypeDeclaration()], MutableClassDeclaration(), MutableClassDeclaration(), MutableClassDeclaration()]]", output.toString());
	}

	private TypedAST compile(String test) {
		List<DSL> dsls = new ArrayList<DSL>();
		dsls.add(new Deploy());
		return Util.doCompile(test, dsls);
	}
}
