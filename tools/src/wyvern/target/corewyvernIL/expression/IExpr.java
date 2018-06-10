package wyvern.target.corewyvernIL.expression;

import java.io.IOException;
import java.util.Set;

import wyvern.target.corewyvernIL.IASTNode;
import wyvern.target.corewyvernIL.effects.EffectAccumulator;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;

public interface IExpr extends IASTNode {
    ValueType typeCheck(TypeContext ctx, EffectAccumulator effectAccumulator);
    /* if no use for effectAccumulator, pass in null --> this has a different meaning than passing in
     * EffectAccumulator(null) (i.e. whose effectSet is null)
     */

    Value interpret(EvalContext ctx);
    void doPrettyPrint(Appendable dest, String indent) throws IOException;
    Set<String> getFreeVariables();
    default ValueType typecheckNoAvoidance(TypeContext ctx, EffectAccumulator effectAccumulator) {
        return typeCheck(ctx, effectAccumulator);
    }
    default boolean isPath() {
        return false;
    }

    default IExpr locationHint(FileLocation loc) {
        return this;
    }
}
