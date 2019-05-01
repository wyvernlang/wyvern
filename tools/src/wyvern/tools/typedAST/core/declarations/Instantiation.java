package wyvern.tools.typedAST.core.declarations;

import java.net.URI;
import java.util.List;

import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.typedastvisitor.TypedASTVisitor;

public class Instantiation extends Declaration implements CoreAST {

    private URI uri;
    private List<TypedAST> args;
    private FileLocation location;
    private String name;

    /**
      * Makes a new Instantiation.
      *
      * @param uri the URI at which to instantiate
      * @param arg the argument to the instantiation
      * @param loc the location in the source where the instantiation is to occur
      */
    public Instantiation(URI uri, List<TypedAST> args, String image, FileLocation loc) {
        this.uri = uri;
        this.args = args;
        this.location = loc;
        this.name = image;
    }

    @Override
    public FileLocation getLocation() {
        return location;
    }

    public URI getUri() {
        return this.uri;
    }

    public List<TypedAST> getArgs() {
        return this.args;
    }

    public String getName() {
        return name;
    }

    @Override
    public DeclType genILType(GenContext ctx) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public wyvern.target.corewyvernIL.decl.Declaration generateDecl(
            GenContext ctx,
            GenContext thisContext) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public wyvern.target.corewyvernIL.decl.Declaration topLevelGen(
            GenContext ctx,
            List<TypedModuleSpec> dependencies) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S, T> T acceptVisitor(TypedASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }


}
