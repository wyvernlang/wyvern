package wyvern.stdlib;

import static wyvern.tools.types.TypeUtils.arrow;
import static wyvern.tools.types.TypeUtils.integer;
import static wyvern.tools.types.TypeUtils.unit;
import static wyvern.tools.types.TypeUtils.str;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.imports.extensions.JavaResolver;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.typedAST.core.binding.ImportResolverBinding;
import wyvern.tools.typedAST.core.binding.TypeBinding;
import wyvern.tools.typedAST.core.binding.ValueBinding;
import wyvern.tools.typedAST.core.values.BooleanConstant;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.core.values.StringConstant;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.extensions.ExternalFunction;
import wyvern.tools.typedAST.interfaces.Executor;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.extensions.Bool;
import wyvern.tools.types.extensions.Int;
import wyvern.tools.types.extensions.Str;
import wyvern.tools.types.extensions.Unit;

public class Globals {
	public static Environment getStandardEnv() {
		Environment env = Environment.getEmptyEnvironment();
		env = env.extend(new ImportResolverBinding("java",JavaResolver.getInstance()));
		env = env.extend(new ImportResolverBinding("wyv", WyvernResolver.getInstance()));

		env = env.extend(new TypeBinding("Unit", Unit.getInstance()));
		env = env.extend(new TypeBinding("Int", Int.getInstance()));
		env = env.extend(new TypeBinding("Bool", Bool.getInstance()));
		env = env.extend(new TypeBinding("Str", Str.getInstance()));
		
		env = env.extend(new ValueBinding("null", UnitVal.getInstance(FileLocation.UNKNOWN))); // How to represent  shock/horror  null!?
		env = env.extend(new ValueBinding("true", new BooleanConstant(true)));
		env = env.extend(new ValueBinding("false", new BooleanConstant(false)));
		
		env = env.extend(new ValueBinding("print", new ExternalFunction(arrow(str, unit), new Executor() {
			@Override public Value execute(Value argument) {
				System.out.println(((StringConstant)argument).getValue());
				return UnitVal.getInstance(FileLocation.UNKNOWN); // Fake line number! FIXME:
			}
		})));
		env = env.extend(new ValueBinding("printInteger", new ExternalFunction(arrow(integer, unit), new Executor() {
			@Override public Value execute(Value argument) {
				System.out.println(((IntegerConstant)argument).getValue());
				return UnitVal.getInstance(FileLocation.UNKNOWN); // Fake line number! FIXME:
			}
		})));
		return env;
	}
}