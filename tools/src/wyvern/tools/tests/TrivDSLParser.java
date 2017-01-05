package wyvern.tools.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import wyvern.tools.parsing.ExtParser;
import wyvern.tools.parsing.ParseBuffer;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.DefDeclaration;
import wyvern.tools.typedAST.core.expressions.New;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.types.extensions.Int;
import wyvern.tools.types.extensions.Unit;

public class TrivDSLParser implements ExtParser {
	@Override
    @Deprecated
	public TypedAST parse(ParseBuffer input) {
		New newv = new New(new HashMap<>(), null);
		TypedAST dbody = new IntegerConstant(Integer.parseInt(input.getSrcString().trim()));
		newv.setBody(new DeclSequence(Arrays.asList(new DefDeclaration("getValue", new Arrow(new Unit(), new Int()), new ArrayList<>(), dbody, false))));
		return newv;
	}
}
