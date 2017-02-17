package wyvern.tools.typedAST.extensions;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractExpressionAST;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.EvaluationEnvironment;

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
    @Deprecated
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
	public Expression generateIL(GenContext ctx, ValueType expectedType, List<TypedModuleSpec> dependencies) {
		// TODO Auto-generated method stub
		return null;
	}
}
