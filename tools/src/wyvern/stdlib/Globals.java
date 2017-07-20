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
import wyvern.target.corewyvernIL.decltype.TaggedTypeMember;
import wyvern.target.corewyvernIL.expression.ObjectValue;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.EmptyGenContext;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.GenUtil;
import wyvern.target.corewyvernIL.support.InterpreterState;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.TypeOrEffectGenContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.DynamicType;
import wyvern.target.corewyvernIL.type.ExtensibleTagType;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.imports.extensions.JavaResolver;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.tests.TestUtil;
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
	public static final NominalType JAVA_IMPORT_TYPE = new NominalType("system", "Java");
    public static final NominalType PYTHON_IMPORT_TYPE = new NominalType("system", "Python");
	public static final NominalType PLATFORM_IMPORT_TYPE = new NominalType("system", "Platform");
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

	@Deprecated
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

	@Deprecated
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
		genCtx = new TypeOrEffectGenContext("Int", "system", genCtx);
		genCtx = new TypeOrEffectGenContext("Unit", "system", genCtx);
		genCtx = new TypeOrEffectGenContext("String", "system", genCtx);
        genCtx = new TypeOrEffectGenContext("Character", "system", genCtx);
		genCtx = new TypeOrEffectGenContext("Boolean", "system", genCtx);
		genCtx = new TypeOrEffectGenContext("Dyn", "system", genCtx);
		genCtx = new TypeOrEffectGenContext("Java", "system", genCtx);
		genCtx = new TypeOrEffectGenContext("Python", "system", genCtx);
		genCtx = new TypeOrEffectGenContext("Platform", "system", genCtx);
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

        List<DeclType> stringDeclTypes = new LinkedList<DeclType>();
        stringDeclTypes.add(new DefDeclType("==", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.stringType()))));
        stringDeclTypes.add(new DefDeclType("<", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.stringType()))));
        stringDeclTypes.add(new DefDeclType(">", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.stringType()))));
        stringDeclTypes.add(new DefDeclType("+", Util.stringType(), Arrays.asList(new FormalArg("other", Util.stringType()))));
        stringDeclTypes.add(new DefDeclType("charAt", Util.charType(), Arrays.asList(new FormalArg("index", Util.intType()))));
        stringDeclTypes.add(new DefDeclType("length", Util.intType(), new LinkedList<FormalArg>()));
        stringDeclTypes.add(new DefDeclType("substring", Util.stringType(), Arrays.asList(new FormalArg[]{new FormalArg("start", Util.intType()),new FormalArg("end", Util.intType())})));
        stringDeclTypes.add(new DefDeclType("concat", Util.stringType(), Arrays.asList(new FormalArg("other", Util.stringType()))));
        ValueType stringType = new StructuralType("stringSelf", stringDeclTypes);
        declTypes.add(new ConcreteTypeMember("String", stringType));

        List<DeclType> charDeclTypes = new LinkedList<DeclType>();
        charDeclTypes.add(new DefDeclType("==", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.charType()))));
        charDeclTypes.add(new DefDeclType("<", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.charType()))));
        charDeclTypes.add(new DefDeclType(">", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.charType()))));
        ValueType charType = new StructuralType("charSelf", charDeclTypes);
        declTypes.add(new ConcreteTypeMember("Character", charType));

        declTypes.add(new ConcreteTypeMember("Dyn", new DynamicType()));
        ExtensibleTagType platformType = new ExtensibleTagType(null, Util.unitType());
        declTypes.add(new TaggedTypeMember("Platform", platformType));
        NominalType systemPlatform = new NominalType("system", "Platform");
        ExtensibleTagType javaType = new ExtensibleTagType(systemPlatform, Util.unitType());
        declTypes.add(new TaggedTypeMember("Java", javaType));
        //declTypes.add(new AbstractTypeMember("Python"));
        List<DeclType> pyDeclTypes = new LinkedList<DeclType>();
        pyDeclTypes.add(new DefDeclType("toString", Util.stringType(), Arrays.asList(new FormalArg("other", Util.dynType()))));	
        pyDeclTypes.add(new DefDeclType("isEqual", Util.booleanType(), Arrays.asList(new FormalArg("arg1", Util.dynType()), new FormalArg("arg2", Util.dynType()))));
        ValueType pythonType = new StructuralType("Python", pyDeclTypes);
        ExtensibleTagType pythonTagType = new ExtensibleTagType(systemPlatform, pythonType);
        //declTypes.add(new ConcreteTypeMember("Python", pythonType));
        declTypes.add(new TaggedTypeMember("Python", pythonTagType));
        declTypes.add(new AbstractTypeMember("Context"));
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
		decls.add(new TypeDeclaration("Unit", Util.unitType(), FileLocation.UNKNOWN));
		decls.add(new TypeDeclaration("String", new NominalType("this", "String"), FileLocation.UNKNOWN));
        decls.add(new TypeDeclaration("Character", new NominalType("this", "Character"), FileLocation.UNKNOWN));
		decls.add(new TypeDeclaration("Dyn", new DynamicType(), FileLocation.UNKNOWN));
		decls.add(new TypeDeclaration("Java", new NominalType("this", "Java"), FileLocation.UNKNOWN));
		decls.add(new TypeDeclaration("Platform", new NominalType("this", "Platform"), FileLocation.UNKNOWN));
		decls.add(new TypeDeclaration("Python", new NominalType("this", "Python"), FileLocation.UNKNOWN));
		ObjectValue systemVal = new ObjectValue(decls, "this", getSystemType(), null, null, EvalContext.empty());
		return systemVal;
	}
}
