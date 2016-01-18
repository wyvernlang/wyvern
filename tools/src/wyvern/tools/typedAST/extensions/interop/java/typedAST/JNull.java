package wyvern.tools.typedAST.extensions.interop.java.typedAST;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.WyvernException;
import wyvern.tools.typedAST.abs.AbstractExpressionAST;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.extensions.interop.java.types.JNullType;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.TreeWriter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class JNull extends AbstractExpressionAST {
	@Override
	public Type getType() {
		return new JNullType();
	}

	@Override
	public Type typecheck(Environment env, Optional<Type> expected) {
		return new JNullType();
	}

	@Override
	public Value evaluate(EvaluationEnvironment env) {
		return UnitVal.getInstance(FileLocation.UNKNOWN);
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		return new HashMap<>();
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		return this;
	}

    @Override
    public void codegenToIL(GenerationEnvironment environment, ILWriter writer) {
        throw new WyvernException("Cannot generate IL for an interop object", FileLocation.UNKNOWN);
    }

    @Override
	public FileLocation getLocation() {
		return FileLocation.UNKNOWN;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
	}

	@Override
	public Expression generateIL(GenContext ctx, ValueType expectedType) {
		// TODO Auto-generated method stub
		return null;
	}
}
