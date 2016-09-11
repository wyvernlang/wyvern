package wyvern.tools.tests;

import static java.util.Optional.empty;
import static wyvern.stdlib.Globals.getStandardEnv;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRState;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;
import wyvern.target.oir.OIRProgram;
import wyvern.tools.parsing.Wyvern;
import wyvern.tools.tests.suites.CurrentlyBroken;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.transformers.ExpressionWriter;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.types.extensions.Int;

public class CodegenTests {
    @Test
    public void testSimpleAdd() throws IOException, CopperParserException {
        String input =
                "val a = 1\na+2";
        TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
        Assert.assertEquals(res.typecheck(getStandardEnv(), empty()), new Int());
        Expression output = ExpressionWriter.generate(iw -> res.codegenToIL(new GenerationEnvironment(), iw));
        OIRAST oirast = output.acceptVisitor(new EmitOIRVisitor (), new EmitOIRState(null, OIREnvironment.getRootEnvironment()));
        OIRProgram oirprogram = OIRProgram.program;
        System.out.println ("");
        //EmitLLVMNative.createMainFunction();
        //String toReturn = oirast.acceptVisitor(new EmitLLVMVisitor (), OIREnvironment.getRootEnvironment());
        //EmitLLVMNative.functionCreated(toReturn);
    }
    @Test
    public void testDef() throws IOException, CopperParserException {
        String input =
                "def test(a:Int):Int\n\ta+1\ntest(1)";
        TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
        Assert.assertEquals(res.typecheck(getStandardEnv(), empty()), new Int());
        Expression output = ExpressionWriter.generate(iw -> res.codegenToIL(new GenerationEnvironment(), iw));
        OIRAST oirast = output.acceptVisitor(new EmitOIRVisitor (), new EmitOIRState(null, OIREnvironment.getRootEnvironment()));
        OIRProgram oirprogram = OIRProgram.program;
        System.out.println ("");
    }
	@Category(CurrentlyBroken.class)
    @Test
    public void testNew() throws IOException, CopperParserException {
        String input =
                "val t = new\n\tval x:Int = 2\n\tval y:Int = 3\nt.x";
        TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
        Assert.assertEquals(res.typecheck(getStandardEnv(), empty()), new Int());
        Expression output = ExpressionWriter.generate(iw -> res.codegenToIL(new GenerationEnvironment(), iw));
        OIRAST oirast = output.acceptVisitor(new EmitOIRVisitor (), new EmitOIRState(null, OIREnvironment.getRootEnvironment()));
        OIRProgram oirprogram = OIRProgram.program;
        System.out.println ("");
    }
}
