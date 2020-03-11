package wyvern.tools.parsing;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import wyvern.target.corewyvernIL.decl.ValDeclaration;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.BooleanLiteral;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.Invokable;
import wyvern.target.corewyvernIL.expression.ObjectValue;
import wyvern.target.corewyvernIL.expression.StringLiteral;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.interop.JavaValue;
import wyvern.tools.interop.JavaWrapper;
import wyvern.tools.typedAST.abs.AbstractExpressionAST;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.typedastvisitor.TypedASTVisitor;

/**
 * Created by Ben Chung on 3/11/14.
 */
public class DSLLit extends AbstractExpressionAST implements ExpressionAST {
    public static final String METADATA_TYPE_RECEIVER = "$metadataTypeReceiver$";
    private Optional<String> dslText = Optional.empty();
    private TypedAST dslAST = null;
    private FileLocation location;

    public void setText(String text) {
        if (dslText == null) {
            throw new RuntimeException();
        }
        dslText = Optional.of(text);
    }

    public Optional<String> getText() {
        return dslText;
    }

    public DSLLit(Optional<String> dslText, FileLocation loc) {
        location = loc;
        this.dslText = (dslText);
    }

    public TypedAST getAST() {
        return (dslAST);
    }

    @Override
    public FileLocation getLocation() {
        return location;
    }

    @Override
    public <S, T> T acceptVisitor(TypedASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }


    @Override
    public Expression generateIL(GenContext ctx, ValueType expectedType, List<TypedModuleSpec> dependencies) {
        if (expectedType == null) {
            ToolError.reportError(ErrorMessage.NO_EXPECTED_TYPE, this);
        }
        final wyvern.target.corewyvernIL.expression.Value metadata = expectedType.getMetadata(ctx);
        if (metadata == null) {
            ToolError.reportError(ErrorMessage.NO_METADATA_WHEN_PARSING_TSL, this, expectedType.desugar(ctx));
        }
        if (!(metadata instanceof Invokable)) {
            ToolError.reportError(ErrorMessage.METADATA_MUST_BE_AN_OBJECT, this, expectedType.toString());
        }
        ValueType metaType = metadata.getType();
        final DeclType parseTSLDecl = metaType.getStructuralType(ctx).findDecl("parseTSL", ctx);
        if (parseTSLDecl == null) {
            ToolError.reportError(ErrorMessage.METADATA_MUST_INCLUDE_PARSETSL, this, expectedType.toString());
        }
        // TODO: check that parseTSLDecl has the right signature
        List<wyvern.target.corewyvernIL.expression.Value> args = new LinkedList<wyvern.target.corewyvernIL.expression.Value>();
        args.add(new StringLiteral(dslText.get()));
        GenContext ctxWithReceiver = ctx.extend(METADATA_TYPE_RECEIVER, (Expression) expectedType.getPath(), expectedType);
        args.add(new JavaValue(JavaWrapper.wrapObject(ctxWithReceiver), new NominalType("system", "Context")));
        try {
            wyvern.target.corewyvernIL.expression.Value parsedAST = ((Invokable) metadata).invoke("parseTSL", args).executeIfThunk();
            // we get an option back, is it success?
            ValDeclaration isDefined = (ValDeclaration) ((ObjectValue) parsedAST).findDecl("isDefined", false);
            BooleanLiteral success = (BooleanLiteral) isDefined.getDefinition();
            if (success.getValue()) {
                ValDeclaration valueDecl = (ValDeclaration) ((ObjectValue) parsedAST).findDecl("value", false);
                ObjectValue astWrapper = (ObjectValue) valueDecl.getDefinition();
                ValDeclaration astDecl = (ValDeclaration) ((ObjectValue) astWrapper).findDecl("ast", false);
                IExpr definition = astDecl.getDefinition();
                if (definition instanceof JavaValue) {
                    definition = (IExpr) ((JavaValue) definition).getWrappedValue();
                }
                return (Expression) definition;
            } else {
                ToolError.reportError(ErrorMessage.TSL_ERROR, this, "[detailed TSL error messages not supported yet]");
                throw new RuntimeException("can't get here");
            }
        } catch (ToolError e) {
            if (e.getTypecheckingErrorMessage() == ErrorMessage.CANNOT_USE_METADATA_IN_SAME_FILE) {
                if (e.getLocation() == null) {
                    // provide an error with the usage location
                    ToolError.reportError(ErrorMessage.CANNOT_USE_METADATA_IN_SAME_FILE, this);
                }
            }
            FileLocation loc = e.getLocation();
            if (loc == null) {
                loc = new FileLocation("", 0, 0);
            }
            FileLocation myLoc = this.getLocation();
            FileLocation newLoc = new FileLocation(myLoc.getFilename(), myLoc.getLine() + loc.getLine() - 1, myLoc.getCharacter() + loc.getCharacter() - 1);
            ToolError updatedE = e.withNewLocation(newLoc);
            throw updatedE;
        }
    }

    @Override
    public StringBuilder prettyPrint() {
        StringBuilder sb = new StringBuilder();
        sb.append("DSLLit(TODO)");
        return sb;
    }
}
