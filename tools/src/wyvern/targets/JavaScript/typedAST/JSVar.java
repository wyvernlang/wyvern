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

public class JSVar extends CachingTypedAST implements CoreAST {
	private final FileLocation location;
	private final String varName;

	public JSVar(String jsVarName, FileLocation location) {
		this.location = location;
		this.varName = jsVarName;
	}

	@Override
	protected Type doTypecheck(Environment env) {
		return JSObjectType.getInstance();
	}

	@Override
	public Value evaluate(Environment env) {
		throw new RuntimeException("Can't interpret a JS var");
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		return new HashMap<>();
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		return new JSVar(varName, location);
	}

	@Override
	public FileLocation getLocation() {
		return location;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
	}

	public String getName() {
		return varName;
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		if (!(visitor instanceof JSCodegenVisitor))
			throw new RuntimeException("JSFunctions can only be traversed by a JS codegeneration visitor.");

		((JSCodegenVisitor)visitor).visit(this);
	}
}
