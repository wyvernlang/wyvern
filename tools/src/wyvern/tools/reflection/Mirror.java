package wyvern.tools.reflection;

import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.DeclarationWithRHS;
import wyvern.target.corewyvernIL.expression.Invokable;
import wyvern.target.corewyvernIL.expression.ObjectValue;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.typedAST.core.expressions.Invocation;

import java.util.List;

/**
 * Created by ewang on 2/16/16.
 */
public class Mirror {

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
                    // TODO: compare RHS of decls
                    // if ()
                }
            }
        }
        return 1;
    }

    public Value invoke(ObjectValue o, String methodName, List<Value> argList) {
        return o.invoke(methodName, argList);
    }

    public void set() {
        return;
    }

    public ValueType type(ObjectValue o) {
        return o.getType();
    }
}
