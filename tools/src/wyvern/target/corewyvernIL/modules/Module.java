package wyvern.target.corewyvernIL.modules;

import java.util.List;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.IExpr;

public class Module {
	private final TypedModuleSpec spec;
	private final IExpr expr;
	private final List<TypedModuleSpec> dependencies;
	
	public Module(TypedModuleSpec spec, IExpr program, List<TypedModuleSpec> dependencies) {
		this.spec = spec;
		this.expr = program;
		this.dependencies=dependencies;
	}
	
	public TypedModuleSpec getSpec() {
		return spec;
	}
	
	public Expression getExpression() {
		return (Expression) expr;
	}
	
	public List<TypedModuleSpec> getDependencies() {
		return dependencies;
	}
	
	@Override
	public String toString() {
		return "Module("+spec.getQualifiedName()+")";
	}
}
