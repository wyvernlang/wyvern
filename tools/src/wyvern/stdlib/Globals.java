package wyvern.stdlib;

import static wyvern.tools.types.TypeUtils.arrow;
import static wyvern.tools.types.TypeUtils.integer;
import static wyvern.tools.types.TypeUtils.str;
import static wyvern.tools.types.TypeUtils.unit;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.TypeDeclaration;
import wyvern.target.corewyvernIL.decltype.AbstractTypeMember;
import wyvern.target.corewyvernIL.decltype.ConcreteTypeMember;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.expression.ObjectValue;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.EmptyGenContext;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.GenUtil;
import wyvern.target.corewyvernIL.support.InterpreterState;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.TypeGenContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.DynamicType;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.imports.extensions.JavaResolver;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.tests.tagTests.TestUtil;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.compiler.ImportResolverBinding;
import wyvern.tools.typedAST.core.binding.evaluation.ValueBinding;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
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
	public static final NominalType JAVA_IMPORT_TYPE = new NominalType("system", "java");
    public static final NominalType PYTHON_IMPORT_TYPE = new NominalType("system", "python");
	public static final boolean checkRuntimeTypes = false;
	private static final Set<String> javaWhiteList = new HashSet<String>();
	
	static {
		// the whitelist that anyone can import without requiring java or becoming a resource module
		// WARNING: do NOT add anything to this list that is a resource we might conceivably want to limit!
		javaWhiteList.add("wyvern.stdlib.support.StringHelper.utils");
		javaWhiteList.add("wyvern.stdlib.support.Int.utils");
		javaWhiteList.add("wyvern.stdlib.support.AST.utils");
		javaWhiteList.add("wyvern.stdlib.support.Regex.utils");
		javaWhiteList.add("wyvern.stdlib.support.Stdio.debug");
	}
	
	public static boolean checkSafeJavaImport(String packageName) {
		return javaWhiteList.contains(packageName);
	}

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

	public static GenContext getStandardGenContext() {
      return Globals.getGenContext(new InterpreterState(InterpreterState.PLATFORM_JAVA, new File(TestUtil.BASE_PATH), new File(TestUtil.LIB_PATH)));
	}

	public static GenContext getGenContext(InterpreterState state) {
		if (state.getGenContext() != null) {
			return state.getGenContext();
		}
		GenContext genCtx = new EmptyGenContext(state).extend("system", new Variable("system"), Globals.getSystemType());
		genCtx = new TypeGenContext("Int", "system", genCtx);
		genCtx = new TypeGenContext("Unit", "system", genCtx);
		genCtx = new TypeGenContext("String", "system", genCtx);
		genCtx = new TypeGenContext("Boolean", "system", genCtx);
		genCtx = new TypeGenContext("Dyn", "system", genCtx);
		genCtx = new TypeGenContext("java", "system", genCtx);
		genCtx = new TypeGenContext("python", "system", genCtx);
		genCtx = GenUtil.ensureJavaTypesPresent(genCtx);
		return genCtx;
	}

	private static ValueType getSystemType() {
		List<FormalArg> ifTrueArgs = Arrays.asList(
				new FormalArg("trueBranch", Util.unitToDynType()),
				new FormalArg("falseBranch", Util.unitToDynType()));
    List<DeclType> boolDeclTypes = new LinkedList<DeclType>();
    boolDeclTypes.add(new DefDeclType("ifTrue", new DynamicType(), ifTrueArgs));
    boolDeclTypes.add(new DefDeclType("&&", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.booleanType()))));
    boolDeclTypes.add(new DefDeclType("||", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.booleanType()))));
		// construct a type for the system object
		List<DeclType> declTypes = new LinkedList<DeclType>();
		List<DeclType> intDeclTypes = new LinkedList<DeclType>();
		intDeclTypes.add(new DefDeclType("+", Util.intType(), Arrays.asList(new FormalArg("other", Util.intType()))));
		intDeclTypes.add(new DefDeclType("-", Util.intType(), Arrays.asList(new FormalArg("other", Util.intType()))));
		intDeclTypes.add(new DefDeclType("*", Util.intType(), Arrays.asList(new FormalArg("other", Util.intType()))));
		intDeclTypes.add(new DefDeclType("/", Util.intType(), Arrays.asList(new FormalArg("other", Util.intType()))));
		intDeclTypes.add(new DefDeclType("%", Util.intType(), Arrays.asList(new FormalArg("other", Util.intType()))));
		intDeclTypes.add(new DefDeclType("<", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.intType()))));
		intDeclTypes.add(new DefDeclType(">", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.intType()))));
		intDeclTypes.add(new DefDeclType("==", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.intType()))));
    intDeclTypes.add(new DefDeclType("negate", Util.intType(), Arrays.asList()));
		ValueType intType = new StructuralType("intSelf", intDeclTypes);
		ValueType boolType = new StructuralType("boolean", boolDeclTypes);
		declTypes.add(new ConcreteTypeMember("Int", intType));
		declTypes.add(new ConcreteTypeMember("Boolean", boolType));
		declTypes.add(new ConcreteTypeMember("Unit", Util.unitType()));
		declTypes.add(new AbstractTypeMember("String"));
		declTypes.add(new ConcreteTypeMember("Dyn", new DynamicType()));
		declTypes.add(new AbstractTypeMember("java", true));
		declTypes.add(new AbstractTypeMember("python"));
		ValueType systemType = new StructuralType("system", declTypes);
		return systemType;
	}

	public static TypeContext getStandardTypeContext() {
		GenContext ctx = GenContext.empty();
		ctx = ctx.extend("system", new Variable("system"), getSystemType());
		ctx = GenUtil.ensureJavaTypesPresent(ctx);
		return ctx;
	}

	public static EvalContext getStandardEvalContext() {
		EvalContext ctx = EvalContext.empty();
		ctx = ctx.extend("system", Globals.getSystemValue());
		return ctx;
	}

	private static ObjectValue getSystemValue() {
		// construct a type for the system object
		List<Declaration> decls = new LinkedList<Declaration>();
		decls.add(new TypeDeclaration("Int", new NominalType("this", "Int"), FileLocation.UNKNOWN));
		decls.add(new TypeDeclaration("Unit", new NominalType("this", "Unit"), FileLocation.UNKNOWN));
		decls.add(new TypeDeclaration("String", new NominalType("this", "String"), FileLocation.UNKNOWN));
		decls.add(new TypeDeclaration("Dyn", new DynamicType(), FileLocation.UNKNOWN));
		decls.add(new TypeDeclaration("java", new NominalType("this", "java"), FileLocation.UNKNOWN));
		decls.add(new TypeDeclaration("python", new NominalType("this", "python"), FileLocation.UNKNOWN));
		ObjectValue systemVal = new ObjectValue(decls, "this", getSystemType(), null, null, EvalContext.empty());
		return systemVal;
	}
}
