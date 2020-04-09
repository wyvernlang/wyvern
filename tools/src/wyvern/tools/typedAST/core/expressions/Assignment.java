package wyvern.tools.typedAST.core.expressions;

import java.util.LinkedList;
import java.util.List;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.FieldGet;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.generics.GenericArgument;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.CallableExprGenerator;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.RefinementType;
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

    public static ValueType getOptionType(ValueType optionType) {
      if (optionType instanceof RefinementType) {

        // cast the type memeber to refinement type
        RefinementType optionRefinementType = (RefinementType) optionType;

        // extract base type of the passed in option type, "option.Option".
        // note that getBase returns ValueType
        ValueType optionBaseType = optionRefinementType.getBase();

        if (optionBaseType.equals(new NominalType("option", "Option")) 
         || optionBaseType.equals(new NominalType("MOD$wyvern.option", "Option"))) {

          // obtain the generic argument list from the lhs Refinement Type
          List<GenericArgument> genList = optionRefinementType.getGenericArguments();

          // ensure that generic list only has 1 argument.
          // requirement for option.Option type
          if (genList.size() == 1) {
            return genList.get(0).getType();
          }
        }
      }

      // return null if any of the type checks above failed
      return null;
    }

    public static IExpr generateOptionExpr(ValueType lhsExpressionExpectedType, IExpr rhsExpression, ValueType rhsExpressionType, FileLocation location) {
        // when LHS Type = RefinementType(option.Option[T]),
        // check if T matches between the base type of LHS expression and the RHS expression.
        // If so, change the RHS from a regular type T to option.Option[T] (MethodCall object)
        // If so, change the RHS type from NominalType([T]) to RefinementType(option.Option[T])

        // Type check: check if the LHS option type matches the RHS type
        if (rhsExpressionType.equals(getOptionType(lhsExpressionExpectedType))) {

          // list of intermediate expressions, to be passed into the MethodCall object that is created.
          List<IExpr> iExprList = new LinkedList<>();

          // iExprList[0] = New(TypeDeclaration(type T = system.string)), type declaration
          // iExprList[1] = RHS Expression
          iExprList.add(new wyvern.target.corewyvernIL.expression.New(
            new wyvern.target.corewyvernIL.decl.TypeDeclaration(
                "T", 
                rhsExpressionType,
                location), 
          location));
          iExprList.add(rhsExpression);
          
          // convert rhs actual assignment type T to lhs type option.Option[T]
          // Modify the expr to assign (RHS), to emulate the option expression based on the base type (RHS Expression Type)
          return new MethodCall((IExpr) new wyvern.target.corewyvernIL.expression.Variable("MOD$wyvern.option", location),
                                        "Some",
                                        iExprList,
                                        null);
        }
        // otherwise the type T in option.Option[T] does not match the actual type T on the rhs
        // do nothing, proceed with the normal approach for assignment statements
        // implicit conversion is not performed
        return null;
    }

    @Override
    public Expression generateIL(
            GenContext ctx,
            ValueType expectedType,
            List<TypedModuleSpec> dependencies) {

        // Figure out expression being assigned and target it is being assigned to
        IExpr lhsExpression = generateFieldGet(ctx, dependencies);

        // obtain the type of the express field and pass it to the generateIL function for exprToAssign
        ValueType lhsExpressionExpectedType = lhsExpression.typeCheck(ctx, null);
        IExpr rhsExpression = value.generateIL(ctx, lhsExpressionExpectedType, dependencies);
        ValueType rhsExpressionType = rhsExpression.typeCheck(ctx, null);

        // obtain the option expression for the rhs of the assignment
        IExpr rhsOptionExpression = wyvern.tools.typedAST.core.expressions.Assignment.generateOptionExpr(
          lhsExpressionExpectedType, 
          rhsExpression, 
          rhsExpressionType, 
          this.getLocation());

        // keep the original rhs expression or
        // obtain the option type expression
        // if passed type check.
        rhsExpression = (rhsOptionExpression == null ? rhsExpression : rhsOptionExpression); 

        // Assigning to a top-level var.
        if (lhsExpression instanceof MethodCall) {

            // Figure out the var being assigned and get the name of its setter.
            MethodCall methCall = (MethodCall) lhsExpression;
            String methName     = methCall.getMethodName();
            String varName      = GetterAndSetterGeneration.getterToVarName(methName);
            String setterName   = GetterAndSetterGeneration.varNameToSetter(varName);

            // Return an invocation to the setter w/ appropriate argmuents supplied.
            IExpr receiver = methCall.getObjectExpr();
            List<IExpr> setterArgs = new LinkedList<>();

            // pass the RHS expression as a parameter to the Method Call object
            setterArgs.add(rhsExpression);
            return new MethodCall(receiver, setterName, setterArgs, this);

        } else if (lhsExpression instanceof FieldGet) {
            // Assigning to an object's field.
            // Return a FieldSet to the appropriate field.
            FieldGet fieldGet = (FieldGet) lhsExpression;
            String fieldName = fieldGet.getName();
            IExpr objExpr = fieldGet.getObjectExpr();
            return new wyvern.target.corewyvernIL.expression.FieldSet(
                    rhsExpressionType,
                    objExpr,
                    fieldName,
                    rhsExpression);
        } else {
            // Unknown what's going on.
            ToolError.reportError(ErrorMessage.NOT_ASSIGNABLE, this);
            return null;
        }
    }

}
