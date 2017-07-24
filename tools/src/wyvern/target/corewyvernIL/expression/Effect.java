package wyvern.target.corewyvernIL.expression;

import java.util.HashSet;

import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.EffectDeclType;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;

public class Effect {
	private Path path;
	private String name;
	private FileLocation loc;
	
	public Effect(Variable p, String n, FileLocation l) {
		path = p;
		name = n;
		loc = l;
	}
	
	/* moved to IL EffectDeclaration */
//	public void effectCheck(TypeContext ctx, TypeContext thisCtx) { 
//		for (Effect e : effectSet) {
//			ValueType vt = ctx.lookupTypeOf(e.getName());
//			if (vt == null){
//				throw new RuntimeException("Path not found.");
//			} else {
//				if (!(vt.findDecl(e.getName(), ctx).equals(e.getDeclType()))) {
//					throw new RuntimeException("Effect name not found in path.");
//				}
//			}
//		}
//	}

	public Variable getPath() {
		return (Variable) path;
	}
	
	public String getName() {
		return name;
	}
	
	public FileLocation getLocation() {
		return loc;
	}
	
//	/* Necessary?
//	 * [INCOMPLETE]
//	 * Look up the set of effects that this represents in the context, 
//	 * use them as part of the EffectDeclType returned.
//	 */
//	public DeclType getDeclType(TypeContext ctx) {
//		return new EffectDeclType(getName(), new HashSet<Effect>(), getLocation());
//	}
}