package wyvern.tools.typedAST.extensions;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractTypedAST;
import wyvern.tools.typedAST.extensions.interop.java.Util;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.TypeType;
import wyvern.tools.util.TreeWriter;
import wyvern2.parsing.ExtParser;

import java.util.Map;
import java.util.Optional;

/**
 * Created by Ben Chung on 3/11/14.
 */
public class DSLLit extends AbstractTypedAST {
	Optional<String> dslText = Optional.empty();
	TypedAST dslAST = null;
	Type dslASTType = null;

	public void setText(String text) {
		if (dslText == null)
			throw new RuntimeException();
		dslText = Optional.of(text);
	}

	public Optional<String> getText() { return dslText; }

	public DSLLit(Optional<String> dslText) {
		this.dslText = (dslText);
	}

	public TypedAST getAST() { return dslAST; }

	@Override
	public Type getType() {
		return dslASTType;
	}

	@Override
	public Type typecheck(Environment env, Optional<Type> expected) {
		Type dslType = expected.get();

		ExtParser parser = (ExtParser) Util.toJavaObject(((TypeType) dslType).getAttrValue(), ExtParser.class);

		dslAST = parser.parse(dslText.get());

		return dslAST.typecheck(env,expected);
	}

	@Override
	public Value evaluate(Environment env) {
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
	public FileLocation getLocation() {
		return null;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {

	}
}
