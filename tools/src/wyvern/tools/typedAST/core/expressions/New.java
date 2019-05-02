package wyvern.tools.typedAST.core.expressions;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import wyvern.target.corewyvernIL.BindingSite;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.RefinementType;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractExpressionAST;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.DefDeclaration;
import wyvern.tools.typedAST.core.declarations.VarDeclaration;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.typedastvisitor.TypedASTVisitor;
import wyvern.tools.types.Type;

public class New extends AbstractExpressionAST implements CoreAST {

    private FileLocation location = FileLocation.UNKNOWN;
    private Map<String, TypedAST> args = new HashMap<String, TypedAST>();
    private boolean isGeneric = false;
    private DeclSequence seq;
    private String selfName;
    private BindingSite site;

    /**
     * Makes a New expression with the provided mapping, file location, and self name.
     *
     * @param args The mapping from arg name to Expression.
     * @param fileLocation the location in the file where the New expression occurs
     * @param selfName the name of the object created by this expression, like 'this' in Java
     */
    public New(Map<String, TypedAST> args, FileLocation fileLocation, String selfName) {
        this.args = args;
        this.location = fileLocation;
        this.selfName = selfName;
    }

    /**
     * Makes a New expression with the provided mapping and file location.
     *
     * @param args The mapping from arg name to Expression.
     * @param fileLocation the location in the file where the New expression occurs
     */
    public New(Map<String, TypedAST> args, FileLocation fileLocation) {
        this.args = args;
        this.location = fileLocation;
        this.selfName = null;
    }

    /**
     * This constructor makes a New expression with the provided declaration sequence.
     *
     * @param seq the list of declaration internal to the object created by this expression
     * @param fileLocation the location in the file where the New expression occurs
     */
    public New(DeclSequence seq, FileLocation fileLocation) {
        this.seq = seq;
        this.location = fileLocation;
    }

    @Override
    public <S, T> T acceptVisitor(TypedASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }

    public void setBody(DeclSequence seq) {
        this.seq = seq;
    }

    public DeclSequence getDecls() {
        return seq;
    }

    private String self() {
        return (this.selfName == null) ? "this" : this.selfName;
    }

    public Map<String, TypedAST> getArgs() {
        return args;
    }

    @Override
    public FileLocation getLocation() {
        return location;
    }

    public boolean isGeneric() {
        return isGeneric;
    }

    @Override
    public Expression generateIL(
            GenContext ctx,
            ValueType expectedType,
            List<TypedModuleSpec> dependencies
            ) {

        if (site == null) {
            site = new BindingSite(this.self());
        }
        StructuralType structuralType = seq.inferStructuralType(ctx, site);
        /* We need access to both the structural contents of the object and
         * the expected type. The object can have extra decls on top of the
         * expected type it has been ascribed to, and typechecking requires
         * use of both.
         */
        ValueType type = null;
        if (expectedType != null) {
            if (expectedType.isTagged(ctx)) {
                List<DeclType> declTypes = structuralType.getDeclTypes();
                type = new RefinementType(expectedType, declTypes, this, site);
            } else {
                type = structuralType;
            }
        } else {
            type = structuralType;
        }

        // Translate the declarations.
        GenContext thisContext = ctx.extend(
                site,
                new wyvern.target.corewyvernIL.expression.Variable(site),
                type
                );
        List<wyvern.target.corewyvernIL.decl.Declaration> decls =
                new LinkedList<wyvern.target.corewyvernIL.decl.Declaration>();

        for (TypedAST d : seq) {
            wyvern.target.corewyvernIL.decl.Declaration decl = ((Declaration) d)
                    .generateDecl(ctx, thisContext);
            if (decl == null) {
                throw new NullPointerException();
            }
            decls.add(decl);

            // A VarDeclaration also generates declarations for
            // the getter and setter to the var field.
            // TODO: is the best place for this to happen?
            if (d instanceof VarDeclaration) {
                VarDeclaration varDecl = (VarDeclaration) d;
                String varName = varDecl.getName();
                Type varType = varDecl.getType();

                // Create references to "this" for the generated methods.
                wyvern.tools.typedAST.core.expressions.Variable receiver1;
                wyvern.tools.typedAST.core.expressions.Variable receiver2;

                receiver1 = new wyvern.tools.typedAST.core.expressions.Variable(
                        this.self(),
                        null
                        );
                receiver2 = new wyvern.tools.typedAST.core.expressions.Variable(
                        this.self(),
                        null
                        );

                // Generate getter and setter; add to the declarations.
                wyvern.target.corewyvernIL.decl.Declaration getter;
                wyvern.target.corewyvernIL.decl.Declaration setter;
                getter = DefDeclaration.generateGetter(ctx, receiver1, varName, varType)
                        .generateDecl(thisContext, thisContext);
                setter = DefDeclaration.generateSetter(ctx, receiver2, varName, varType)
                        .generateDecl(thisContext, thisContext);
                decls.add(getter);
                decls.add(setter);
            }
        }

        return new wyvern.target.corewyvernIL.expression.New(
                decls,
                site,
                type,
                getLocation()
                );
    }

    public void setSelfName(String n) {
        this.selfName = n;
    }
}
