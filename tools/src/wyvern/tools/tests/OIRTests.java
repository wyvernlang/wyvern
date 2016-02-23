package wyvern.tools.tests;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.FieldGet;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.expression.StringLiteral;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.GenUtil;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.TypeGenContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;
import wyvern.target.oir.PrettyPrintVisitor;
import wyvern.tools.Interpreter;
import wyvern.tools.errors.ToolError;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.interop.FObject;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.suites.CurrentlyBroken;
import wyvern.tools.tests.suites.RegressionTests;
import wyvern.tools.tests.tagTests.TestUtil;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;

@Category(RegressionTests.class)
public class OIRTests {
    	
	private static final String BASE_PATH = TestUtil.BASE_PATH;
	private static final String PATH = BASE_PATH + "modules/module/";
	
    @BeforeClass public static void setupResolver() {
    	TestUtil.setPaths();
		WyvernResolver.getInstance().addPath(PATH);
    }

    private void printPyFromInput(String input) throws ParseException {
        ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input);
        Expression ILprogram = ast.generateIL(GenContext.empty().extend("system", new Variable("system"), null), null);
        OIRAST oirast =
            ILprogram.acceptVisitor(new EmitOIRVisitor(),
                                    null,
                                    OIREnvironment.getRootEnvironment());
        String pprint =
            oirast.acceptVisitor(new PrettyPrintVisitor(),
                                 OIREnvironment.getRootEnvironment());
        System.out.println("OIR Program:\n" + pprint);
    }

    @Test
    public void testOIRLetValWithParse() throws ParseException {
        String input =
            "val x = 5\n"
        		+ "x\n";
        printPyFromInput(input);
    }

    @Test
    public void testOIRLetValWithString() throws ParseException {
        String input =
            "val x = \"five\"\n"
        		+ "x\n";
        printPyFromInput(input);
    }

    @Test
    public void testOIRLetValWithString3() throws ParseException {
        String input =
            "val identity = (x: system.Int) => x\n" +
            "identity(5)";
        printPyFromInput(input);
    }

    @Test
    public void testOIRFieldRead() throws ParseException {
        String input =
            "val obj = new\n" +
            "    val v = 5\n" +
            "obj.v\n";
        printPyFromInput(input);
    }

    @Test
    public void testOIRVarFieldRead() throws ParseException {
        String input =
            "val obj = new\n" +
            "    var v : system.Int = 5\n" +
            "obj.v\n";
        printPyFromInput(input);
    }
}
