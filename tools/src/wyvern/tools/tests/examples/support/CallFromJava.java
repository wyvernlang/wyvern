package wyvern.tools.tests.examples.support;

import java.util.LinkedList;
import java.util.List;

import wyvern.stdlib.support.Effect;
import wyvern.stdlib.support.Pure;
import wyvern.target.corewyvernIL.expression.ObjectValue;
import wyvern.target.corewyvernIL.expression.Value;

// Tell Wyvern that this class has no side effects on initialization.
@Pure
public class CallFromJava {
    public static final CallFromJava singleton = new CallFromJava();

    public void callWyvernThunk(ObjectValue lambda) {
        List<Value> args = new LinkedList<Value>();
        lambda.invoke("apply", args).executeIfThunk();
    }

    @Effect("stdout.print")
    public void impureMethod() {
        System.out.println("Hello from CallFromJava.impureMethod!");
    }

    @Pure
    public String pureMethod() {
        return "Hello from CallFromJava.getString!";
    }

    public static class DemoObject {
        public void doThing() {
            System.out.println("Hello from DemoObject.print!");
        }
    }

    @Pure
    public DemoObject getObject() {
        return new DemoObject();
    }
}
