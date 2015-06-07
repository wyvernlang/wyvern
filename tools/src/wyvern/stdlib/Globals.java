package wyvern.stdlib;

import static wyvern.tools.types.TypeUtils.arrow;
import static wyvern.tools.types.TypeUtils.integer;
import static wyvern.tools.types.TypeUtils.unit;
import static wyvern.tools.types.TypeUtils.str;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.imports.extensions.JavaResolver;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.compiler.ImportResolverBinding;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.core.binding.evaluation.ValueBinding;
import wyvern.tools.typedAST.core.values.BooleanConstant;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.core.values.StringConstant;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.extensions.ExternalFunction;
import wyvern.tools.types.Environment;
import wyvern.tools.types.extensions.Bool;
import wyvern.tools.types.extensions.Int;
import wyvern.tools.types.extensions.Str;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.EvaluationEnvironment;

public class Globals {
	public static Environment getStandardEnv() {
		Environment env = Environment.getEmptyEnvironment();
		env = env.extend(new ImportResolverBinding("java",JavaResolver.getInstance()));
		env = env.extend(new ImportResolverBinding("wyv", WyvernResolver.getInstance()));

		env = env.extend(new TypeBinding("Unit", new Unit()));
		env = env.extend(new TypeBinding("Int", new Int()));
		env = env.extend(new TypeBinding("Bool", new Bool()));
		env = env.extend(new TypeBinding("Str", new Str()));

		env = env.extend(new NameBindingImpl("true", new Bool()));
		env = env.extend(new NameBindingImpl("false", new Bool()));
		env = env.extend(new NameBindingImpl("print", (arrow(str, unit))));
		env = env.extend(new NameBindingImpl("printInteger", arrow(integer, unit)));
		return env;
	}
	public static EvaluationEnvironment getStandardEvalEnv() {
		EvaluationEnvironment env = EvaluationEnvironment.EMPTY;
		env = env.extend(new ValueBinding("null", UnitVal.getInstance(FileLocation.UNKNOWN))); // How to represent  shock/horror  null!?
		env = env.extend(new ValueBinding("true", new BooleanConstant(true)));
		env = env.extend(new ValueBinding("false", new BooleanConstant(false)));

		env = env.extend(new ValueBinding("print", new ExternalFunction(arrow(str, unit), (env1, argument) -> {
			System.out.println(((StringConstant)argument).getValue());
			return UnitVal.getInstance(FileLocation.UNKNOWN); // Fake line number! FIXME:
		})));
		env = env.extend(new ValueBinding("printInteger", new ExternalFunction(arrow(integer, unit), (env1, argument) -> {
			System.out.println(((IntegerConstant)argument).getValue());
			return UnitVal.getInstance(FileLocation.UNKNOWN); // Fake line number! FIXME:
		})));
		return env;
	}

	public static final boolean checkRuntimeTypes = false;
}