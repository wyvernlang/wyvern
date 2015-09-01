package wyvern.tools.types;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.typedAST.core.expressions.Invocation;
import wyvern.tools.typedAST.core.expressions.Variable;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.util.TreeWriter;

public class QualifiedType extends AbstractTypeImpl {
	private TypedAST base;
	private String name;
	
	public String getName() { return name; }
	public TypedAST getBase() { return base; }

	public QualifiedType(TypedAST base, String name) {
		this.name = name;
		this.base = base;
	}

	@Override
	public ValueType generateILType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ValueType getILType(GenContext ctx) {
		return new NominalType(getPath(base, ctx), name);
	}

	private static Path getPath(TypedAST ast, GenContext ctx) {
		Expression exp = ast.generateIL(ctx);
		return (Path) exp;
	}
	
	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(base, name);
	}

}
