package wyvern.tools.tests.examples.support;

import wyvern.target.corewyvernIL.expression.ObjectValue;

public class CallFromJava {
    public static final CallFromJava callFromJava = new CallFromJava();

    public void callFive(ObjectValue lambda) {
        /*
        // get the type of the argument and extract the apply method
        ValueType type = lambda.getType();
        EvalContext ctx = lambda.getEvalCtx();
        DeclType applyDecl = type.getStructuralType(ctx).findDecl("apply", ctx);
        // TODO: check that applyDecl has the right signature

        // construct arguments and make the call
        List<Value> args = new LinkedList<Value>();
        //args.add(Util.unitValue());
        wyvern.target.corewyvernIL.expression.Value parsedAST = lambda.invoke("apply", args).executeIfThunk();
        */
    }
}
