package wyvern.target.corewyvernIL.modules;

import java.util.HashSet;

import wyvern.target.corewyvernIL.support.TypeContext;

/* Adapted from wyvern.target.corewyvernIL.modules.modules */

public class Effect {
	private HashSet<String> effectSet;
	private String name;
	
	public Effect(String effectName, HashSet<String> effects) {
		this.effectSet = effects;
		this.name = effectName;
	}
	
	public HashSet<String> getEffectSet() {
		return effectSet;
	}
	
	public String getEffectName() {
		return name;
	}
	
	public void effectCheck(TypeContext ctx, TypeContext thisCtx) {
		
	}
}


///* Adapted from corewyvernIL.type.Type */
//
//package wyvern.target.corewyvernIL.type;
//
//import wyvern.target.corewyvernIL.ASTNode;
//import wyvern.target.corewyvernIL.IASTNode;
//import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
//import wyvern.target.corewyvernIL.support.TypeContext;
//import wyvern.target.corewyvernIL.support.View;
//import wyvern.tools.errors.FileLocation;
//import wyvern.tools.errors.HasLocation;
//
//public class Effect extends ASTNode implements IASTNode {
//    public Effect() {}
//    public Effect(HasLocation hasLoc) { super(hasLoc); }
//    public Effect(FileLocation loc) { super(loc); }
////	public abstract ValueType getValueType(); 
////	public abstract NominalType getParentType(View view);
//	
//	/**
//	 * Returns an effect that is equivalent to this effect
//	 * under the View v.  If v maps x to y.f, for example,
//	 * then an effect of the form x.g.T will be mapped to the
//	 * effect y.f.g.T
//	 */
//	public Effect adapt(View v) {
//		
//	}
//
//	/**
//	 * Checks if this effect is well-formed, throwing an exception if not???
//	 */
//	public void checkWellFormed(TypeContext ctx) {
//		
//	}
//	
//	@Override
//	public <S, T> T acceptVisitor(ASTVisitor <S, T> emitILVisitor,
//			S state) {
//		return emitILVisitor.visit(state, this);
//	}
//	
////	// TODO: depth limit is hacky, find a more principled approach to avoidance
////	public Type doAvoid(String varName, TypeContext ctx, int depth);
//}
