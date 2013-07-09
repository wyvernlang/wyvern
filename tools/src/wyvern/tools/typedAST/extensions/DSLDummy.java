package wyvern.tools.typedAST.extensions;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.LineSequenceParser;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class DSLDummy implements TypedAST {
	private final Type expected;
	private TypedAST dslDef = null;
    public DSLDummy(Type expected) {
		this.expected = expected;
	}

	public Type getExpected() {
		return expected;
	}

    public void setDef(TypedAST ast) {
        dslDef = ast;
    }

    @Override
    public Type getType() {
        return dslDef.getType();
    }

    @Override
    public Type typecheck(Environment env) {
        return dslDef.typecheck(env);
    }

    @Override
    public Value evaluate(Environment env) {
        return dslDef.evaluate(env);
    }

    @Override
    public LineParser getLineParser() {
        if (dslDef != null)
            return dslDef.getLineParser();
        return null;
    }

    @Override
    public LineSequenceParser getLineSequenceParser() {
        return dslDef.getLineSequenceParser();
    }

    @Override
    public FileLocation getLocation() {
        if (dslDef != null)
            return dslDef.getLocation();
        else
            return FileLocation.UNKNOWN;
    }

    @Override
    public void writeArgsToTree(TreeWriter writer) {

    }
}
