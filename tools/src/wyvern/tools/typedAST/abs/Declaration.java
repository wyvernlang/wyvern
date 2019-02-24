package wyvern.tools.typedAST.abs;

import java.util.List;

import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TopLevelContext;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Type;
import wyvern.tools.util.AbstractTreeWritable;

// TODO: Consider adding a class "ListOfDeclarations" that only handles indents with decls and make
// Type and Class to be subtypes of that rather than this current Declaration which can be called DeclarationWithBody? (Alex)

// TODO SMELL: probably should have Declarations not be in an "evaluate" part of the AST
public abstract class Declaration extends AbstractTreeWritable implements TypedAST {

    public abstract String getName();
    public boolean isClassMember() {
        return false;
    }

    public abstract DeclType genILType(GenContext ctx);

    /** Generates code for a IL Declaration.
     * We pass in the context with and without a "this" binding. Most cases use thisContext but for var and val
     * we typecheck (and evaluate) the body before "this" is in scope, so ctx is used in that case.
     *
     * @param ctx
     * @param thisContext
     * @return
     */
    public abstract wyvern.target.corewyvernIL.decl.Declaration generateDecl(GenContext ctx, GenContext thisContext);

    /**
     * Generate IL declaration for top level Module System declaration </br>
     *
     * The difference between topLevelGen and generateDecl is: there is no this context in top level declarations of a module.</br>
     * Actually I think we can combine generateDecl and topLevelGen. </br>
     *
     * @param ctx the context
     * @param dependencies TODO
     * @return the declaration generated
     */
    public abstract wyvern.target.corewyvernIL.decl.Declaration topLevelGen(GenContext ctx, List<TypedModuleSpec> dependencies);

    //public abstract void addModuleDecl(TopLevelContext tlc);
    public void addModuleDecl(TopLevelContext tlc) {
        throw new RuntimeException("not implemented");
    }

    /**
     * Override this method when necessary.
     */
    public Type getType() {
        throw new RuntimeException();
    }
    public void checkAnnotated(GenContext ctxWithoutThis) {
        // by default does nothing; annotations are required on everything except ValDeclarations 
    }
}
