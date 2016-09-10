package wyvern.target.corewyvernIL.transformers;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.DefDeclaration;
import wyvern.target.corewyvernIL.decl.DelegateDeclaration;
import wyvern.target.corewyvernIL.decl.NamedDeclaration;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.expression.AbstractValue;
import wyvern.target.corewyvernIL.expression.Bind;
import wyvern.target.corewyvernIL.expression.Cast;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.FFIImport;
import wyvern.target.corewyvernIL.expression.FieldGet;
import wyvern.target.corewyvernIL.expression.FieldSet;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.expression.Match;
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.expression.New;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.DynamicType;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.ValueType;

public class DynCastsTransformer implements ILTransformer {

	@Override
	public IExpr transform(IExpr ast)  {
		return transform(ast, Globals.getStandardGenContext());
	}
	
	private IExpr transform(IExpr ast, GenContext ctx) {
		return transformIExpr(ast, ctx);
	}

	private IExpr transformIExpr(IExpr expr, GenContext ctx) {
		
		// Transforming Expression.
		if (expr instanceof Expression) {
			return transformExpr((Expression)expr, ctx);
		}

		// Transforming literal.
		else if (expr instanceof Value) {
			return expr;
		}
		
		// Some unsupported IExpr being transformed.
		else {
			throw new RuntimeException("Unable to perform DynCast transformation on an AST of type " + expr.getClass());
		}
		
	}
	
	private Expression transformExpr(Expression expr, GenContext ctx) {
		
		if (expr instanceof AbstractValue) return expr;
		
		// TODO: Transforming Bind.
		if (expr instanceof Bind) {
			return null;
		}
		
		if (expr instanceof Cast) return expr;
		if (expr instanceof FFIImport) return expr;
		
		// TODO: if receiver has Dyn type, cast it to something with the specified field.
		if (expr instanceof FieldGet) {
			return null;
		}
		
		if (expr instanceof FieldSet) {
			
			// Transform expression being assigned; wrap in a cast if necessary.
			FieldSet fieldSet = (FieldSet) expr;
			IExpr toAssign = transform(fieldSet.getExprToAssign(), ctx);
			if (!(hasDynamicType(toAssign, ctx))) {
				ValueType fieldType = fieldSet.getObjectExpr().typeCheck(ctx);
				toAssign = castFromDyn(toAssign, fieldType);
			}
			
			// Transform the receiver.
			// TODO: if receiver has Dyn type, cast to something with the specified field.
			IExpr receiver = transform(fieldSet.getObjectExpr(), ctx);
			return new FieldSet(fieldSet.getExprType(),
								 receiver,
								 fieldSet.getFieldName(),
								 toAssign);
		}
		
		if (expr instanceof Let) {
			
			// Transform subexpressions.
			Let let = (Let) expr;
			IExpr toReplace = transform(let.getToReplace(), ctx);
			GenContext subCtx = ctx.extend(let.getVarName(),  let.getInExpr(), let.getVarType());
			IExpr inExpr = transform(let.getInExpr(), subCtx);
			
			// Add a cast if binding something of type Dyn.
			if (hasDynamicType(toReplace, ctx)) {
				ValueType cast2this = let.getVarType();
				toReplace = castFromDyn(toReplace, cast2this);
			}
			
			return new Let(let.getVarName(),
							let.getVarType(),
							toReplace,
							inExpr);
		}
		
		// TODO: transforming Match.
		if (expr instanceof Match) {
			// TODO
			return null;
		}
		
		if (expr instanceof MethodCall) {
			
			// Transform the receiver.
			// TODO: cast receiver to something with specified method, if they are Dyn.
			MethodCall methCall = (MethodCall) expr;
			IExpr receiver = transform(methCall.getObjectExpr(), ctx);
			
			// Get formal arguments of the method being invoked.
			DefDeclType formalMethCall = methCall.getMethodDeclaration(ctx);
			List<FormalArg> formalArgs = formalMethCall.getFormalArgs();
			
			// We will transform each argument to this method call.
			List<? extends IExpr> args = methCall.getArgs();
			List<IExpr> argsTransformed = new LinkedList<>();
			
			// Transform each argument; if it has Dyn type, cast it to the formal argument's type.
			for (int i = 0; i < methCall.getArgs().size(); i++) {
				IExpr arg = args.get(i);
				IExpr argTransformed = transform(arg, ctx);
				if (hasDynamicType(argTransformed, ctx)) {
					ValueType formalType = formalArgs.get(i).getType();
					argTransformed = castFromDyn(argTransformed, formalType);
				}
				argsTransformed.add(argTransformed);
			}
			
			// Return the transformed method call.
			return new MethodCall(receiver,
					               methCall.getMethodName(),
					               argsTransformed,
					               methCall); // TODO: not sure about putting this as the file location
			
		}
		
		if (expr instanceof New) {
			
			// Transform all declarations inside the object.
			New obj = (New) expr;
			List<Declaration> declarations = obj.getDecls().stream()
					.map(decl -> transformDecl(decl, ctx))
					.collect(Collectors.toList());
			
			// Don't bother recomputing the type--it will be the same.
			return new New(declarations, obj.getSelfName(), obj.getExprType(), obj.getLocation());
		}
		
		if (expr instanceof Variable) return expr;
		
			
		throw new RuntimeException("Unable to perform DynCast.transformExpr on " + expr.getClass().toString());
		
	}

	public Declaration transformDecl(Declaration decl, GenContext ctx) {
		
		// TODO delegate declarations.
		if (decl instanceof DelegateDeclaration) {
			throw new RuntimeException("transformDecl not implemented for delegate declarations.");
		}
		
		// Transform the method body.
		if (decl instanceof DefDeclaration) {
			DefDeclaration defDecl = (DefDeclaration) decl;
			IExpr bodyTransformed = transform(defDecl.getBody(), ctx);
			return new DefDeclaration(defDecl.getName(), defDecl.getFormalArgs(),
					defDecl.getType(), bodyTransformed, defDecl.getLocation());
		}
		
		// Other declarations stay the same.
		if (decl instanceof NamedDeclaration) {
			return decl;
		}
		
		throw new RuntimeException("transformDecl not implemented for " + decl.getClass().toString());
	}
	
	/**
	 * Check if an expression has the dynamic type.
	 * @param expr: expr whose type is to be checked.
	 * @param ctx: context in which typechecking happens.
	 */
	private boolean hasDynamicType(IExpr expr, GenContext ctx) {
		ValueType type = expr.typeCheck(ctx);
		return type.equals(new NominalType("system", "Dyn"))
				|| type instanceof DynamicType;
	}
	
	/**
	 * Wraps an expression in a cast.
	 * @param expr: thing to be cast.
	 * @param type: what it should be cast to.
	 */
	private Cast castFromDyn(IExpr expr, ValueType type) {
		return new Cast(expr, type);
	}

}
