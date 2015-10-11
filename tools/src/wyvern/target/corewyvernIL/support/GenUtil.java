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
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.TypeVarDecl;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.DefDeclaration;
import wyvern.tools.typedAST.core.declarations.ValDeclaration;
import wyvern.tools.typedAST.interfaces.TypedAST;

public class GenUtil {

	/**
	 * Generate Wyvern IL Expression by a Wyvern Module System iterator inside a module. </br>
	 * @see topLevelGen
	 * 
	 * for a valdeclaration v = e:
	 * 	wrap it into let v = e in rest</br>
	 * 
	 * for a method or type declaration d: 
	 * 	wrap it into an object y, 
	 * 	and translate to wrap let y = new { d to IL declaration } in rest,
	 *  adding mapping f->y.f or T->y.T into context </br>
	 * 
	 * for an expression e:
	 *  wrap it into let y (new name) = e in rest </br>
	 * 
	 * while generating comes to the end (empty iterator)
	 *  generate an IL declaration list for all mapping inside the context,
	 *  create a new object to wrap the declaration list.</br>
	 * 
	 * @param ctx the context before generate
	 * @param ai the iterator of a Wyvern Module System declaration
	 * @return the generated IL Expression
	 */
	public static Expression doGenModuleIL(GenContext ctx, GenContext origCtx, Iterator<? extends TypedAST> ai, boolean isModule) {
		if (ai.hasNext()) {
			TypedAST ast = ai.next(); 
			// TODO (BUG): the approach of wrapping at the end won't work for VarDeclarations!
			// because the var in the object wrapper is not the same as the var in the original module
			// Maybe fix by translation of var into getters/setters?
			// or just don't support top-level vars
			if(ast instanceof TypeVarDecl || ast instanceof DefDeclaration) {
				String newName = GenContext.generateName();
				GenContext newCtx = ctx.rec(newName, ast); // extend the environment 
				wyvern.target.corewyvernIL.decl.Declaration decl = ((Declaration) ast).topLevelGen(newCtx);
				List<wyvern.target.corewyvernIL.decl.Declaration> decls =
						new LinkedList<wyvern.target.corewyvernIL.decl.Declaration>();
				List<wyvern.target.corewyvernIL.decltype.DeclType> declts =
						new LinkedList<wyvern.target.corewyvernIL.decltype.DeclType>();
				decls.add(decl);
				declts.add(((Declaration) ast).genILType(newCtx));
				ValueType type = new StructuralType(newName, declts);
				/* wrap the declaration into an object */
				Expression newExp = new New(decls, newName, type);
				Expression e = doGenModuleIL(newCtx, origCtx, ai, isModule); // generate the rest part 
				return new Let(newName, newExp, e);
			} else if (ast instanceof ValDeclaration) {
				/* same as doGenIL */
				ValDeclaration vd = (ValDeclaration) ast;
				ValueType type = ((ValDeclType)vd.genILType(ctx)).getRawResultType();
				String name = vd.getName();
				GenContext newCtx = ctx.extend(name, new wyvern.target.corewyvernIL.expression.Variable(name), type);
				Expression dfn = vd.getDefinition().generateIL(ctx);
				return new Let(name, dfn, doGenModuleIL(newCtx, origCtx, ai, isModule));
			} else if (ast instanceof DeclSequence || ast instanceof Sequence) {
				String newName = GenContext.generateName();
				
				List<wyvern.target.corewyvernIL.decl.Declaration> decls =
						new LinkedList<wyvern.target.corewyvernIL.decl.Declaration>();
				List<wyvern.target.corewyvernIL.decltype.DeclType> declts =
						new LinkedList<wyvern.target.corewyvernIL.decltype.DeclType>();
				
				GenContext newCtx = ctx;
				
				Sequence seq = (Sequence) ast;
				
				for(TypedAST seq_ast : seq.getDeclIterator()) {
					Declaration d = (Declaration) seq_ast;
					newCtx = newCtx.rec(newName, d); // extend the environment 
				}
				
				for(TypedAST seq_ast : seq.getDeclIterator()) {
					Declaration d = (Declaration) seq_ast;
					newCtx = newCtx.rec(newName, d); // extend the environment 
					wyvern.target.corewyvernIL.decl.Declaration decl = d.topLevelGen(newCtx);
					decls.add(decl);
					declts.add(d.genILType(newCtx));
				}
		
				ValueType type = new StructuralType(newName, declts);
				/* wrap the declaration into an object */
				Expression newExp = new New(decls, newName, type);
				Expression e = doGenModuleIL(newCtx, origCtx, ai, isModule); // generate the rest part 
				return new Let(newName, newExp, e);
			} else {
				/* same as doGenIL */
				Expression e1 = ast.generateIL(ctx);
				if (isModule || ai.hasNext()) {
					Expression e2 = doGenModuleIL(ctx, origCtx, ai, isModule);
					return new Let(GenContext.generateName(), e1, e2);
				} else {
					return e1;
				}
			}			
		} else {
			/* when declaration sequence come to the end, create a new object for this module */
			String newName = GenContext.generateName();
			return new New(ctx.genDeclSeq(origCtx), newName, new StructuralType(newName, ctx.genDeclTypeSeq(origCtx)));
		}
	}

	/**
	 * Linking of single modules </br>
	 * 
	 * for a simple module: 
	 * 	It is a value declaration, simply add the value into the context </br>
	 * 
	 * for a resource module: 
	 * 	It is a method declaration, wrap it into an object, add the object into the context </br>
	 * 
	 * for a type declaration: 
	 * 	It is a type declaration, add a new variable that has the same name as the type into the context </br>
	 * 
	 * @param genCtx origin context
	 * @param decl the IL Declaration generated by top level generation 
	 * @return new context
	 */
	public static GenContext link(GenContext genCtx, wyvern.target.corewyvernIL.decl.Declaration decl) {
		if(decl instanceof wyvern.target.corewyvernIL.decl.ValDeclaration) {
			wyvern.target.corewyvernIL.decl.ValDeclaration vd = (wyvern.target.corewyvernIL.decl.ValDeclaration) decl;
			return genCtx.extend(vd.getName(), vd.getDefinition(), vd.getType()); // manually adding instead of linking
		} else if (decl instanceof wyvern.target.corewyvernIL.decl.TypeDeclaration) {
			wyvern.target.corewyvernIL.decl.TypeDeclaration td = (wyvern.target.corewyvernIL.decl.TypeDeclaration) decl;
			return genCtx.extend(td.getName(), new Variable(td.getName()), (ValueType) td.getSourceType()); // manually adding instead of linking
		} else if (decl instanceof wyvern.target.corewyvernIL.decl.DefDeclaration) {
			wyvern.target.corewyvernIL.decl.Declaration methodDecl = (wyvern.target.corewyvernIL.decl.DefDeclaration) decl;
			List<wyvern.target.corewyvernIL.decl.Declaration> decls =
					new LinkedList<wyvern.target.corewyvernIL.decl.Declaration>();
			List<wyvern.target.corewyvernIL.decltype.DeclType> declts =
					new LinkedList<wyvern.target.corewyvernIL.decltype.DeclType>();
			decls.add(methodDecl);
			declts.add(methodDecl.typeCheck(genCtx, genCtx));
			ValueType type = new StructuralType(decl.getName(), declts);
			
			/* manually wrap the method into an object*/
			Expression newExp = new New(decls, decl.getName(), type);
			
			return genCtx.extend(decl.getName(), newExp, type); // adding the object into the environment, instead of linking 
		}
		
		return genCtx;
	}
}
