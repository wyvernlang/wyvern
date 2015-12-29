package wyvern.tools.typedAST.extensions;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.support.CallableExprGenerator;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.WyvernException;
import wyvern.tools.typedAST.abs.AbstractExpressionAST;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.TreeWriter;

import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;

public class DSLDummy extends AbstractExpressionAST implements ExpressionAST {
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
    public void codegenToIL(GenerationEnvironment environment, ILWriter writer) {
        throw new WyvernException("DSL internal compilation error - DSL literal not resolved" , this);
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

	@Override
	public Expression generateIL(GenContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}
}
