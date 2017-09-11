package wyvern.target.corewyvernIL.expression;

import java.io.IOException;
import java.util.Set;

import wyvern.target.corewyvernIL.IASTNode;
import wyvern.target.corewyvernIL.effects.EffectAccumulator;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.ValueType;

public interface IExpr extends IASTNode {
	public ValueType typeCheck(TypeContext ctx, EffectAccumulator effectAccumulator);
	/* if no use for effectAccumulator, pass in null --> this has a different meaning than passing in 
	 * EffectAccumulator(null) (i.e. whose effectSet is null)
	 */
		
	public abstract Value interpret(EvalContext ctx);
	void doPrettyPrint(Appendable dest, String indent) throws IOException;
	public abstract Set<String> getFreeVariables();
}
