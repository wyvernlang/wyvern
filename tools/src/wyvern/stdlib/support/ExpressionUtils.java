package wyvern.stdlib.support;

import java.util.Arrays;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.expression.Variable;

public class ExpressionUtils {

	public static Expression call(String receiver, String name, Expression... arguments) {
		return new MethodCall(new Variable(receiver), name, Arrays.asList(arguments), null);
	}

}
