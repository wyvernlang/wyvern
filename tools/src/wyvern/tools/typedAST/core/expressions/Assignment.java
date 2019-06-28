package wyvern.tools.typedAST.core.expressions;

import java.util.LinkedList;
import java.util.List;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.FieldGet;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.CallableExprGenerator;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.AbstractExpressionAST;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.typedastvisitor.TypedASTVisitor;
import wyvern.tools.util.GetterAndSetterGeneration;

public class Assignment extends AbstractExpressionAST implements CoreAST {

    private ExpressionAST target;
    private ExpressionAST value;
    private ExpressionAST nextExpr;
    private FileLocation location = FileLocation.UNKNOWN;

    /**
     * An assignment from a r-value (value) to an l-value (target).
     *
     * @param target the receiver of the assignment
     * @param value  the expression on the right hand side of the =
     * @param fileLocation the location in the source code where the assignment occurs
     */
    public Assignment(TypedAST target, TypedAST value, FileLocation fileLocation) {
        this.target = (ExpressionAST) target;
        this.value = (ExpressionAST) value;
        this.location = fileLocation;
    }

    public TypedAST getTarget() {
        return target;
    }

    public TypedAST getValue() {
        return value;
    }

    public TypedAST getNext() {
        return nextExpr;
    }

    public FileLocation getLocation() {
        return this.location;
    }

    @Override
    public <S, T> T acceptVisitor(TypedASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }


    private IExpr generateFieldGet(GenContext ctx, List<TypedModuleSpec> dependencies) {

        // In most cases we can get a generator to do this for us.
        CallableExprGenerator cegReceiver = target.getCallableExpr(ctx);
        if (cegReceiver.getDeclType(ctx) != null) {
            return cegReceiver.genExpr(target.getLocation());
        }

        // If the receiver is dynamic (signified by getDeclType being null),
        // we have to manually do this.
        if (target instanceof Invocation) {
            Invocation invocation = (Invocation) target;
            return new FieldGet(
                    invocation.getReceiver().generateIL(ctx, null, dependencies),
                    invocation.getOperationName(),
                    getLocation());
        } else if (target instanceof Variable) {
            return ctx.lookupExp(((Variable) target).getName(), getLocation());
        } else {
            throw new RuntimeException("Getting field of dynamic object,"
                    + "but dynamic object's AST is some unsupported type: " + target.getClass());
        }
    }

    @Override
    public Expression generateIL(
            GenContext ctx,
            ValueType expectedType,
            List<TypedModuleSpec> dependencies) {
        // Figure out expression being assigned and target it is being assigned to
        ValueType exprFieldExpectedType = null;
        IExpr exprFieldGet = generateFieldGet(ctx, dependencies);

        // obtain the type of the express field and pass it to the generateIL function for exprToAssign
        exprFieldExpectedType = exprFieldGet.typeCheck(ctx, null);
        IExpr exprToAssign = value.generateIL(ctx, exprFieldExpectedType, dependencies);
        ValueType exprType = exprToAssign.typeCheck(ctx, null);

        // Assigning to a top-level var.
        if (exprFieldGet instanceof MethodCall) {

            // Figure out the var being assigned and get the name of its setter.
            MethodCall methCall = (MethodCall) exprFieldGet;
            String methName     = methCall.getMethodName();
            String varName      = GetterAndSetterGeneration.getterToVarName(methName);
            String setterName   = GetterAndSetterGeneration.varNameToSetter(varName);

            // Return an invocation to the setter w/ appropriate argmuents supplied.
            IExpr receiver = methCall.getObjectExpr();
            List<IExpr> setterArgs = new LinkedList<>();
            setterArgs.add(exprToAssign);
            return new MethodCall(receiver, setterName, setterArgs, this);

        } else if (exprFieldGet instanceof FieldGet) {
            // Assigning to an object's field.
            // Return a FieldSet to the appropriate field.
            FieldGet fieldGet = (FieldGet) exprFieldGet;
            String fieldName = fieldGet.getName();
            IExpr objExpr = fieldGet.getObjectExpr();
            return new wyvern.target.corewyvernIL.expression.FieldSet(
                    exprType,
                    objExpr,
                    fieldName,
                    exprToAssign);
        } else {
            // Unknown what's going on.
            ToolError.reportError(ErrorMessage.NOT_ASSIGNABLE, this);
            return null;
        }
    }

}
