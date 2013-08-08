package wyvern.tools.tests;

import org.junit.Test;
import wyvern.stdlib.Globals;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.parsing.BodyParser;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.LineSequenceParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.rawAST.*;
import wyvern.tools.simpleParser.Phase1Parser;
import wyvern.tools.typedAST.core.binding.TypeBinding;
import wyvern.tools.typedAST.core.values.StringConstant;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.AbstractTypeImpl;
import wyvern.tools.types.Environment;
import wyvern.tools.types.SubtypeRelation;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Str;
import wyvern.tools.util.CompilationContext;
import wyvern.tools.util.Pair;
import wyvern.tools.util.TreeWriter;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;

public class TypeDrivenParsingTests {
    private class TestType extends AbstractTypeImpl {
        @Override
        public LineParser getParser() {
            return new TestTypeParser();
        }

        @Override
        public void writeArgsToTree(TreeWriter writer) {

        }

        private class TestTypeParser implements LineParser {
            @Override
            public TypedAST parse(TypedAST first, CompilationContext ctx) {
                LineSequence seq = ParseUtils.extractLines(ctx);

                return new ExampleAST("Hi!");
            }
        }

        @Override
        public boolean subtype(Type other) {
            return other instanceof TestType || other instanceof Str;
        }
    }

    private class ExampleAST implements TypedAST {

        private final String s;

        public ExampleAST(String s) {
            this.s = s;
        }

        @Override
        public Type getType() {
            return new TestType();
        }

        @Override
        public Type typecheck(Environment env) {
            return new TestType();
        }

        @Override
        public Value evaluate(Environment env) {
            return null;
        }

        @Override
        public LineParser getLineParser() {
            return null;
        }

        @Override
        public LineSequenceParser getLineSequenceParser() {
            return null;
        }

        @Override
        public FileLocation getLocation() {
            return null;
        }

        @Override
        public void writeArgsToTree(TreeWriter writer) {

        }
    }

    private TypedAST doCompile(String input) {
        Reader reader = new StringReader(input);
        RawAST parsedResult = Phase1Parser.parse("Test", reader);
        Environment env = Globals.getStandardEnv();
        env = env.extend(new TypeBinding("TestType",new TestType()));
        TypedAST typedAST = parsedResult.accept(BodyParser.getInstance(), env);
        Type resultType = typedAST.typecheck(env);
        return typedAST;
    }

    @Test
    public void testSimpleTypeParsing() {
        String test =
                "val test : TestType = ~\n" +
                "   expected\n";
        TypedAST result = doCompile(test);
    }

    @Test
    public void testFunctionTypeParsing() {
        String test =
                "def test(a:TestType) : Int = 2\n" +
                "test(~)\n" +
                "   expected";
        TypedAST result = doCompile(test);
    }

    @Test
    public void testFunctionTypeParsing2() {
        String test =
                "def test(a:TestType, b : Int) : Int = 5 + b\n" +
                "test(~, 2)\n" +
                "   expected";
        TypedAST result = doCompile(test);
    }

    @Test
    public void testFunctionTypeParsing3() {
        String test =
                "def test(a : Int, b : TestType, c : Int) : Int = 5\n" +
                "test(1,~,3)\n" +
                "   expected";
        doCompile(test);
    }

    @Test
    public void testFunctionTypeParsing4() {
        String test =
                "def test(a : Int, b : TestType) : Int = 5\n" +
                        "test(1,~)\n" +
                        "   expected";
        doCompile(test);
    }

	@Test
	public void testFunctionTypeParsing5() {
		String test =
				"class IC\n" +
				"	class def test(a : Int, b : TestType) : Int = 5\n" +
				"IC.test(1,~)\n" +
				"   expected";
		doCompile(test);
	}

	@Test
	public void testFunctionTypeParsing6() {
		String test =
				"class IC\n" +
				"	class IIC\n" +
				"		class def test(a : Int, b : TestType) : Int = 5\n" +
				"IC.IIC.test(1,~)\n" +
				"   expected";
		doCompile(test);
	}
}
