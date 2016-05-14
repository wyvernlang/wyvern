package wyvern.stdlib.support;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.expression.ObjectValue;
import wyvern.target.corewyvernIL.expression.StringLiteral;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.modules.Module;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.InterpreterState;
import wyvern.target.corewyvernIL.support.ModuleResolver;

public class Regex {
	public static Regex utils = new Regex();

	public ObjectValue findPrefixOf(String regex, String source) {
		Matcher m = Pattern.compile(regex).matcher(source);
		EvalContext ctx = ModuleResolver.getLocal().contextWith("wyvern.option");
		Expression call = null;
		if (m.find() && m.start() == 0) {
			String matchedString = m.group();
			call=new MethodCall(new Variable("option"), "Some", Arrays.asList(new StringLiteral(matchedString)), null);
		} else {
			call=new MethodCall(new Variable("option"), "None", Arrays.asList(), null);
		}
		return (ObjectValue) call.interpret(ctx);
	}
}
