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

public class LexingTests {
	@Test
	public void testComments1() throws IOException, CopperParserException {
		String input =
				"exn1\n" +
				"\n" +
				"// foo\n" +
				"\n" +
				"exn2\n";
		Object o = new WyvernLexer().parse(new StringReader(input), "test input");
		System.out.println(o);
	}
	@Test
	public void testDSLBlock1() throws IOException, CopperParserException {
		String input =
				"foo(~)\n" +
				"  DSL here!\n" +
				"bar()\n";
		Object o = new WyvernLexer().parse(new StringReader(input), "test input");
		System.out.println(o);
	}
}

