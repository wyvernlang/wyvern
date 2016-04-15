package wyvern.target.corewyvernIL.modules;

import java.util.List;

import wyvern.target.corewyvernIL.expression.Expression;

public class Module {
	private final TypedModuleSpec spec;
	private final Expression expr;
	private final List<TypedModuleSpec> dependencies;
	
	public Module(TypedModuleSpec spec, Expression expr, List<TypedModuleSpec> dependencies) {
		this.spec = spec;
		this.expr = expr;
		this.dependencies=dependencies;
	}
	
	public TypedModuleSpec getSpec() {
		return spec;
	}
	
	public Expression getExpression() {
		return expr;
	}
	
	public List<TypedModuleSpec> getDependencies() {
		return dependencies;
	}
}
