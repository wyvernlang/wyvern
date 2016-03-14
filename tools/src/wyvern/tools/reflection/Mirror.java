package wyvern.tools.reflection;

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
        // o1.typeCheck(evalContext);
        Value obj = o2.getField("original");
        Value res = null;
        if (obj instanceof ObjectValue) {
            // compare obj and o1
            // ((ObjectValue) obj).typeCheck(evalContext);
        }
        return 1;
    }

    public Value invoke(ObjectValue o, String methodName, List<Value> argList) {
        return o.invoke(methodName, argList);
    }

    public void set() {
        return;
    }

    // TODO: change to EvalContext
    public ValueType type(ObjectValue o, TypeContext evalContext) {
        return o.typeCheck(evalContext);
    }
}
