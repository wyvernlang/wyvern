package wyvern.target.corewyvernIL.support;

import java.util.ArrayList;
import java.util.List;

import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.decl.DefDeclaration;
import wyvern.target.corewyvernIL.decl.NamedDeclaration;
import wyvern.target.corewyvernIL.expression.Cast;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.expression.New;
import wyvern.target.corewyvernIL.expression.StringLiteral;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.ValueType;

public final class ILFactory {
    private static final ILFactory instance = new ILFactory();
    public static ILFactory instance() {
        return instance;
    }
    private ILFactory() { }

    public NominalType nominalType(String varName, String typeName) {
        return new NominalType(new Variable(varName), typeName);
    }
    public Variable variable(String varName) {
        return new Variable(varName);
    }
    public StringLiteral string(String value) {
        return new StringLiteral(value);
    }
    public Cast cast(IExpr expr, ValueType type) {
        return new Cast(expr, type);
    }
    public MethodCall call(IExpr receiver, String name, List<IExpr> args) {
        return new MethodCall(receiver, name, args, receiver);
    }
    /** Note: the module must have already been loaded; this does not load the module, only mentions an already loaded module */
    public Variable module(String qualifiedModuleName) {
        String internalName = ModuleResolver.getLocal().resolveModule(qualifiedModuleName).getSpec().getInternalName();
        return new Variable(internalName);
    }
    public New newObject(NamedDeclaration decl) {
        return new New(decl);
    }
    public DefDeclaration defDecl(String methodName, List<String> args,
            List<ValueType> argTypes,
            ValueType resultType, IExpr body) {
        List<FormalArg> argspec = new ArrayList<FormalArg>();
        for (int i = 0; i < args.size(); ++i) {
            argspec.add(new FormalArg(args.get(i), argTypes.get(i)));
        }
        return new DefDeclaration(methodName, argspec, resultType, body, body.getLocation());
    }
    public New function(String name, List<String> args, List<ValueType> argTypes, ValueType resultType, IExpr body) {
        return newObject(defDecl(name, args, argTypes, resultType, body));
    }
}
