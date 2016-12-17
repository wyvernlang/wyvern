package wyvern.target.corewyvernIL.transformers;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import wyvern.target.corewyvernIL.ASTNode;
import wyvern.target.corewyvernIL.Case;
import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.DefDeclaration;
import wyvern.target.corewyvernIL.decl.DelegateDeclaration;
import wyvern.target.corewyvernIL.decl.TypeDeclaration;
import wyvern.target.corewyvernIL.decl.ValDeclaration;
import wyvern.target.corewyvernIL.decl.VarDeclaration;
import wyvern.target.corewyvernIL.decltype.AbstractTypeMember;
import wyvern.target.corewyvernIL.decltype.ConcreteTypeMember;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.decltype.ValDeclType;
import wyvern.target.corewyvernIL.decltype.VarDeclType;
import wyvern.target.corewyvernIL.expression.Bind;
import wyvern.target.corewyvernIL.expression.BooleanLiteral;
import wyvern.target.corewyvernIL.expression.Cast;
import wyvern.target.corewyvernIL.expression.FFIImport;
import wyvern.target.corewyvernIL.expression.FieldGet;
import wyvern.target.corewyvernIL.expression.FieldSet;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.expression.Match;
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.expression.New;
import wyvern.target.corewyvernIL.expression.RationalLiteral;
import wyvern.target.corewyvernIL.expression.StringLiteral;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.CaseType;
import wyvern.target.corewyvernIL.type.DataType;
import wyvern.target.corewyvernIL.type.DynamicType;
import wyvern.target.corewyvernIL.type.ExtensibleTagType;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;

public class DynCastsTransformer extends ASTVisitor<TypeContext, ASTNode> {

	/**
	 * Check if an expression has the dynamic type.
	 * @param expr: expr whose type is to be checked.
	 * @param ctx: context in which typechecking happens.
	 */
	private boolean hasDynamicType(IExpr expr, TypeContext ctx) {
		return Util.isDynamicType(expr.typeCheck(ctx));
	}
	
	/**
	 * Wraps an expression in a cast.
	 * @param expr: thing to be cast.
	 * @param type: what it should be cast to.
	 */
	private Cast castFromDyn(IExpr expr, ValueType type) {
		return new Cast(expr, type);
	}

	@Override
	public New visit(TypeContext ctx, New newExpr) {
		
		// Transform all declarations inside the object.
	    List<Declaration> newDecls = new LinkedList<>();
	    TypeContext thisCtx = ctx.extend(newExpr.getSelfName(), newExpr.getExprType());

	    for (Declaration decl : newExpr.getDecls()) {
	        Declaration newDecl = (Declaration) decl.acceptVisitor(this, thisCtx);
	        newDecls.add(newDecl);
	    }
	    
	    
		// Don't bother recomputing the type--it will stay the same.	    
		return new New(newDecls, newExpr.getSelfName(), newExpr.getExprType(), newExpr.getLocation());
		
	}

	@Override
	public Case visit(TypeContext ctx, Case c) {
		throw new RuntimeException("DynCasts transformation not yet implemented for Case");
	}

	@Override
	public MethodCall visit(TypeContext ctx, MethodCall methCall) {
		
		// Transform the receiver.
		IExpr receiver = (IExpr) methCall.getObjectExpr().acceptVisitor(this, ctx);

		// Dynamic receiver: cast object to something with appropriate method.
		if (hasDynamicType(receiver, ctx)) {
		    
		    List<? extends IExpr> actualArgs = methCall.getArgs();
		    List<FormalArg> fargs = new LinkedList<>();
		    
		    // TODO: update context with a fake "this" ?
		    for (int i = 0; i < actualArgs.size(); i++) {
		        IExpr arg = actualArgs.get(i);
		        arg = (IExpr) arg.acceptVisitor(this, ctx);
		        ValueType argType = arg.typeCheck(ctx);
		        fargs.add(new FormalArg("_arg" + i, argType));    
		    }
		    
		    // Build up the type to which the receiver shall be cast.
		    DefDeclType methodDecl = new DefDeclType(methCall.getMethodName(), Util.dynType(), fargs);
		    List<DeclType> transformedDecls = new LinkedList<>();
		    transformedDecls.add(methodDecl);
		    ValueType receiverCastType = new StructuralType("this", transformedDecls);
		    receiver = castFromDyn(receiver, receiverCastType);
		    return new MethodCall(receiver, methCall.getMethodName(), actualArgs, methCall);
		}
		
		// Non-dynamic receiver: cast any dynamic arguments to their formal type.
		else {

	        // Get formal arguments of the method being invoked.
	        DefDeclType formalMethCall = methCall.typeMethodDeclaration(ctx);
	        List<FormalArg> formalArgs = formalMethCall.getFormalArgs();
	        
	        // Transform the actual arguments supplied to the method call.
	        List<? extends IExpr> args = methCall.getArgs();
	        List<IExpr> argsTransformed = new LinkedList<>();
	        for (int i = 0; i < methCall.getArgs().size(); i++) {
	            IExpr arg = args.get(i);
	            IExpr argTransformed = (IExpr) arg.acceptVisitor(this, ctx);
	            if (hasDynamicType(argTransformed, ctx)) {
	                ValueType formalType = formalArgs.get(i).getType();
	                argTransformed = castFromDyn(argTransformed, formalType);
	            }
	            argsTransformed.add(argTransformed);
	        }
	        
	        // Construct and return the transformed method call.
	        return new MethodCall(receiver, methCall.getMethodName(), argsTransformed, methCall);

		}
		
	}

	@Override
	public Match visit(TypeContext ctx, Match match) {
		throw new RuntimeException("Unable to perform Dyncast.transformExpr on Match expressions.");
	}

	@Override
	public FieldGet visit(TypeContext ctx, FieldGet fieldGet) {
	    IExpr receiver = fieldGet.getObjectExpr();
	    ValueType receiverType = receiver.typeCheck(ctx);

        // If accessing field of object with Dyn type, cast it to something with that field.
	    if (Util.isDynamicType(receiverType)) {
	        ValDeclType fieldDecl = new ValDeclType(fieldGet.getName(), Util.dynType());
            LinkedList<DeclType> declTypes = new LinkedList<>();
            declTypes.add(fieldDecl);
            ValueType newType = new StructuralType("this", declTypes);
            return new FieldGet(castFromDyn(receiver, newType), fieldGet.getName(), fieldGet.getLocation());
	    } else {
            return fieldGet;
	    }
	}

	@Override
	public Let visit(TypeContext ctx, Let let) {
		
		// Transform subexpressions.
	    IExpr toReplace = let.getToReplace();
	    toReplace = (IExpr) toReplace.acceptVisitor(this, ctx);
	    
        // Add a cast if binding something with Dyn type.
        if (hasDynamicType(toReplace, ctx)) {
            ValueType cast2this = let.getVarType();
            toReplace = castFromDyn(toReplace, cast2this);
        }
        
		//IExpr toReplace = (IExpr) let.getToReplace().acceptVisitor(this, ctx);
		TypeContext subCtx = ctx.extend(let.getVarName(), let.getVarType());
		IExpr inExpr = let.getInExpr();
		inExpr = (IExpr) inExpr.acceptVisitor(this, subCtx);
		    
		return new Let(let.getVarName(), let.getVarType(), toReplace, inExpr);
	}

	@Override
	public Bind visit(TypeContext ctx, Bind bind) {
		throw new RuntimeException("Unable to perform DynCast.transformExpr on Bind expressions.");
	}

	@Override
	public FieldSet visit(TypeContext ctx, FieldSet fieldSet) {
		
		// Transform expression being assigned. Wrap in a cast if necessary.
		IExpr toAssign = (IExpr) fieldSet.getExprToAssign().acceptVisitor(this, ctx);
		if (hasDynamicType(toAssign, ctx)) {
			ValueType fieldType = fieldSet.getObjectExpr().typeCheck(ctx);
			toAssign = castFromDyn(toAssign, fieldType);
		}
		
		// Transform the expression on the left-hand side. If we assign to a dynamic object,
		// we should cast the receiver to an object with the specified field.
		System.out.println();
		IExpr receiver = (IExpr) fieldSet.getObjectExpr().acceptVisitor(this, ctx);
		if (hasDynamicType(receiver, ctx)) {
		    VarDeclType varDecl = new VarDeclType(fieldSet.getFieldName(), toAssign.typeCheck(ctx));
		    LinkedList<DeclType> newDecls = new LinkedList<>();
		    newDecls.add(varDecl);
		    ValueType objCastType = new StructuralType("this", newDecls);
		    receiver = castFromDyn(receiver, objCastType);
		}
		
		// If assigning to a dynamic field add a cast.
		FieldGet fg = new FieldGet(receiver, fieldSet.getFieldName(), fieldSet.getLocation());
		if (hasDynamicType(fg, ctx)) {
		    
		}
		
		// Construct and return the transformed FieldSet.
		return new FieldSet(fieldSet.getExprType(), receiver, fieldSet.getFieldName(), toAssign);
		
	}

	@Override
	public Variable visit(TypeContext ctx, Variable variable) {
		return variable;
	}

	@Override
	public Cast visit(TypeContext ctx, Cast cast) {
		return cast;
	}

	@Override
	public VarDeclaration visit(TypeContext ctx, VarDeclaration varDecl) {
		return varDecl;
	}

	@Override
	public DefDeclaration visit(TypeContext ctx, DefDeclaration defDecl) {
	    
	    // Update context with the arguments.
	    TypeContext methodCtx = ctx;
	    for (FormalArg farg : defDecl.getFormalArgs()) {
	        methodCtx = methodCtx.extend(farg.getName(), farg.getType());
	    }
	    
		IExpr bodyTransformed = (IExpr) defDecl.getBody().acceptVisitor(this, methodCtx);
		return new DefDeclaration(defDecl.getName(), defDecl.getFormalArgs(), defDecl.getType(),
				bodyTransformed, defDecl.getLocation());
	}

	@Override
	public ValDeclaration visit(TypeContext ctx, ValDeclaration valDecl) {
		return valDecl;
	}

	@Override
	public IntegerLiteral visit(TypeContext ctx, IntegerLiteral integerLiteral) {
		return integerLiteral;
	}

  @Override
  public BooleanLiteral visit(TypeContext ctx, BooleanLiteral booleanLiteral) {
    return booleanLiteral;
	}

	@Override
	public RationalLiteral visit(TypeContext ctx, RationalLiteral rational) {
		return rational;
	}

	@Override
	public FormalArg visit(TypeContext ctx, FormalArg formalArg) {
		return formalArg;
	}

	@Override
	public VarDeclType visit(TypeContext ctx, VarDeclType varDeclType) {
		return varDeclType;
	}

	@Override
	public ValDeclType visit(TypeContext ctx, ValDeclType valDeclType) {
		return valDeclType;
	}

	@Override
	public DefDeclType visit(TypeContext ctx, DefDeclType defDeclType) {
		return defDeclType;
	}

	@Override
	public AbstractTypeMember visit(TypeContext ctx, AbstractTypeMember abstractDeclType) {
		return abstractDeclType;
	}

	@Override
	public NominalType visit(TypeContext ctx, NominalType nominalType) {
		return nominalType;
	}

	@Override
	public StructuralType visit(TypeContext ctx, StructuralType structuralType) {
		return structuralType;
	}

	@Override
	public StringLiteral visit(TypeContext ctx, StringLiteral stringLiteral) {
		return stringLiteral;
	}

	@Override
	public DelegateDeclaration visit(TypeContext ctx, DelegateDeclaration delegateDecl) {
		return delegateDecl;
	}

	@Override
	public ConcreteTypeMember visit(TypeContext ctx, ConcreteTypeMember concreteTypeMember) {
		return concreteTypeMember;
	}

	@Override
	public TypeDeclaration visit(TypeContext ctx, TypeDeclaration typeDecl) {
		return typeDecl;
	}

	@Override
	public CaseType visit(TypeContext ctx, CaseType caseType) {
		return caseType;
	}

	@Override
	public ExtensibleTagType visit(TypeContext ctx, ExtensibleTagType extensibleTagType) {
		return extensibleTagType;
	}

	@Override
	public DataType visit(TypeContext ctx, DataType dataType) {
		return dataType;
	}

	@Override
	public FFIImport visit(TypeContext ctx, FFIImport ffiImport) {
		return ffiImport;
	}

}
