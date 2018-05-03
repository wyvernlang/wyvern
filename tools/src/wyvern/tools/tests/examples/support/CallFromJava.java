package wyvern.tools.tests.examples.support;

import java.util.LinkedList;
import java.util.List;

import wyvern.target.corewyvernIL.expression.ObjectValue;
import wyvern.target.corewyvernIL.expression.Value;

public class CallFromJava {
    public static final CallFromJava callFromJava = new CallFromJava();

    public void callFive(ObjectValue lambda) {
        List<Value> args = new LinkedList<Value>();
        lambda.invoke("apply", args).executeIfThunk();
    }
}
