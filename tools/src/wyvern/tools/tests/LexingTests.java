package wyvern.tools.tests;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import wyvern.stdlib.Globals;
import wyvern.tools.errors.ToolError;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.lexing.WyvernLexer;
import wyvern.tools.parsing.HasParser;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.core.binding.evaluation.ValueBinding;
import wyvern.tools.typedAST.core.declarations.*;
import wyvern.tools.typedAST.core.expressions.*;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.parsing.DSLLit;
import wyvern.tools.typedAST.extensions.SpliceExn;
import wyvern.tools.typedAST.extensions.TSLBlock;
import wyvern.tools.typedAST.extensions.interop.java.Util;
import wyvern.tools.typedAST.extensions.interop.java.objects.JavaObj;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.types.extensions.Int;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.parsing.transformers.DSLTransformer;
import wyvern.tools.parsing.ExtParser;
import wyvern.tools.parsing.Wyvern;

import java.io.*;
import java.util.*;
import static wyvern.tools.parsing.coreparser.WyvernParserConstants.*;
import wyvern.tools.parsing.coreparser.Token;

public class LexingTests {
	public String kindToName(int kind) {
		switch(kind) {
		  case IDENTIFIER: return "IDENTIFIER";
		  case WHITESPACE: return "WHITESPACE";
		  case DSLLINE: return "DSLLINE";
		  case DEDENT: return "DEDENT";
		  case SINGLE_LINE_COMMENT: return "SINGLE_LINE_COMMENT";
		  case NEWLINE: return "NEWLINE";
		  default: return "UNKNOWN(" + kind + ")";
		}
	}
	
	public void printTokenList(List l) {
		for (Object e : l) {
			if (e instanceof Token) {
				Token t = (Token) e;
				System.out.print(kindToName(t.kind) + "(" + t.image + ")");
			} else {
				System.out.print("notToken(" + e + ")");
			}
			System.out.println();
		}
	}

	public String concat(List<Token> tokens) {
		StringBuffer buf = new StringBuffer();
		for (Token t:tokens) {
			buf.append(t.image);
		}
		return buf.toString();
	}
	
	public void checkKinds(int[] kinds, List<Token> tokens) {
		int index = 0;
		for (Token t: tokens) {
			if (index >= kinds.length)
				Assert.fail("more tokens than expected");
			int k = kinds[index++];
			if (k != t.kind)
				Assert.fail("expected " + kindToName(k) + " but was " + kindToName(t.kind));
		}
		Assert.assertEquals(kinds.length, tokens.size());
	}
	
	public void checkLex(String input, int[] kinds) throws IOException, CopperParserException {
		List<Token> tokens = new WyvernLexer().parse(new StringReader(input), "test input");
		checkKinds(kinds, tokens);
		Assert.assertEquals(input, concat(tokens));
	}
	
	@Test
	public void testComments1() throws IOException, CopperParserException {
		String input =
				"exn1\n" +
				"\n" +
				"// foo\n" +
				"\n" +
				"exn2\n";
		int[] expected = new int[] {
				IDENTIFIER, NEWLINE,
				WHITESPACE,
				SINGLE_LINE_COMMENT, WHITESPACE,
				WHITESPACE,
				IDENTIFIER, NEWLINE,
			};
		checkLex(input, expected);
	}
	@Test
	public void testDSLBlock1() throws IOException, CopperParserException {
		String input =
				"foo(~)\n" +
				"  DSL here!\n" +
				"bar()\n";
		int[] expected = new int[] {
				IDENTIFIER, LPAREN, TILDE, RPAREN, NEWLINE,
				WHITESPACE, DSLLINE,
				IDENTIFIER, LPAREN, RPAREN, NEWLINE,
			};
		checkLex(input, expected);
	}
	@Test
	public void testDSLBlock2() throws IOException, CopperParserException {
		String input =
				"foo(~)\n" +
				"  DSL here!\n" +
				"  and here!\n" +
				"bar()\n";
		int[] expected = new int[] {
				IDENTIFIER, LPAREN, TILDE, RPAREN, NEWLINE,
				WHITESPACE, DSLLINE,
				WHITESPACE, DSLLINE,
				IDENTIFIER, LPAREN, RPAREN, NEWLINE,
			};
		checkLex(input, expected);
	}
	@Test
	public void testIndentBlock() throws IOException, CopperParserException {
		String input =
				"foo()\n" +
				"  baz()\n" +
				"  bam()\n" +
				"bar()\n";
		int[] expected = new int[] {
				IDENTIFIER, LPAREN, RPAREN, NEWLINE,
				INDENT, IDENTIFIER, LPAREN, RPAREN, NEWLINE,
				WHITESPACE, IDENTIFIER, LPAREN, RPAREN, NEWLINE,
				DEDENT, IDENTIFIER, LPAREN, RPAREN, NEWLINE,
			};
		checkLex(input, expected);
	}
}

