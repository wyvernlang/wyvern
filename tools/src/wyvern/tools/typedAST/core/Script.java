package wyvern.tools.typedAST.core;

import java.util.List;

import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TopLevelContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractExpressionAST;
import wyvern.tools.typedAST.core.declarations.ImportDeclaration;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.typedastvisitor.TypedASTVisitor;

public class Script extends AbstractExpressionAST implements CoreAST {
    private List<ImportDeclaration> imports;
    private List<ImportDeclaration> requires;
    private Sequence body;

    public Script(List requires, List imports, TypedAST body) {
        this.imports = (List<ImportDeclaration>) imports;
        this.requires = (List<ImportDeclaration>) requires;
        this.body = body instanceof Sequence ? (Sequence) body : new Sequence(body);
    }

    public List<ImportDeclaration> getImports() {
        return imports;
    }
    public List<ImportDeclaration> getRequires() {
        return requires;
    }

    @Override
    public FileLocation getLocation() {
        return body.getLocation();
    }

    public TopLevelContext generateTLC(GenContext ctx, ValueType expectedType, List<TypedModuleSpec> dependencies) {
        TopLevelContext tlc = new TopLevelContext(ctx, expectedType);
        for (ImportDeclaration i: requires) {
            i.genTopLevel(tlc);
        }
        for (ImportDeclaration i: imports) {
            i.genTopLevel(tlc);
        }
        Sequence combinedSeq = ((Sequence) body).combine();
        combinedSeq.genTopLevel(tlc, expectedType);
        List<TypedModuleSpec> newDeps = tlc.getDependencies();
        if (!newDeps.isEmpty()) {
            dependencies.addAll(newDeps);
        }
        return tlc;
    }
    
    @Override
    public IExpr generateIL(GenContext ctx, ValueType expectedType, List<TypedModuleSpec> dependencies) {
        return this.generateTLC(ctx, expectedType, dependencies).getExpression();
    }

    @Override
    public <S, T> T acceptVisitor(TypedASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }

    @Override
    public StringBuilder prettyPrint() {
        StringBuilder sb = new StringBuilder();
        sb.append("script\n");
        sb.append("imports").append(requires);
        sb.append("requires ").append(requires);
        sb.append(body.prettyPrint());
        return sb;
    }
}
