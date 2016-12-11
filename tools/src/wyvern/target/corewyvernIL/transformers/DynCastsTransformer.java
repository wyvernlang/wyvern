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
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.CaseType;
import wyvern.target.corewyvernIL.type.DataType;
import wyvern.target.corewyvernIL.type.DynamicType;
import wyvern.target.corewyvernIL.type.ExtensibleTagType;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;

public class DynCastsTransformer extends ASTVisitor<GenContext, ASTNode> {

	/**
	 * Check if an expression has the dynamic type.
	 * @param expr: expr whose type is to be checked.
	 * @param ctx: context in which typechecking happens.
	 */
	private boolean hasDynamicType(IExpr expr, GenContext ctx) {
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
	public New visit(GenContext ctx, New newExpr) {
		
		// Transform all declarations inside the object.
		List<Declaration> declarations = newExpr.getDecls().stream()
				.map(decl -> (Declaration) decl.acceptVisitor(this, ctx))
				.collect(Collectors.toList());
		
		// Don't bother recomputing the type--it will stay the same.
		return new New(declarations, newExpr.getSelfName(), newExpr.getExprType(), newExpr.getLocation());
		
	}

	@Override
	public Case visit(GenContext ctx, Case c) {
		throw new RuntimeException("DynCasts transformation not yet implemented for Case");
	}

	@Override
	public MethodCall visit(GenContext ctx, MethodCall methCall) {
		
		// Transform the receiver.
		IExpr receiver = (IExpr) methCall.getObjectExpr().acceptVisitor(this, ctx);
		
		// Get formal arguments of the method being invoked.
		DefDeclType formalMethCall = methCall.typeMethodDeclaration(ctx);
		List<FormalArg> formalArgs = formalMethCall.getFormalArgs();
		
		// We shall transform the actual arguments supplied to the method call.
		// Keep track of transformed arguments in a separate list.
		List<? extends IExpr> args = methCall.getArgs();
		List<IExpr> argsTransformed = new LinkedList<>();
		
		// First we transform each argument. If the transformed argument has Dyn type,
		// wrap in a cast to the formal type.
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

	@Override
	public Match visit(GenContext ctx, Match match) {
		throw new RuntimeException("Unable to perform Dyncast.transformExpr on Match expressions.");
	}

	@Override
	public FieldGet visit(GenContext ctx, FieldGet fieldGet) {
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
	public Let visit(GenContext ctx, Let let) {
		
		// Transform subexpressions.
		IExpr toReplace = (IExpr) let.getToReplace().acceptVisitor(this, ctx);
		GenContext subCtx = ctx.extend(let.getVarName(), let.getInExpr(), let.getVarType());
		IExpr inExpr = (IExpr) let.getInExpr().acceptVisitor(this, subCtx);
		
		// Add a cast if binding something with Dyn type.
		if (hasDynamicType(toReplace, ctx)) {
			ValueType cast2this = let.getVarType();
			toReplace = castFromDyn(toReplace, cast2this);
		}
		
		return new Let(let.getVarName(), let.getVarType(), toReplace, inExpr);
	}

	@Override
	public Bind visit(GenContext ctx, Bind bind) {
		throw new RuntimeException("Unable to perform DynCast.transformExpr on Bind expressions.");
	}

	@Override
	public FieldSet visit(GenContext ctx, FieldSet fieldSet) {
		
		// Transform expression being assigned. Wrap in a cast if necessary.
		IExpr toAssign = (IExpr) fieldSet.getExprToAssign().acceptVisitor(this, ctx);
		if (!(hasDynamicType(toAssign, ctx))) {
			ValueType fieldType = fieldSet.getObjectExpr().typeCheck(ctx);
			toAssign = castFromDyn(toAssign, fieldType);
		}
		
		// Transform the receiver.
		// TODO: if receiver has Dyn type, we should cast it to an object with the specified field.
		IExpr receiver = (IExpr) fieldSet.getObjectExpr().acceptVisitor(this, ctx);
		
		// Construct and return the transformed FieldSet.
		return new FieldSet(fieldSet.getExprType(), receiver, fieldSet.getFieldName(), toAssign);
		
	}

	@Override
	public Variable visit(GenContext ctx, Variable variable) {
		return variable;
	}

	@Override
	public Cast visit(GenContext ctx, Cast cast) {
		return cast;
	}

	@Override
	public VarDeclaration visit(GenContext ctx, VarDeclaration varDecl) {
		return varDecl;
	}

	@Override
	public DefDeclaration visit(GenContext ctx, DefDeclaration defDecl) {
		IExpr bodyTransformed = (IExpr) defDecl.getBody().acceptVisitor(this, ctx);
		return new DefDeclaration(defDecl.getName(), defDecl.getFormalArgs(), defDecl.getType(),
				bodyTransformed, defDecl.getLocation());
	}

	@Override
	public ValDeclaration visit(GenContext ctx, ValDeclaration valDecl) {
		return valDecl;
	}

	@Override
	public IntegerLiteral visit(GenContext ctx, IntegerLiteral integerLiteral) {
		return integerLiteral;
	}

  @Override
  public BooleanLiteral visit(GenContext ctx, BooleanLiteral booleanLiteral) {
    return booleanLiteral;
	}

	@Override
	public RationalLiteral visit(GenContext ctx, RationalLiteral rational) {
		return rational;
	}

	@Override
	public FormalArg visit(GenContext ctx, FormalArg formalArg) {
		return formalArg;
	}

	@Override
	public VarDeclType visit(GenContext ctx, VarDeclType varDeclType) {
		return varDeclType;
	}

	@Override
	public ValDeclType visit(GenContext ctx, ValDeclType valDeclType) {
		return valDeclType;
	}

	@Override
	public DefDeclType visit(GenContext ctx, DefDeclType defDeclType) {
		return defDeclType;
	}

	@Override
	public AbstractTypeMember visit(GenContext ctx, AbstractTypeMember abstractDeclType) {
		return abstractDeclType;
	}

	@Override
	public NominalType visit(GenContext ctx, NominalType nominalType) {
		return nominalType;
	}

	@Override
	public StructuralType visit(GenContext ctx, StructuralType structuralType) {
		return structuralType;
	}

	@Override
	public StringLiteral visit(GenContext ctx, StringLiteral stringLiteral) {
		return stringLiteral;
	}

	@Override
	public DelegateDeclaration visit(GenContext ctx, DelegateDeclaration delegateDecl) {
		return delegateDecl;
	}

	@Override
	public ConcreteTypeMember visit(GenContext ctx, ConcreteTypeMember concreteTypeMember) {
		return concreteTypeMember;
	}

	@Override
	public TypeDeclaration visit(GenContext ctx, TypeDeclaration typeDecl) {
		return typeDecl;
	}

	@Override
	public CaseType visit(GenContext ctx, CaseType caseType) {
		return caseType;
	}

	@Override
	public ExtensibleTagType visit(GenContext ctx, ExtensibleTagType extensibleTagType) {
		return extensibleTagType;
	}

	@Override
	public DataType visit(GenContext ctx, DataType dataType) {
		return dataType;
	}

	@Override
	public FFIImport visit(GenContext ctx, FFIImport ffiImport) {
		return ffiImport;
	}

}
