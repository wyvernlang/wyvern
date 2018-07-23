package wyvern.tools.tests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.suites.CurrentlyBroken;
import wyvern.tools.tests.suites.RegressionTests;

@Category(RegressionTests.class)
public class CoreParserTests {
    @BeforeClass public static void setup() {
        TestUtil.setPaths();
        Globals.setUsePrelude(false);
    }

    @AfterClass public static void teardown() {
        Globals.setUsePrelude(true);  // restore the default to use the prelude
    }


    @Test
    public void testIdentityApp() throws ParseException {
        String input = "((x: Int) => x)(3) \n";
        TestUtil.doTestInt(input, 3);
    }

    @Test
    @Category(CurrentlyBroken.class)
    public void testValVar() throws ParseException {
        String input = "require stdout\n\n"
                + "val x = \"Hello, \"\n"
                + "var y : String = \"World\"\n"
                + "val z : String = \"!\"\n"
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
                + "obj.getValue()\n";
        TestUtil.doTestInt(input, 5);
    }

    @Test
    public void testFieldRead() throws ParseException {
        String input = "val obj = new\n"
                + "    val v:Int = 5\n"
                + "obj.v\n";
        TestUtil.doTestInt(input, 5);
    }

    @Test
    public void testVarField() throws ParseException {
        String input = "val obj = new\n"
                + "    var v:Int = 5\n"
                + "obj.v = 3\n"
                + "obj.v\n";
        TestUtil.doTestInt(input, 3);
    }

    @Test
    public void testTypeDecl() throws ParseException {
        String input = ""
                + "type ValHolder\n"
                + "    def getValue():Int\n"
                + "val obj : ValHolder = new\n"
                + "    def getValue():Int\n"
                + "        5\n"
                + "obj.getValue()\n";
        TestUtil.doTestInt(input, 5);
    }

    @Test
    public void testDatatypeDecl() throws ParseException {
        String input = ""
                + "datatype Exp\n"
                + "    Var(name: Int)\n"
                + "    Apply(fn: Exp, arg: Exp)\n"
                + "    Lambda(param: String, body: Int)\n"
                + "    Unit\n"
                // + "tagged type Exp2 comprises Var, Apply, Lambda\n"

                + "val v : Var = new\n"
                + "    val name : Int = 5\n"

                + "val k : Lambda = new\n"
                + "    val param : String = 'abc'\n"
                + "    val body : Int = 4\n"

                //+ "val v3 : Exp = Var(4)\n"   this currently doesn't work

                + "val e : Exp = v\n"
                + "val result = match e:\n"
                + "    v2 : Var => v2.name\n"
                + "    k : Lambda => k.body\n"
                + "result\n";

        TestUtil.doTestInt(input, 5);
    }

    @Test
    public void testDatatypeDecl2() throws ParseException {
        String input = ""
                + "datatype Color\n"
                + "    Red\n"
                + "    Blue\n"
                + "    Green\n"
                + "    Yellow\n"
                //+ "val a : Red = new\n"
                + "5\n";
        TestUtil.doTestInt(input, 5);
    }
}