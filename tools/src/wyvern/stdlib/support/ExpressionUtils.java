package wyvern.stdlib.support;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import wyvern.target.corewyvernIL.decl.NamedDeclaration;
import wyvern.target.corewyvernIL.decl.TypeDeclaration;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.expression.New;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;

public final class ExpressionUtils {
    private ExpressionUtils() { }
    public static Expression call(String receiver, String name, Expression... arguments) {
        return new MethodCall(new Variable(receiver), name, Arrays.asList(arguments), null);
    }
    public static Expression typeParam(String name, ValueType type) {
        List<NamedDeclaration> decls = new LinkedList<NamedDeclaration>();
        FileLocation loc = null;
        decls.add(typeDeclaration(name, type));
        return new New(decls, loc);
    }
    public static TypeDeclaration typeDeclaration(String typeName, ValueType sourceType) {
        return new TypeDeclaration(typeName, sourceType, null);
    }
}
