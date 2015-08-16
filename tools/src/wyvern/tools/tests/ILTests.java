package wyvern.tools.tests;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;

import org.junit.Assert;
import org.junit.Test;

import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.EmitLLVMNative;
import wyvern.target.oir.EmitLLVMVisitor;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;
import wyvern.target.oir.OIRProgram;
import wyvern.tools.parsing.Wyvern;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.tagTests.TestUtil;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.transformers.ExpressionWriter;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.types.Environment;
import wyvern.tools.types.extensions.Int;

import java.io.IOException;
import java.io.StringReader;

import static java.util.Optional.empty;
import static wyvern.stdlib.Globals.getStandardEnv;

public class ILTests {
    @Test
    public void testLetVal() {
    	NominalType Int = new NominalType("system", "Int");
    	Variable x = new Variable("x");
    	IntegerLiteral five = new IntegerLiteral(5);
    	Expression letExpr = new Let("x", five, x);
    	
    	TypeContext ctx = TypeContext.empty();
		ValueType t = letExpr.typeCheck(ctx);
		Assert.assertEquals(Int, t);
		Value v = letExpr.interpret(EvalContext.empty());
		Assert.assertEquals(five, v);
    }
    
    @Test
    public void testLetValWithParse() throws ParseException {
        String input =
                  "val x = 5\n"
        		+ "x\n";
		TypedAST ast = TestUtil.getNewAST(input);
		Expression program = ast.generateIL(GenContext.empty());
    	TypeContext ctx = TypeContext.empty();
		ValueType t = program.typeCheck(ctx);
		Assert.assertEquals(Util.intType(), t);
		Value v = program.interpret(EvalContext.empty());
    	IntegerLiteral five = new IntegerLiteral(5);
		Assert.assertEquals(five, v);
    }
	@Test
	public void testFieldRead() throws ParseException {
		String input = "val obj = new\n"
				     + "    val v = 5\n"
				     + "obj.v\n"
				     ;
		TypedAST ast = TestUtil.getNewAST(input);
		Expression program = ast.generateIL(GenContext.empty());
    	TypeContext ctx = TypeContext.empty();
		ValueType t = program.typeCheck(ctx);
		Assert.assertEquals(Util.intType(), t);
		Value v = program.interpret(EvalContext.empty());
    	IntegerLiteral five = new IntegerLiteral(5);
		Assert.assertEquals(five, v);
	}
	@Test
	public void testVarFieldRead() throws ParseException {
		String input = "val obj = new\n"
				     + "    var v : system.Int = 5\n"
				     + "obj.v\n"
				     ;
		TypedAST ast = TestUtil.getNewAST(input);
		// bogus "system" entry, but makes the text work for now
		GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), null);
		Expression program = ast.generateIL(genCtx);
    	TypeContext ctx = TypeContext.empty();
		ValueType t = program.typeCheck(ctx);
		Assert.assertEquals(Util.intType(), t);
		Value v = program.interpret(EvalContext.empty());
    	IntegerLiteral five = new IntegerLiteral(5);
		Assert.assertEquals(five, v);
	}
}
