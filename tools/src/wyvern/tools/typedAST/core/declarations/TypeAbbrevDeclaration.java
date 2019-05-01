package wyvern.tools.typedAST.core.declarations;

import static wyvern.tools.errors.ToolError.reportError;

import java.util.List;

import wyvern.target.corewyvernIL.decl.TypeDeclaration;
import wyvern.target.corewyvernIL.decltype.AbstractTypeMember;
import wyvern.target.corewyvernIL.decltype.ConcreteTypeMember;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TopLevelContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.typedastvisitor.TypedASTVisitor;
import wyvern.tools.types.Type;

// TODO: this represents an abstract type when the reference is null.
// would be better to have a separate class for representing an abstract type

/** Represents a type abbreviation of the form:
 *
 * type T = T1
 *
 * @author aldrich
 *
 */
public class TypeAbbrevDeclaration extends Declaration implements CoreAST {

    private String alias;
    private Type reference;
    private FileLocation location;
    private TypedAST metadata;

    public TypeAbbrevDeclaration(String alias, Type reference, TypedAST metadata, FileLocation loc) {
        this.alias = alias;
        this.reference = reference;
        this.location = loc;
        this.metadata = metadata;
    }

    @Override
    public FileLocation getLocation() {
        return location == null ? reference.getLocation() : location;
    }

    @Override
    public String getName() {
        return alias;
    }

    @Override
    public DeclType genILType(GenContext ctx) {
        if (this.reference == null) {
            return new AbstractTypeMember(this.alias);
        }
        ValueType referenceILType = reference.getILType(ctx);

        IExpr metadataExp = null;
        if (metadata != null) {
            metadataExp = ((ExpressionAST) metadata).generateIL(ctx, null, null);
        }

        return new ConcreteTypeMember(getName(), referenceILType, metadataExp);
    }

    @Override
    public wyvern.target.corewyvernIL.decl.Declaration generateDecl(GenContext ctx, GenContext thisContext) {
        if (reference == null) {
            System.out.println("reference is null with alias =" + alias + ", location = " + getLocation().toString());
        }
        return new TypeDeclaration(alias, reference.getILType(ctx), getLocation());
    }

    @Override
    public wyvern.target.corewyvernIL.decl.Declaration topLevelGen(GenContext ctx, List<TypedModuleSpec> dependencies) {
        if (reference == null) {
            reportError(ErrorMessage.NO_ABSTRACT_TYPES_IN_OBJECTS, this);
        }
        ValueType referenceILType = reference.getILType(ctx);
        referenceILType.checkWellFormed(ctx);

        IExpr metadataExp = null;
        if (metadata != null) {
            metadataExp = ((ExpressionAST) metadata).generateIL(ctx, null, null);
        }
        return new TypeDeclaration(getName(), referenceILType, metadataExp, getLocation());
    }

    @Override
    public void addModuleDecl(TopLevelContext tlc) {
        wyvern.target.corewyvernIL.decl.Declaration decl = topLevelGen(tlc.getContext(), null);
        DeclType dt = genILType(tlc.getContext());
        tlc.addModuleDecl(decl, dt);
    }

    @Override
    public <S, T> T acceptVisitor(TypedASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }

}
