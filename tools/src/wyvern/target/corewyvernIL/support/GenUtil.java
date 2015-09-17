package wyvern.target.corewyvernIL.support;

import java.util.Iterator;

import wyvern.target.corewyvernIL.decltype.ValDeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.typedAST.core.declarations.ValDeclaration;
import wyvern.tools.typedAST.interfaces.TypedAST;

public class GenUtil {

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

}
