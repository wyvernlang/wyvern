package wyvern.stdlib.support;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.ObjectValue;
import wyvern.target.corewyvernIL.expression.StringLiteral;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.ModuleResolver;

public class Regex {
    public static final Regex utils = new Regex();

    public ObjectValue findPrefixOf(String regex, String source) {
        Matcher m = Pattern.compile(regex).matcher(source);
        EvalContext ctx = ModuleResolver.getLocal().contextWith("wyvern.runtime", "wyvern.option");
        Expression call = null;
        Expression typeParam = ExpressionUtils.typeParam("T", AST.utils.nominalType("system", "String"));
        if (m.find() && m.start() == 0) {
            String matchedString = m.group();
            call = ExpressionUtils.call("option", "Some", typeParam, new StringLiteral(matchedString));
        } else {
            call = ExpressionUtils.call("option", "None", typeParam);
        }
        return (ObjectValue) call.interpret(ctx);
    }

    public ObjectValue findPrefixMatchOf(String regex, String source) {
        Matcher m = Pattern.compile(regex).matcher(source);
        EvalContext ctx = ModuleResolver.getLocal().contextWith("wyvern.option", "wyvern.util.matching.regexInternal");
        Expression call = null;
        Expression typeParam = ExpressionUtils.typeParam("T", AST.utils.nominalType("regexInternal", "Match"));
        if (m.find() && m.start() == 0) {
            String matchedString = m.group();
            String rest = source.substring(m.end());
            Expression match = ExpressionUtils.call("regexInternal", "makeMatch", new StringLiteral(matchedString), new StringLiteral(rest));
            call = ExpressionUtils.call("option", "Some", typeParam, match);
        } else {
            call = ExpressionUtils.call("option", "None", typeParam);
        }
        return (ObjectValue) call.interpret(ctx);
    }
}
