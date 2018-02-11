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

public class Script extends AbstractExpressionAST implements CoreAST {
    private List<ImportDeclaration> imports;
    private List<ImportDeclaration> requires;
    private Sequence body;

    public Script(List requires, List imports, TypedAST body) {
        this.imports = (List<ImportDeclaration>) imports;
        this.requires = (List<ImportDeclaration>) requires;
        this.body = body instanceof Sequence ? (Sequence) body : new Sequence(body);
    }

    @Override
    public FileLocation getLocation() {
        return body.getLocation();
    }

    @Override
    public IExpr generateIL(GenContext ctx, ValueType expectedType, List<TypedModuleSpec> dependencies) {
        TopLevelContext tlc = new TopLevelContext(ctx, expectedType);
        for (ImportDeclaration i: requires) {
            i.genTopLevel(tlc);
        }
        for (ImportDeclaration i: imports) {
            i.genTopLevel(tlc);
        }
        Sequence combinedSeq = ((Sequence) body).combine();
        combinedSeq.genTopLevel(tlc, expectedType);
        dependencies.addAll(tlc.getDependencies());
        return tlc.getExpression();
    }

}
