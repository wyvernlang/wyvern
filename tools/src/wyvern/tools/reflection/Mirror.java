package wyvern.tools.reflection;

import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.DeclarationWithRHS;
import wyvern.target.corewyvernIL.expression.*;
import wyvern.target.corewyvernIL.support.EvalContext;

import java.util.List;

/**
 * Created by ewang on 2/16/16.
 */
public class Mirror {

    private boolean valueEquals(Value v1, Value v2) {
        // TODO: JavaValue, RationalLiteral, StringLiteral
        if (v1 instanceof BooleanLiteral && v2 instanceof BooleanLiteral) {
            return ((BooleanLiteral) v1).getValue() == ((BooleanLiteral) v2).getValue();
        }
        if (v1 instanceof IntegerLiteral && v2 instanceof IntegerLiteral) {
            return ((IntegerLiteral) v1).getValue() == ((IntegerLiteral) v2).getValue();
        }
        if (v1 instanceof ObjectValue && v2 instanceof ObjectValue) {
            return (1 == equals((ObjectValue) v1, (ObjectValue) v2));
        }
        return false;
    }

    public int equals(ObjectValue o1, ObjectValue o2) {
        EvalContext evalCtx = o1.getEvalCtx();
        // o2 is an ObjectMirror
        Value obj = o2.getField("original");
        if (!o1.getType().equalsInContext(obj.getType(), evalCtx)) {
            return 0;
        }
        if (obj instanceof ObjectValue) {
            List<Declaration> objDecls = ((ObjectValue) obj).getDecls();
            for (Declaration decl : objDecls) {
                if (decl instanceof DeclarationWithRHS) {
                    Declaration o1Decl = o1.findDecl(decl.getName());
                    if (o1Decl == null || !(o1Decl instanceof DeclarationWithRHS)) {
                        return 0;
                    }
                    Value declVal = ((DeclarationWithRHS) decl)
                            .getDefinition().interpret(evalCtx);
                    Value o1DeclVal = ((DeclarationWithRHS) o1Decl)
                            .getDefinition().interpret(evalCtx);
                    if (!valueEquals(declVal, o1DeclVal)) {
                        return 0;
                    }
                }
            }
        }
        return 1;
    }
}
