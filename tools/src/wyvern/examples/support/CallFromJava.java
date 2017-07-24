package wyvern.examples.support;

import java.util.LinkedList;
import java.util.List;

import wyvern.target.corewyvernIL.expression.ObjectValue;
import wyvern.target.corewyvernIL.expression.Value;

public class CallFromJava {
	public static final CallFromJava callFromJava = new CallFromJava();
	
	public void callFive(ObjectValue lambda) {
		/*System.out.println("called to Java");

		// get the type of the argument and extract the apply method
		ValueType type = lambda.getType();
		EvalContext ctx = lambda.getEvalCtx();
		DeclType applyDecl = type.getStructuralType(ctx).findDecl("apply", ctx);
		// TODO: check that applyDecl has the right signature
		 * 
		 */

		// construct arguments and make the call
		List<Value> args = new LinkedList<Value>();
		//args.add(Util.unitValue());
		wyvern.target.corewyvernIL.expression.Value parsedAST = lambda.invoke("apply", args).executeIfThunk();
		
		System.out.println("\nreturned to Java: " + parsedAST);
		return;
	}
}
