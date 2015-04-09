package wyvern.tools.typedAST.extensions;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.TreeWriter;

import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;

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
    public Type typecheck(Environment env, Optional<Type> expected) {
        return dslDef.typecheck(env, Optional.empty());
    }

    @Override
    public Value evaluate(EvaluationEnvironment env) {
        return dslDef.evaluate(env);
    }

	@Override
	public Map<String, TypedAST> getChildren() {
		Hashtable<String, TypedAST> children = new Hashtable<>();
		children.put("dslDef", dslDef);
		return children;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		DSLDummy dslDummy = new DSLDummy(expected);
		dslDummy.setDef(newChildren.get("dslDef"));
		return dslDummy;
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
