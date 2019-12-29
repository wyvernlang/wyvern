package wyvern.tools.typedAST.core.declarations;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import wyvern.target.corewyvernIL.BindingSite;
import wyvern.target.corewyvernIL.decltype.ConcreteTypeMember;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TopLevelContext;
import wyvern.target.corewyvernIL.support.TypeOrEffectGenContext;
import wyvern.target.corewyvernIL.support.VarGenContext;
import wyvern.target.corewyvernIL.type.DataType;
import wyvern.target.corewyvernIL.type.ExtensibleTagType;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.generics.GenericParameter;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.expressions.TaggedInfo;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.typedastvisitor.TypedASTVisitor;
import wyvern.tools.types.Type;

/** Represents a declaration of a structural type.
 *
 * A thin wrapper for a TypeDeclaration (but note that I think TypeDeclaration is *only*
 * used as part of this class)
 *
 * @author aldrich
 *
 */
public class TypeVarDecl extends Declaration {
    private final String name;
    private final TypeDeclaration body;
    private final DeclSequence bodyOriginal;
    private final FileLocation fileLocation;
    private BindingSite selfSite;
    private final TypedAST metadata;
    private List<GenericParameter> generics;
    private boolean resourceFlag = false;
    private final String defaultSelfName = "this";
    private String activeSelfName;
    private IExpr metadataExp = null;

    public TypeVarDecl(String name, DeclSequence body, TaggedInfo taggedInfo,
                       List<GenericParameter> generics, TypedAST metadata, FileLocation fileLocation, boolean isResource, String selfName) {
        this.metadata = metadata;
        this.name = name;
        this.bodyOriginal = body;
        this.body = new TypeDeclaration(name, body, taggedInfo, fileLocation);
        this.generics = generics == null ? new LinkedList<>() : generics;
        this.fileLocation = fileLocation;
        this.resourceFlag = isResource;
        this.activeSelfName = selfName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public wyvern.tools.types.Type getType() {
        return null;
    }

    @Override
    public FileLocation getLocation() {
        return fileLocation;
    }

    public DeclSequence getBody() {
        return bodyOriginal;
    }

    /*@Override
    public Expression generateIL(GenContext ctx) {
        return body.generateIL(ctx);
    }*/

    private String getSelfName() {
        String s = defaultSelfName;
        if (this.activeSelfName != null && this.activeSelfName.length() != 0) {
            s = this.activeSelfName;
        }
        return s;
    }

    private BindingSite getSelfSite() {
        if (selfSite == null) {
            selfSite = new BindingSite(getSelfName());
        }
        return selfSite;
    }

    @Override
    public <S, T> T acceptVisitor(TypedASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }


    private wyvern.target.corewyvernIL.type.Type computeInternalILType(GenContext ctx) {
        TypeDeclaration td = this.body;
        GenContext ctxWithParams = ctx;
        for (GenericParameter gp : generics) {
            ctxWithParams = new TypeOrEffectGenContext(gp.getName(), getSelfSite(), ctxWithParams);
        }
        GenContext localCtx = ctxWithParams.extend(getSelfName(), new Variable(getSelfSite()), null);
        TaggedInfo taggedInfo = td.getTaggedInfo();
        StructuralType thisType = new StructuralType(getSelfSite(), td.genDeclTypeSeq(localCtx), this.resourceFlag, this.getLocation());
        if (taggedInfo == null) {
            return thisType;
        } else {
            Type parent = taggedInfo.getCaseOfTag();
            NominalType parentType = null;
            if (parent != null) {
                parentType = (NominalType) parent.getILType(ctxWithParams);
            }
            List<Type> children = taggedInfo.getComprisesTags();
            Path container = ctx.getContainerForTypeAbbrev(this.getName());
            if (container == null) {
                String containerName = ((VarGenContext) ctx).getName();
                container = new Variable(containerName);
            }
            NominalType myType = new NominalType(container, this.getName());
            if (children == null) {
                return new ExtensibleTagType(parentType, thisType, myType, getLocation());
            } else {
                final GenContext theCtx = ctxWithParams; // final alias
                List<NominalType> cases = children.stream()
                                                  .map(child -> (NominalType) child.getILType(theCtx))
                                                  .collect(Collectors.toList());
                return new DataType(parentType, thisType, myType, cases, getLocation());
            }
        }
    }

    @Override
    public String toString() {
        return "TypeVarDeclaration(" + name + ")";
    }

    @Override
    public DeclType genILType(GenContext ctx) {
        wyvern.target.corewyvernIL.type.Type type = computeInternalILType(ctx);
        return new ConcreteTypeMember(getName(), type, getMetadata(ctx));
    }

    @Override
    public wyvern.target.corewyvernIL.decl.Declaration generateDecl(GenContext ctx, GenContext thisContext) {
        return computeInternalDecl(thisContext);
    }

    private IExpr getMetadata(GenContext ctx) {
        if (metadata != null) {
            if (metadataExp == null) {
                metadataExp = ((ExpressionAST) metadata).generateIL(ctx, null, null);
            }
            return metadataExp;
        } else {
            return null;
        }
    }

    private wyvern.target.corewyvernIL.decl.Declaration computeInternalDecl(GenContext ctx) {
        wyvern.target.corewyvernIL.type.Type type = computeInternalILType(ctx);
        type.checkWellFormed(ctx);
        if (type instanceof StructuralType) {
            ((StructuralType) type).checkForDuplicates();
        }
        return new wyvern.target.corewyvernIL.decl.TypeDeclaration(getName(), type, getMetadata(ctx), getLocation());
    }

    public boolean isResource() {
        return this.resourceFlag;
    }

    @Override
    public wyvern.target.corewyvernIL.decl.Declaration topLevelGen(GenContext ctx, List<TypedModuleSpec> dependencies) {
        return computeInternalDecl(ctx);
    }

    @Override
    public void addModuleDecl(TopLevelContext tlc) {
        wyvern.target.corewyvernIL.decl.Declaration decl = computeInternalDecl(tlc.getContext());
        DeclType dt = genILType(tlc.getContext());
        tlc.addModuleDecl(decl, dt);
    }
}
