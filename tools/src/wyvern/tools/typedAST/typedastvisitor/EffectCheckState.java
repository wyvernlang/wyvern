package wyvern.tools.typedAST.typedastvisitor;

import wyvern.target.corewyvernIL.effects.EffectSet;
import wyvern.target.corewyvernIL.support.GenContext;

public class EffectCheckState {
    private EffectSet upperBound;
    private GenContext ctx;

    public EffectCheckState(EffectSet set, GenContext context) {
        upperBound = set;
        ctx = context;
    }
}
