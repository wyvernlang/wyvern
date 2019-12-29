package wyvern.tools.typedAST.core.declarations;

import java.util.LinkedList;
import java.util.List;

import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.expressions.TaggedInfo;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.typedastvisitor.TypedASTVisitor;


/** Represents the contents of a structural type.  Not used at the top level to my knowledge;
 * used only within TypeVarDecl.
 *
 * @author aldrich
 *
 */
public class TypeDeclaration extends AbstractTypeDeclaration implements CoreAST {
    private String name;
    private DeclSequence decls;
    private TaggedInfo taggedInfo;

    public TypeDeclaration(String name, DeclSequence decls, TaggedInfo taggedInfo, FileLocation clsNameLine) {
        this.name = name;
        this.decls = decls;
        this.location = clsNameLine;
        this.taggedInfo = taggedInfo;
    }

    public DeclSequence getDecls() {
        return decls;
    }

    public TaggedInfo getTaggedInfo() {
        return taggedInfo;
    }

    private FileLocation location = FileLocation.UNKNOWN;

    @Override
    public String toString() {
        return "TypeDeclaration(" + name + ")";
    }

    @Override
    public FileLocation getLocation() {
        return location;
    }

    @Override
    public DeclType genILType(GenContext ctx) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public wyvern.target.corewyvernIL.decl.Declaration generateDecl(GenContext ctx, GenContext thisContext) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public wyvern.target.corewyvernIL.decl.Declaration topLevelGen(GenContext ctx, List<TypedModuleSpec> dependencies) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<DeclType> genDeclTypeSeq(GenContext ctx) {
        List<DeclType> declts = new LinkedList<DeclType>();
        for (Declaration d : decls.getDeclIterator()) {
            // temporary context for verifying existence of variables within the same type so far
            if (d instanceof EffectDeclaration) {
                /* HACK: only do it for effect-checking purposes (otherwise results in NullPointerException
                 * for tests like testTSL). */
                ctx = ctx.extend(d.getName(), null, new StructuralType(d.getName(), declts));
            }
            declts.add(d.genILType(ctx));
        }

        return declts;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return name;
    }

    @Override
    public <S, T> T acceptVisitor(TypedASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }


}
