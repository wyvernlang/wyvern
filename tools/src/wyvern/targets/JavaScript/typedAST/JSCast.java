package wyvern.targets.JavaScript.typedAST;

import wyvern.targets.JavaScript.types.JSObjectType;
import wyvern.targets.JavaScript.visitors.JSCodegenVisitor;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.CachingTypedAST;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

import java.util.HashMap;
import java.util.Map;


public class JSCast extends CachingTypedAST implements CoreAST {
	private final Type cast;
	private final TypedAST source;
	private final FileLocation fileLocation;

	public JSCast(TypedAST source, Type cast, FileLocation fileLocation) {
		this.source = source;
		this.cast = cast;
		this.fileLocation = fileLocation;
	}

	@Override
	protected Type doTypecheck(Environment env) {
		if (!(source.typecheck(env) instanceof JSObjectType))
			throw new RuntimeException();

		return cast;
	}

	@Override
	public Value evaluate(Environment env) {
		throw new RuntimeException();
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		Map<String, TypedAST> childMap = new HashMap<>();
		childMap.put("src", source);
		return childMap;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		return new JSCast(newChildren.get("source"), cast, getLocation());
	}

	@Override
	public FileLocation getLocation() {
		return fileLocation;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		if (!(visitor instanceof JSCodegenVisitor))
			throw new RuntimeException("Must be in javascript context!");

		((JSCodegenVisitor)visitor).visit(this);
	}

	public TypedAST getBody() {
		return source;
	}
}
