package wyvern.tools.parsing;

import java.util.Map;
import java.util.Optional;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractExpressionAST;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.extensions.TSLBlock;
import wyvern.tools.typedAST.extensions.interop.java.Util;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.TreeWriter;

/**
 * Created by Ben Chung on 3/11/14.
 */
public class DSLLit extends AbstractExpressionAST implements ExpressionAST {
	Optional<String> dslText = Optional.empty();
	TypedAST dslAST = null;
	Type dslASTType = null;
	FileLocation location;

	public void setText(String text) {
		if (dslText == null)
			throw new RuntimeException();
		dslText = Optional.of(text);
	}

	public Optional<String> getText() { return dslText; }

	public DSLLit(Optional<String> dslText) {
		this.dslText = (dslText);
	}

	public DSLLit(Optional<String> dslText, FileLocation loc) {
		location = loc;
		this.dslText = (dslText);
	}

	public TypedAST getAST() { return (dslAST); }

	@Override
	public Type getType() {
		return dslASTType;
	}

	private Type getDefaultType() {
		//TODO
		return null;
	}

	@Override
	public Type typecheck(Environment env, Optional<Type> expected) {
		Type dslType = expected.orElseGet(this::getDefaultType);

		Value vparser =
				Util.invokeValue(dslType.getResolvedBinding().get().getMetadata().get().get(),
						"getParser", UnitVal.getInstance(FileLocation.UNKNOWN));
		ExtParser parser = (ExtParser) Util.toJavaObject(vparser, ExtParser.class);

		try {
			dslAST = new TSLBlock(parser.parse(new ParseBuffer(dslText.get())));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return dslAST.typecheck(env,expected);
	}

	@Override
	public Value evaluate(EvaluationEnvironment env) {
		return null;
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		return null;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		return null;
	}

	@Override
	public void codegenToIL(GenerationEnvironment environment, ILWriter writer) {
		throw new RuntimeException("DSLLit found when generating code at " + this.getLocation());
	}

	@Override
	public FileLocation getLocation() {
		return location;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {

	}

	@Override
	public Expression generateIL(GenContext ctx, ValueType expectedType) {
		throw new RuntimeException("not implemented");
	}
}
