package wyvern.stdlib;

import static wyvern.tools.types.TypeUtils.arrow;
import static wyvern.tools.types.TypeUtils.integer;
import static wyvern.tools.types.TypeUtils.unit;
import static wyvern.tools.types.TypeUtils.str;
import wyvern.tools.parsing.extensions.ClassParser;
import wyvern.tools.parsing.extensions.FnParser;
import wyvern.tools.parsing.extensions.MethParser;
import wyvern.tools.parsing.extensions.NewParser;
import wyvern.tools.parsing.extensions.PropParser;
import wyvern.tools.parsing.extensions.TypeParser;
import wyvern.tools.parsing.extensions.ValParser;
import wyvern.tools.parsing.extensions.VarParser;
import wyvern.tools.typedAST.Keyword;
import wyvern.tools.typedAST.Value;
import wyvern.tools.typedAST.binding.KeywordNameBinding;
import wyvern.tools.typedAST.binding.TypeBinding;
import wyvern.tools.typedAST.binding.ValueBinding;
import wyvern.tools.typedAST.extensions.Executor;
import wyvern.tools.typedAST.extensions.ExternalFunction;
import wyvern.tools.typedAST.extensions.values.BooleanConstant;
import wyvern.tools.typedAST.extensions.values.IntegerConstant;
import wyvern.tools.typedAST.extensions.values.StringConstant;
import wyvern.tools.typedAST.extensions.values.UnitVal;
import wyvern.tools.types.Environment;
import wyvern.tools.types.extensions.Bool;
import wyvern.tools.types.extensions.Int;
import wyvern.tools.types.extensions.Str;
import wyvern.tools.types.extensions.Unit;

public class Globals {
	public static Environment getStandardEnv() {
		Environment env = Environment.getEmptyEnvironment();
		env = env.extend(new KeywordNameBinding("type", new Keyword(TypeParser.getInstance())));
		env = env.extend(new KeywordNameBinding("prop", new Keyword(PropParser.getInstance())));
		env = env.extend(new KeywordNameBinding("meth", new Keyword(MethParser.getInstance())));
		env = env.extend(new KeywordNameBinding("fn", new Keyword(FnParser.getInstance())));
		env = env.extend(new KeywordNameBinding("class", new Keyword(ClassParser.getInstance())));
		env = env.extend(new KeywordNameBinding("val", new Keyword(ValParser.getInstance())));
		env = env.extend(new KeywordNameBinding("var", new Keyword(VarParser.getInstance())));
		env = env.extend(new KeywordNameBinding("new", new Keyword(NewParser.getInstance())));
		env = env.extend(new TypeBinding("Unit", Unit.getInstance()));
		env = env.extend(new TypeBinding("Int", Int.getInstance()));
		env = env.extend(new TypeBinding("Bool", Bool.getInstance()));
		env = env.extend(new TypeBinding("Str", Str.getInstance()));
		env = env.extend(new ValueBinding("true", new BooleanConstant(true)));
		env = env.extend(new ValueBinding("false", new BooleanConstant(false)));
		env = env.extend(new ValueBinding("print", new ExternalFunction(arrow(str, unit), new Executor() {
			@Override public Value execute(Value argument) {
				System.out.println(((StringConstant)argument).getValue());
				return UnitVal.getInstance();
			}
		})));
		env = env.extend(new ValueBinding("printInteger", new ExternalFunction(arrow(integer, unit), new Executor() {
			@Override public Value execute(Value argument) {
				System.out.println(((IntegerConstant)argument).getValue());
				return UnitVal.getInstance();
			}
		})));
		return env;
	}
}