package wyvern.tools.tests;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import org.junit.Assert;
import org.junit.Test;
import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.tools.parsing.Wyvern;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.transformers.ExpressionWriter;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.types.extensions.Int;

import java.io.IOException;
import java.io.StringReader;

import static java.util.Optional.empty;
import static wyvern.stdlib.Globals.getStandardEnv;

public class CodegenTests {
    @Test
    public void testSimpleAdd() throws IOException, CopperParserException {
        String input =
                "val a = 1\nval b = 2\na+b";
        TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
        Assert.assertEquals(res.typecheck(getStandardEnv(), empty()), new Int());
        Expression output = ExpressionWriter.generate(iw -> res.codegenToIL(new GenerationEnvironment(), iw));
        Assert.assertEquals(res.evaluate(Globals.getStandardEvalEnv()).toString(), "IntegerConstant(19)");
    }
}
