package wyvern.stdlib;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import wyvern.target.corewyvernIL.BindingSite;
import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.TypeDeclaration;
import wyvern.target.corewyvernIL.decl.ValDeclaration;
import wyvern.target.corewyvernIL.decltype.AbstractTypeMember;
import wyvern.target.corewyvernIL.decltype.ConcreteTypeMember;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.decltype.EffectDeclType;
import wyvern.target.corewyvernIL.decltype.ValDeclType;
import wyvern.target.corewyvernIL.expression.ObjectValue;
import wyvern.target.corewyvernIL.expression.SeqExpr;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.modules.Module;
import wyvern.target.corewyvernIL.support.EmptyGenContext;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.InterpreterState;
import wyvern.target.corewyvernIL.support.ModuleResolver;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.TypeOrEffectGenContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.support.VarGenContext;
import wyvern.target.corewyvernIL.type.BottomType;
import wyvern.target.corewyvernIL.type.DynamicType;
import wyvern.target.corewyvernIL.type.ExtensibleTagType;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.interop.GenUtil;
import wyvern.tools.tests.TestUtil;

public final class Globals {
    private Globals() { }
    public static final NominalType JAVA_IMPORT_TYPE = new NominalType("system", "Java");
    public static final NominalType JAVASCRIPT_IMPORT_TYPE = new NominalType("system", "JavaScript");
    public static final NominalType PYTHON_IMPORT_TYPE = new NominalType("system", "Python");
    public static final NominalType PLATFORM_IMPORT_TYPE = new NominalType("system", "Platform");
    public static final boolean checkRuntimeTypes = false;
    private static final Set<String> javaWhiteList = new HashSet<String>();
    private static final Set<String> javascriptWhiteList = new HashSet<String>();
    private static final String PRELUDE_NAME = "prelude.wyv";
    private static final BindingSite system = new BindingSite("system");

    static {
        // the whitelist that anyone can import without requiring java or becoming a resource module
        // WARNING: do NOT add anything to this list that is a resource we might conceivably want to limit!
        javaWhiteList.add("wyvern.stdlib.support.StringHelper.utils");
        javaWhiteList.add("wyvern.stdlib.support.Int.utils");
        javaWhiteList.add("wyvern.stdlib.support.Float.utils");
        javaWhiteList.add("wyvern.stdlib.support.AST.utils");
        javaWhiteList.add("wyvern.stdlib.support.Regex.utils");
        javaWhiteList.add("wyvern.stdlib.support.Stdio.debug");
        javaWhiteList.add("wyvern.stdlib.support.Sys.utils");
        javaWhiteList.add("wyvern.stdlib.support.HashMapWrapper.hashmapwrapper");
        javaWhiteList.add("wyvern.stdlib.support.ArrayWrapper.arr");
        
    }

    static {
        // the whitelist that anyone can import without requiring javascript or becoming a resource module
        // WARNING: do NOT add anything to this list that is a resource we might conceivably want to limit!
        javascriptWhiteList.add("stdlib.support.runtime");
        javascriptWhiteList.add("stdlib.support.exns");
        javascriptWhiteList.add("stdlib.support.jsinterop");
        javascriptWhiteList.add("stdlib.support.string");
        javascriptWhiteList.add("stdlib.support.float");
        javascriptWhiteList.add("stdlib.support.int");
        javascriptWhiteList.add("stdlib.support.lexing");
        javascriptWhiteList.add("stdlib.support.parsing");
        javascriptWhiteList.add("stdlib.support.regexUtils");
    }

    private static boolean gettingPrelude = false;
    private static boolean usePrelude = true;

    /** Allows us to disable the prelude for testing purposes if necessary */
    public static void setUsePrelude(boolean update) {
        usePrelude = update;
    }

    /*public static void resetPrelude() {
        prelude = null;
        gettingPrelude = false;
    }*/

    public static Module getPreludeModule() {
        if (!usePrelude) {
            throw new RuntimeException("may not call getPreludeModule if preludes are disabled");
        }
        return ModuleResolver.getLocal().getPreludeModule();
    }

    private static SeqExpr getPreludeIfPresent() {
        InterpreterState s = InterpreterState.getLocalThreadInterpreter();
        if (s == null) {
            return null;
        }
        return s.getResolver().getPreludeIfPresent();
    }

    public static SeqExpr getPrelude() {
        if (!usePrelude) {
            SeqExpr result = new SeqExpr();
            result.addBinding(system, Globals.getSystemType(), Globals.getSystemValue(), false);
            return result;
        }
        SeqExpr prelude = getPreludeIfPresent();
        if (prelude == null) {
            if (gettingPrelude) {
                return new SeqExpr();
            }
            gettingPrelude = true;
            String preludeLocation = TestUtil.LIB_PATH + PRELUDE_NAME;
            File file = new File(preludeLocation);
            prelude = ModuleResolver.getLocal().loadPrelude(file);
            gettingPrelude = false;
        }
        return prelude;
    }

    public static boolean checkSafeJavaImport(String packageName) {
        return javaWhiteList.contains(packageName);
    }

    public static boolean checkSafeJavascriptImport(String importPath) {
        return javascriptWhiteList.contains(importPath);
    }

    public static GenContext getStandardGenContext() {
        return Globals.getGenContext(new InterpreterState(InterpreterState.PLATFORM_JAVA, new File(TestUtil.BASE_PATH), new File(TestUtil.LIB_PATH)));
    }

    public static GenContext getGenContext(InterpreterState state) {
        if (state.getGenContext() != null) {
            return state.getGenContext();
        }
        // Additional primitives should also be added to primitive list in MethodCall.java bytecode generation
        GenContext genCtx = new EmptyGenContext(state).extend(system, new Variable(system), Globals.getSystemType());
        genCtx = new TypeOrEffectGenContext("Rational", system, genCtx);
        genCtx = new TypeOrEffectGenContext("Int", system, genCtx);
        genCtx = new TypeOrEffectGenContext("Float", system, genCtx);
        genCtx = new TypeOrEffectGenContext("Unit", system, genCtx);
        genCtx = new TypeOrEffectGenContext("String", system, genCtx);
        genCtx = new TypeOrEffectGenContext("Character", system, genCtx);
        genCtx = new TypeOrEffectGenContext("Boolean", system, genCtx);
        genCtx = new TypeOrEffectGenContext("Any", system, genCtx);
        genCtx = new TypeOrEffectGenContext("Nothing", system, genCtx);
        genCtx = new TypeOrEffectGenContext("Dyn", system, genCtx);
        genCtx = new TypeOrEffectGenContext("Java", system, genCtx);
        genCtx = new TypeOrEffectGenContext("Python", system, genCtx);
        genCtx = new TypeOrEffectGenContext("JavaScript", system, genCtx);
        genCtx = new TypeOrEffectGenContext("Platform", system, genCtx);
        genCtx = new VarGenContext(new BindingSite("unit"), Util.unitValue(), Util.unitType(), genCtx);
        genCtx = GenUtil.ensureJavaTypesPresent(genCtx);
        SeqExpr sexpr = getPrelude();
        GenContext newCtx = sexpr.extendContext(genCtx);
        return newCtx;
    }

    public static BindingSite getSystemSite() {
        return system;
    }

    public static ValueType getSystemType() {
        List<FormalArg> ifTrueArgs = Arrays.asList(
                new FormalArg("trueBranch", Util.unitToDynType()),
                new FormalArg("falseBranch", Util.unitToDynType()));
        List<DeclType> boolDeclTypes = new LinkedList<DeclType>();
        boolDeclTypes.add(new DefDeclType("ifTrue", new DynamicType(), ifTrueArgs));
        boolDeclTypes.add(new DefDeclType("&&", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.booleanType()))));
        boolDeclTypes.add(new DefDeclType("||", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.booleanType()))));
        boolDeclTypes.add(new DefDeclType("!", Util.booleanType(), Arrays.asList()));
        boolDeclTypes.add(new DefDeclType("==", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.booleanType()))));
        boolDeclTypes.add(new DefDeclType("!=", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.booleanType()))));

        // construct a type for the system object
        // N.B.: Operations here are built-in and performed on an instance of a type.
        // Static operations must be placed into a Wyvern module for that type.
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
        intDeclTypes.add(new DefDeclType("<=", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.intType()))));
        intDeclTypes.add(new DefDeclType(">=", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.intType()))));
        intDeclTypes.add(new DefDeclType("!=", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.intType()))));

        
        List<DeclType> floatDeclTypes = new LinkedList<DeclType>();
        floatDeclTypes.add(new DefDeclType("+", Util.floatType(), Arrays.asList(new FormalArg("other", Util.floatType()))));
        floatDeclTypes.add(new DefDeclType("-", Util.floatType(), Arrays.asList(new FormalArg("other", Util.floatType()))));
        floatDeclTypes.add(new DefDeclType("*", Util.floatType(), Arrays.asList(new FormalArg("other", Util.floatType()))));
        floatDeclTypes.add(new DefDeclType("/", Util.floatType(), Arrays.asList(new FormalArg("other", Util.floatType()))));
        floatDeclTypes.add(new DefDeclType("<", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.floatType()))));
        floatDeclTypes.add(new DefDeclType(">", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.floatType()))));
        floatDeclTypes.add(new DefDeclType("==", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.floatType()))));
        floatDeclTypes.add(new DefDeclType("negate", Util.floatType(), Arrays.asList()));
        floatDeclTypes.add(new DefDeclType("floor", Util.intType(), Arrays.asList()));
        floatDeclTypes.add(new DefDeclType("<=", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.floatType()))));
        floatDeclTypes.add(new DefDeclType(">=", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.floatType()))));
        floatDeclTypes.add(new DefDeclType("!=", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.floatType()))));
        
        
        ValueType intType = new StructuralType("intSelf", intDeclTypes);
        ValueType boolType = new StructuralType("boolean", boolDeclTypes);
        ValueType floatType = new StructuralType("float", floatDeclTypes);
        declTypes.add(new ConcreteTypeMember("Int", intType));
        declTypes.add(new ConcreteTypeMember("Boolean", boolType));
        declTypes.add(new ConcreteTypeMember("Unit", Util.unitType()));
        declTypes.add(new ConcreteTypeMember("Float", floatType));

        List<DeclType> stringDeclTypes = new LinkedList<DeclType>();
        stringDeclTypes.add(new DefDeclType("==", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.stringType()))));
        stringDeclTypes.add(new DefDeclType("<", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.stringType()))));
        stringDeclTypes.add(new DefDeclType(">", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.stringType()))));
        stringDeclTypes.add(new DefDeclType("+", Util.stringType(), Arrays.asList(new FormalArg("other", Util.stringType()))));
        stringDeclTypes.add(new DefDeclType("<=", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.stringType()))));
        stringDeclTypes.add(new DefDeclType(">=", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.stringType()))));
        stringDeclTypes.add(new DefDeclType("!=", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.stringType()))));
        
        stringDeclTypes.add(new DefDeclType("equals", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.stringType()))));
        stringDeclTypes.add(new DefDeclType("length", Util.intType(), new LinkedList<FormalArg>()));
        stringDeclTypes.add(new DefDeclType("charAt", Util.charType(), Arrays.asList(new FormalArg("index", Util.intType()))));
        stringDeclTypes.add(new DefDeclType("substring", Util.stringType(),
                Arrays.asList(new FormalArg[]{new FormalArg("start", Util.intType()), new FormalArg("end", Util.intType())})));
        stringDeclTypes.add(new DefDeclType("concat", Util.stringType(), Arrays.asList(new FormalArg("other", Util.stringType()))));
        stringDeclTypes.add(new DefDeclType("indexOf", Util.intType(), Arrays.asList(new FormalArg("ch", Util.intType()))));
        ValueType stringType = new StructuralType("stringSelf", stringDeclTypes);
        declTypes.add(new ConcreteTypeMember("String", stringType));

        List<DeclType> rationalDeclTypes = new LinkedList<>();
        rationalDeclTypes.add(new DefDeclType("+", Util.rationalType(), Arrays.asList(new FormalArg("other", Util.rationalType()))));
        rationalDeclTypes.add(new DefDeclType("-", Util.rationalType(), Arrays.asList(new FormalArg("other", Util.rationalType()))));
        rationalDeclTypes.add(new DefDeclType("*", Util.rationalType(), Arrays.asList(new FormalArg("other", Util.rationalType()))));
        rationalDeclTypes.add(new DefDeclType("/", Util.rationalType(), Arrays.asList(new FormalArg("other", Util.rationalType()))));
        rationalDeclTypes.add(new DefDeclType("<", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.rationalType()))));
        rationalDeclTypes.add(new DefDeclType(">", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.rationalType()))));
        rationalDeclTypes.add(new DefDeclType("==", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.rationalType()))));
        rationalDeclTypes.add(new DefDeclType("negate", Util.rationalType(), Arrays.asList()));
        rationalDeclTypes.add(new DefDeclType("<=", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.rationalType()))));
        rationalDeclTypes.add(new DefDeclType(">=", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.rationalType()))));
        rationalDeclTypes.add(new DefDeclType("!=", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.rationalType()))));
        ValueType rationalType = new StructuralType("rational", rationalDeclTypes);
        declTypes.add(new ConcreteTypeMember("Rational", rationalType));

        List<DeclType> charDeclTypes = new LinkedList<DeclType>();
        charDeclTypes.add(new DefDeclType("==", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.charType()))));
        charDeclTypes.add(new DefDeclType("<", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.charType()))));
        charDeclTypes.add(new DefDeclType(">", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.charType()))));
        charDeclTypes.add(new DefDeclType("!=", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.charType()))));
        charDeclTypes.add(new DefDeclType("<=", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.charType()))));
        charDeclTypes.add(new DefDeclType(">=", Util.booleanType(), Arrays.asList(new FormalArg("other", Util.charType()))));
        
        ValueType charType = new StructuralType("charSelf", charDeclTypes);
        declTypes.add(new ConcreteTypeMember("Character", charType));

        declTypes.add(new ConcreteTypeMember("Any", Util.anyType()));
        declTypes.add(new ConcreteTypeMember("Nothing", new BottomType()));
        declTypes.add(new ConcreteTypeMember("Dyn", new DynamicType()));
        NominalType systemPlatform = new NominalType("this", "Platform");
        ExtensibleTagType platformType = new ExtensibleTagType(null, Util.unitType(), systemPlatform, null);
        declTypes.add(new ConcreteTypeMember("Platform", platformType));
        NominalType systemJava = new NominalType("this", "Java");
        ExtensibleTagType javaType = new ExtensibleTagType(systemPlatform, Util.unitType(), systemJava, null);
        declTypes.add(new ConcreteTypeMember("Java", javaType));
        NominalType systemJavaScript = new NominalType("this", "JavaScript");
        ExtensibleTagType javascriptType = new ExtensibleTagType(systemPlatform, Util.unitType(), systemJavaScript, null);
        declTypes.add(new ConcreteTypeMember("JavaScript", javascriptType));
        List<DeclType> pyDeclTypes = new LinkedList<DeclType>();
        pyDeclTypes.add(new DefDeclType("toString", Util.stringType(), Arrays.asList(new FormalArg("other", Util.dynType()))));
        pyDeclTypes.add(new DefDeclType("chr", Util.stringType(), Arrays.asList(new FormalArg("other", Util.dynType()))));
        pyDeclTypes.add(new DefDeclType("isEqual", Util.booleanType(),
                Arrays.asList(new FormalArg("arg1", Util.dynType()), new FormalArg("arg2", Util.dynType()))));
        ValueType pythonType = new StructuralType("Python", pyDeclTypes);
        NominalType systemPython = new NominalType("this", "Java");
        ExtensibleTagType pythonTagType = new ExtensibleTagType(systemPlatform, pythonType, systemPython, null);
        declTypes.add(new ConcreteTypeMember("Python", pythonTagType));
        declTypes.add(new AbstractTypeMember("Context"));
        declTypes.add(new ValDeclType("unit", Util.unitType()));
        //declTypes.add(new EffectDeclType("ffiEffect", null, null));
        declTypes.add(new EffectDeclType("FFI", null, null));
        declTypes.add(new EffectDeclType("EffectNotInScope", null, null));
        ValueType systemType = new StructuralType(new BindingSite("this"), declTypes);
        return systemType;
    }

    public static TypeContext getStandardTypeContext() {
        GenContext ctx = GenContext.empty();
        ctx = ctx.extend(system, new Variable(system), getSystemType());
        ctx = GenUtil.ensureJavaTypesPresent(ctx);
        SeqExpr sexpr = getPrelude();
        GenContext newCtx = sexpr.extendContext(ctx);
        return newCtx;
    }

    public static EvalContext getStandardEvalContext() {
        EvalContext ctx = EvalContext.empty();
        ctx = ctx.extend(system, Globals.getSystemValue());
        SeqExpr sexpr = getPreludeIfPresent();
        if (sexpr != null) {
            ctx = sexpr.interpretCtx(ctx).getSecond();
        }
        return ctx;
    }

    private static Declaration platformTypeDeclaration(String platform) {
        return new TypeDeclaration(platform, ((ConcreteTypeMember) getSystemType().findDecl(platform, null)).getSourceType(), FileLocation.UNKNOWN);
    }

    public static ObjectValue getSystemValue() {
        // construct a type for the system object
        List<Declaration> decls = new LinkedList<Declaration>();
        decls.add(new TypeDeclaration("Rational", new NominalType("this", "Rational"), FileLocation.UNKNOWN));
        decls.add(new TypeDeclaration("Int", new NominalType("this", "Int"), FileLocation.UNKNOWN));
        decls.add(new TypeDeclaration("Float", new NominalType("this", "Float"), FileLocation.UNKNOWN));
        decls.add(new TypeDeclaration("Unit", Util.unitType(), FileLocation.UNKNOWN));
        decls.add(new TypeDeclaration("String", new NominalType("this", "String"), FileLocation.UNKNOWN));
        decls.add(new TypeDeclaration("Character", new NominalType("this", "Character"), FileLocation.UNKNOWN));
        decls.add(new TypeDeclaration("Dyn", new DynamicType(), FileLocation.UNKNOWN));
        decls.add(new TypeDeclaration("Any", Util.anyType(), FileLocation.UNKNOWN));
        decls.add(new TypeDeclaration("Nothing", new BottomType(), FileLocation.UNKNOWN));
        decls.add(platformTypeDeclaration("Platform"));
        decls.add(platformTypeDeclaration("Java"));
        decls.add(platformTypeDeclaration("Python"));
        decls.add(platformTypeDeclaration("JavaScript"));
        decls.add(new ValDeclaration("unit", Util.unitType(), Util.unitValue(), null));
        ObjectValue systemVal = new ObjectValue(decls, new BindingSite("this"), getSystemType(), null, null, EvalContext.empty());
        return systemVal;
    }

    /** Resets all static state in the program.
     * Should not matter, but sometimes it has in the past when testing.
     * The only static state should be the current InterpreterState and the java types object.
     */
    public static void resetState() {
        InterpreterState.resetThreadLocalInterpreter();
        GenUtil.resetJavaTypes();
        usePrelude = true;
    }
}
