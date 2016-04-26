package wyvern.target.corewyvernIL;

import java.util.LinkedList;
import java.util.List;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;

/*
 * Represents a 
 */
public abstract class ContextBinding extends VarBinding {
	private String contextName;
	
	public ContextBinding(String varName, ValueType type, Expression expr, String contextName) {
		super(varName, type, expr);
		this.contextName = contextName;
	}

	public String getContextName() {
		return contextName;
	}
	
	public abstract GenContext extendContext(GenContext ctx);

	/*public static Expression wrap(Expression program, List<TypedModuleSpec> dependencies) {
		for (TypedModuleSpec s:dependencies) {
			program = new Let(, program);
		}
		return program;
	}*/
}
