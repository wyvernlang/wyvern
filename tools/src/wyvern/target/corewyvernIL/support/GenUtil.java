package wyvern.target.corewyvernIL.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.ValDeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.expression.New;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.TypeVarDecl;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.DefDeclaration;
import wyvern.tools.typedAST.core.declarations.ValDeclaration;
import wyvern.tools.typedAST.interfaces.TypedAST;

public class GenUtil {

	/** Precondition: the passed-in iterator must have at least one element */
	/** Precondition: the passed-in iterator must have at least one element */
	public static Expression doGenIL(GenContext ctx, Iterator<? extends TypedAST> ai) {
		TypedAST ast = ai.next();
		if (ai.hasNext()) {
			if (ast instanceof ValDeclaration) {
				ValDeclaration vd = (ValDeclaration) ast;
				ValueType type = ((ValDeclType)vd.genILType(ctx)).getRawResultType();
				String name = vd.getName();
				GenContext newCtx = ctx.extend(name, new wyvern.target.corewyvernIL.expression.Variable(name), type);
				Expression dfn = vd.getDefinition().generateIL(ctx);
				return new Let(name, dfn, doGenIL(newCtx, ai));
			} else {
				Expression e1 = ast.generateIL(ctx);
				Expression e2 = doGenIL(ctx, ai);
				return new Let(ctx.generateName(), e1, e2);
			}			
		} else {
			return ast.generateIL(ctx);
		}
	}
	
	public static Expression doGenModuleIL(GenContext ctx, Iterator<? extends TypedAST> ai) {
		if (ai.hasNext()) {
			TypedAST ast = ai.next(); 
			if(ast instanceof TypeVarDecl || ast instanceof DefDeclaration) {
				String newName = GenContext.generateName();
				GenContext newCtx = ctx.rec(newName, ast);
				System.out.println("newCtx " + newCtx.hashCode());
				System.out.println("Ctx    " + ctx.hashCode());
				wyvern.target.corewyvernIL.decl.Declaration decl = ((Declaration) ast).topLevelGen(newCtx);
				List<wyvern.target.corewyvernIL.decl.Declaration> decls =
						new LinkedList<wyvern.target.corewyvernIL.decl.Declaration>();
				List<wyvern.target.corewyvernIL.decltype.DeclType> declts =
						new LinkedList<wyvern.target.corewyvernIL.decltype.DeclType>();
				decls.add(decl);
				declts.add(((Declaration) ast).genILType(newCtx));
				ValueType type = new StructuralType(newName, declts);
				Expression newExp = new New(decls, newName, type);
				Expression e = doGenModuleIL(newCtx, ai);
				return new Let(newName, newExp, e);
			} else if (ast instanceof ValDeclaration) {
				ValDeclaration vd = (ValDeclaration) ast;
				ValueType type = ((ValDeclType)vd.genILType(ctx)).getRawResultType();
				String name = vd.getName();
				GenContext newCtx = ctx.extend(name, new wyvern.target.corewyvernIL.expression.Variable(name), type);
				Expression dfn = vd.getDefinition().generateIL(ctx);
				return new Let(name, dfn, doGenModuleIL(newCtx, ai));
			} else {
				Expression e1 = ast.generateIL(ctx);
				Expression e2 = doGenModuleIL(ctx, ai);
				return new Let(GenContext.generateName(), e1, e2);
			}			
		} else {
			String newName = GenContext.generateName();
			return new New(ctx.genDeclSeq(), newName, new StructuralType(newName, ctx.genDeclTypeSeq()));
		}
	}
}
