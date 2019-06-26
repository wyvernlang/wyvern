package wyvern.tools.parsing.coreparser;

import java.math.BigInteger;
import java.net.URI;
import java.util.List;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.generics.GenericArgument;
import wyvern.tools.generics.GenericParameter;

interface ASTBuilder<AST, Type> {
    AST recDecl(AST body);
    AST recConstructDecl(String name, Type type, AST body, FileLocation loc);
    
    AST sequence(AST t1, AST t2, boolean inModule);

    AST script(List<AST> requires, List<AST> imports, AST body);
    AST moduleDecl(String name, List<AST> imports, List<GenericParameter> generics,
                   List args, AST ast, Type type, FileLocation loc, boolean isResource, boolean isAnnotated, String effects);
    AST importDecl(URI uri, FileLocation loc, Token name, boolean isRequire, boolean isMetadata, boolean isLifted);
    /** if type is null, it will be inferred*/
    AST valDecl(String name, Type type, AST exp, FileLocation loc);
    AST varDecl(String name, Type type, AST exp, FileLocation loc);
    AST defDecl(String name, Type type, List<GenericParameter> generics, List args, AST body, boolean isClassDef, FileLocation loc, String effects);
    AST typeDecl(String name, AST body, Object tagInfo, AST metadata, FileLocation loc, boolean isResource, String selfName);

    AST datatypeDecl(
            String name, List<GenericParameter> generics, AST body, Object tagInfo, AST metadata, FileLocation loc, boolean isResource, String selfName
    );

    AST forwardDecl(Type type, AST exp, FileLocation loc);
    AST effectDecl(String name, String effects, FileLocation loc);
    AST assertion(String description, AST exp, FileLocation loc);


    AST defDeclType(String name, Type type, List<GenericParameter> generics, List args, FileLocation loc, String effects);
    AST valDeclType(String name, Type type, FileLocation loc);
    AST varDeclType(String name, Type type, FileLocation loc);
    AST typeAbbrevDecl(String alias, Type reference, AST metadata, FileLocation loc);
    AST effectDeclType(String name, String effects, FileLocation loc);

    AST constructDeclType(String name, List<GenericParameter> generics, List args, FileLocation loc);

    Object formalArg(String name, Type type);
    AST fn(List args, AST body, FileLocation loc);
    AST var(String name, FileLocation loc);
    AST stringLit(String value, FileLocation loc);
    AST characterLit(char value, FileLocation loc);
    AST integerLit(BigInteger value, FileLocation loc);
    AST booleanLit(boolean value, FileLocation loc);
    AST invocation(AST receiver, String name, AST argument, FileLocation loc);
    AST application(AST function, List<AST> arguments, FileLocation loc, List<GenericArgument> genericArguments, boolean recur);
    AST addArguments(AST application, List<String> names, List<AST> arguments) throws ParseException;
    AST assignment(AST lhs, AST rhs, FileLocation loc);
    AST unitValue(FileLocation loc);
    AST newObj(FileLocation loc, String selfName);
    AST dsl(FileLocation loc);
    AST match(AST exp, List cases, FileLocation loc);

    Object caseArm(String name, Type type, AST exp, FileLocation loc);
    Object tagInfo(Type type, List<Type> comprises);

    Type nominalType(String name, FileLocation loc);
    Type arrowType(List<Type> arguments, Type result, boolean isResource, String effects, FileLocation loc);
    Type parameterizedType(Type base, List<GenericArgument> genericArguments, FileLocation loc);

    Type qualifiedType(AST base, String name);

    void setNewBody(AST newExp, AST decls);
    void setDSLBody(AST dslExp, String text);

    AST instantiation(URI uri, List<AST> args, Token name, FileLocation loc);

    void addArgument(AST application, AST argument) throws ParseException;

    AST parseExpr(String source, FileLocation loc);

    AST floatLit(float parseFloat, FileLocation loc);

}
