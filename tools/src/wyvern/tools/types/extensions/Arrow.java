package wyvern.tools.types.extensions;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.effects.EffectSet;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.core.expressions.Fn;
import wyvern.tools.types.AbstractTypeImpl;
import wyvern.tools.types.ApplyableType;
import wyvern.tools.types.Type;

public class Arrow extends AbstractTypeImpl implements ApplyableType {
    private Type result;
    private List<Type> arguments;
    private boolean isResource;
    private EffectSet effectSet;
    private boolean adapted = false;

    public Arrow(List<Type> arguments, Type result, boolean isResource, String effects, FileLocation location) {
        super(location);
        this.arguments = arguments;
        this.result = result;
        this.isResource = isResource;
        this.effectSet = EffectSet.parseEffects("arrow type", effects, false, location);
    }

    public Type getResult() {
        return result;
    }

    public boolean isResource() {
        return isResource;
    }

    public List<Type> getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        String argString = (arguments == null) ? null : arguments.toString();
        return argString + " -> " + result;
    }

    @Override
    public boolean equals(Object otherT) {
        if (!(otherT instanceof Arrow)) {
            return false;
        }
        Arrow otherAT = (Arrow) otherT;
        return arguments.equals(otherAT.arguments) && result.equals(otherAT.result);
    }

    @Override
    public int hashCode() {
        return arguments.hashCode() + result.hashCode();
    }

    public static final ValueType NOMINAL_UNIT = new NominalType("system", "Unit");

    @Override
    public ValueType getILType(GenContext ctx) {
        List<FormalArg> formals = new LinkedList<FormalArg>();
        for (int i = 0; i < arguments.size(); ++i) {
            ValueType argType = arguments.get(i).getILType(ctx);
            if (!Util.unitType().equals(argType) && !NOMINAL_UNIT.equals(argType)) {
                // it's a real argument, add it to the list
                formals.add(new FormalArg("arg" + i, argType));
            }
        }
        
        if (!adapted && effectSet != null) {
            effectSet.contextualize(ctx);
            adapted = true;
        }


        return new StructuralType(Fn.LAMBDA_STRUCTUAL_DECL,
                                  Arrays.asList(new DefDeclType(Util.APPLY_NAME, result.getILType(ctx), formals, effectSet)),
                                  isResource);
    }
}
