package wyvern.tools.typedAST.core.declarations;

import java.util.LinkedList;
import java.util.List;

import wyvern.target.corewyvernIL.BindingSite;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.ValDeclType;
import wyvern.target.corewyvernIL.generics.GenericArgument;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TopLevelContext;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.values.StringConstant;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.typedastvisitor.TypedASTVisitor;
import wyvern.tools.types.QualifiedType;
import wyvern.tools.types.Type;
import wyvern.tools.types.UnresolvedType;

public class ValDeclaration extends Declaration implements CoreAST {
    private ExpressionAST definition;
    private NameBinding binding;
    private Type declaredType;
    private String variableName;
    private ValueType cachedValueType;

    public ValDeclaration(String name, TypedAST definition, FileLocation location) {
        this.definition = (ExpressionAST) definition;
        binding = new NameBindingImpl(name, null);
        this.location = location;
    }

    public ValDeclaration(String name, Type type, TypedAST definition, FileLocation location) {
        if (type instanceof UnresolvedType) {
            variableName = name;
            declaredType = type;
        }

        this.definition = (ExpressionAST) definition;
        binding = new NameBindingImpl(name, type);
        this.location = location;
    }

    @Override
    public Type getType() {
        return binding.getType();
    }

    @Override
    public String getName() {
        return binding.getName();
    }

    public ExpressionAST getDefinition() {
        return definition;
    }

    private FileLocation location = FileLocation.UNKNOWN;
    public FileLocation getLocation() {
        return this.location; //TODO
    }


    @Override
    public <S, T> T acceptVisitor(TypedASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }

    @Override
    public void genTopLevel(TopLevelContext tlc) {
        ValueType declType = getILValueType(tlc.getContext());

        // obtain LHS and RHS types and expressions
        ValueType lhsExpressionExpectedType = getILValueType(tlc.getContext());
        wyvern.target.corewyvernIL.expression.IExpr rhsExpression = definition.generateIL(tlc.getContext(), declType, tlc.getDependencies());
        ValueType rhsExpressionType = rhsExpression.typeCheck(tlc.getContext(), null);

        // when LHS Type = RefinementType(option.Option[T]),
        // check if T matches between the base type of LHS expression and the RHS expression.
        // If so, change the RHS from a regular type T to option.Option[T] (MethodCall object)
        // If so, change the RHS type from NominalType([T]) to RefinementType(option.Option[T])

        // case when lhs has expected type of RefinementType and rhs has actually that is not of Refinement Type
        if (lhsExpressionExpectedType instanceof wyvern.target.corewyvernIL.type.RefinementType) {
          // cast the type memeber to refinement type
          wyvern.target.corewyvernIL.type.RefinementType lhsExpectedType = (wyvern.target.corewyvernIL.type.RefinementType) lhsExpressionExpectedType;

          // extract base type of the LHS expected type, "option.Option", getBase returns ValueType
          ValueType lhsBaseType = lhsExpectedType.getBase();

          // check whether the base type of the refinement type is of option.Option type
          if (lhsBaseType.equals(new NominalType("option", "Option"))) {

            // obtain the generic argument list from the lhs Refinement Type
            List<GenericArgument> genList = lhsExpectedType.getGenericArguments();

            // ensure that generic list only has 1 argument.
            // requirement for option.Option type
            if (genList.size() == 1) {

              // type check the generic argument (if it matches the rhs assignment type)
              if (rhsExpressionType.equals(genList.get(0).getType())) {

                // list of intermediate expressions, to be passed into the MethodCall object that is created.
                List<wyvern.target.corewyvernIL.expression.IExpr> iExprList = new LinkedList<>();

                // iExprList[0] = New(TypeDeclaration(type T = system.string)), type declaration
                // iExprList[1] = RHS Expression
                iExprList.add(new wyvern.target.corewyvernIL.expression.New(
                  new wyvern.target.corewyvernIL.decl.TypeDeclaration(
                      "T", 
                      rhsExpressionType,
                      this.getLocation()), 
                  this.getLocation()));
                iExprList.add(rhsExpression);

                // convert rhs actual assignment type T to lhs type option.Option[T]
                // Modify the expr to assign (RHS), to emulate the option expression based on the base type (RHS Expression Type)
                rhsExpression = new wyvern.target.corewyvernIL.expression.MethodCall((wyvern.target.corewyvernIL.expression.IExpr) new wyvern.target.corewyvernIL.expression.Variable("option", this.getLocation()),
                                              "Some",
                                              iExprList,
                                              null);
              }
              // otherwise the type T in option.Option[T] does not match the actual type T on the rhs
              // do nothing, proceed with the normal approach for assignment statements
              // implicit conversion is not performed
            }
          }
        }

        //ExpressionAST modifiedDefinition = new wyvern.tools.typedAST.core.expressions.Application(new Invocation(), arguments, this.getLocation(), genericArguments, false);
        tlc.addLet(new BindingSite(getName()),
                lhsExpressionExpectedType,
                rhsExpression,
                false);
    }

    // raises an error if the type is null
    @Override
    public void checkAnnotated(GenContext ctxWithoutThis) {
        if (binding.getType() == null) {
            try {
                ValueType vt = getILValueType(ctxWithoutThis);
            } catch (RuntimeException e) {
                ToolError.reportError(ErrorMessage.VAL_NEEDS_TYPE, this, binding.getName());
            }
        }
    }
    
    @Override
    public DeclType genILType(GenContext ctx) {
        ValueType vt = getILValueType(ctx);
        return new ValDeclType(getName(), vt);
    }

    private ValueType getILValueType(GenContext ctx) {
        /* this method does not work properly if called when the variable being
         * declared has already been added to ctx.  We solve this problem by
         * caching the value resulting from the first time this method is
         * invoked on this object */
        if (cachedValueType != null) {
            return cachedValueType;
        }
        ValueType vt;
        if (declaredType != null) {
            // convert the declared type if there is one
            vt = declaredType.getILType(ctx);
        } else {

            final Type type = this.binding.getType();
            if (type != null) {

                // then there is no proper R-value
                //if(definition == null) {
                vt = type.getILType(ctx);
            } else {
                // convert the declaration and typecheck it
                vt = definition.generateIL(ctx, null, null).typeCheck(ctx, null);
            }
        }
        cachedValueType = vt;
        return vt;
    }

    @Override
    public wyvern.target.corewyvernIL.decl.Declaration generateDecl(GenContext ctx, GenContext thisContext) {

        ValueType expectedType = getILValueType(thisContext);
        /* uses ctx for generating the definition, as the selfName is not in scope */
        return new wyvern.target.corewyvernIL.decl.ValDeclaration(getName(), expectedType, definition.generateIL(ctx, expectedType, null), location);
    }

    @Override
    public wyvern.target.corewyvernIL.decl.Declaration topLevelGen(GenContext ctx, List<TypedModuleSpec> dependencies) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addModuleDecl(TopLevelContext tlc) {
        wyvern.target.corewyvernIL.decl.Declaration decl =
                new wyvern.target.corewyvernIL.decl.ValDeclaration(getName(),
                        getILValueType(tlc.getContext()),
                        new wyvern.target.corewyvernIL.expression.Variable(getName()), location);
        DeclType dt = genILType(tlc.getContext());
        tlc.addModuleDecl(decl, dt);
    }

    public String toString() {
        return "val " + variableName + " = ...";
    }

    @Override
    public StringBuilder prettyPrint() {
        StringBuilder sb = new StringBuilder();
        sb.append("val ");
        sb.append(variableName);
        sb.append(" : ");
        if (declaredType != null) {
            sb.append(declaredType.toString());
        } else {
            sb.append("null");
        }
        sb.append(" = ");
        if (definition != null) {
            sb.append(definition.prettyPrint());
        } else {
            sb.append("null");
        }
        return sb;
    }
}
