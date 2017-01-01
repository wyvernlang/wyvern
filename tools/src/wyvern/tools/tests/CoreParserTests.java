package wyvern.tools.tests;

import java.io.Reader;
import java.io.StringReader;
import java.util.Optional;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import wyvern.tools.tests.suites.CurrentlyBroken;
import wyvern.stdlib.Globals;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.types.TypeUtils;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.parsing.coreparser.ParseUtils;
import wyvern.tools.parsing.coreparser.WyvernParser;
import wyvern.tools.tests.suites.RegressionTests;
import wyvern.tools.tests.TestUtil;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Int;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;

@Category(RegressionTests.class)
public class CoreParserTests {
    @BeforeClass public static void setupResolver() {
    	TestUtil.setPaths();
    }

	@Test
	public void testIdentityApp() throws ParseException {
		String input = "((x: Int) => x)(3) \n";
        TestUtil.doTest(input, null, new IntegerLiteral(3));
	}

	@Test
    @Category(CurrentlyBroken.class)
	public void testValVar() throws ParseException {
		String input = "require stdout\n\n"
					 + "val x = \"Hello, \"\n"
					 + "var y : system.String = \"World\"\n"
					 + "val z : system.String = \"!\"\n"
				     + "stdout.print(x)\n"
				     + "stdout.print(y)\n"
				     + "stdout.print(z)\n";
        TestUtil.doTest(input, Util.unitType(), Util.unitValue());
	}
	
	@Test
	public void testNewInvoke() throws ParseException {
		String input = "val obj = new\n"
				     + "    def getValue():Int\n"
				     + "        5\n"
				     + "obj.getValue()\n"
				     ;

        TestUtil.doTest(input, null, new IntegerLiteral(5));
    }
	
	@Test
	public void testFieldRead() throws ParseException {
		String input = "val obj = new\n"
				     + "    val v:Int = 5\n"
				     + "obj.v\n"
				     ;
        TestUtil.doTest(input, null, new IntegerLiteral(5));
	}
	@Test
	public void testVarField() throws ParseException {
		String input = "val obj = new\n"
				     + "    var v:Int = 5\n"
				     + "obj.v = 3\n"
				     + "obj.v\n"
				     ;
        TestUtil.doTest(input, null, new IntegerLiteral(3));
	}
	
	@Test
	public void testTypeDecl() throws ParseException {
		String input = ""
			         + "type ValHolder\n"
				     + "    def getValue():Int\n"
				     + "val obj : ValHolder = new\n"
				     + "    def getValue():Int\n"
				     + "        5\n"
				     + "obj.getValue()\n"
				     ;
        TestUtil.doTest(input, null, new IntegerLiteral(5));
	}	
}
