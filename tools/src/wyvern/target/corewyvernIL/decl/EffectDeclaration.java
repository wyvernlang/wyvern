/**
 * IL representation of a defined effect.
 * Its set of effects are checked for validity.
 * 
 * @author vzhao
 */
package wyvern.target.corewyvernIL.decl;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.decltype.ConcreteTypeMember;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.EffectDeclType;
import wyvern.target.corewyvernIL.expression.Effect;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.TypeOrEffectGenContext;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.Type;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;

public class EffectDeclaration extends NamedDeclaration {
	private Set<Effect> effectSet;
	
	public EffectDeclaration(String name, Set<Effect> effectSet, FileLocation loc) {
		super(name, loc);
		this.effectSet = effectSet;
	}
	
	@Override
	public <S, T> T acceptVisitor(ASTVisitor <S, T> emitILVisitor,
			S state) {
		return emitILVisitor.visit(state, this);
	}

	public Set<Effect> getEffectSet() {
		return effectSet;
	}
	
	@Override
	public DeclType getDeclType() {
		return new EffectDeclType(getName(), getEffectSet(), getLocation());
	}

	@Override
	public DeclType typeCheck(TypeContext ctx, TypeContext thisCtx) {
		effectsCheck(ctx, thisCtx);
		return getDeclType();
	}
		
	/** Iterate through all effects in the set and check that they all exist in the context. 
	 * Errors reported are: VARIABLE_NOT_DECLARED for objects not found, and 
	 * EFFECT_NOT_FOUND for effects not from the signature or another object **/ 
	public void effectsCheck(TypeContext ctx, TypeContext thisCtx) { // technically doesn't need thisCtx	
		if (effectSet != null) {
			String ePathName;
			for (Effect e : effectSet) { // ex. "fio.read"
				if (e.getPath() == null) { // an effect that (if valid) was defined in the same type or module def
					addPath(e, ctx, thisCtx);
				}
				
				ePathName = e.getPath().getName(); // "fio"
				ValueType vt = null;
				try {
					vt = ctx.lookupTypeOf(ePathName); 
				} catch (RuntimeException ex) { // also for a recursive effect declaration (ex. effect process = {process}), variable name would be "var_##"
					ToolError.reportError(ErrorMessage.VARIABLE_NOT_DECLARED, this, ePathName); 
				}
				
				String eName = e.getName(); // "read"
				DeclType eDT = vt.findDecl(eName, ctx); // the effect definition as appeared in the type (ex. "effect receive = ")
				if (eDT==null) {
					ToolError.reportError(ErrorMessage.EFFECT_NOT_FOUND, this, eName, ePathName);
				}
			}
		}
	}
	
	/** Add path to an effect if it doesn't already have one (i.e. if it's defined in the same type or module def). **/
	public void addPath(Effect e, TypeContext ctx, TypeContext thisCtx) { // also doesn't need thisCtx
		try {
			// Without try/catch, GenContext.lookupType() reports an error that isn't specific to this problem
			ValueType eVT = ((GenContext) ctx).lookupType(e.getName(), getLocation());

			Path ePath = ((NominalType) eVT).getPath(); // not the best way, but it seems to work
			e.setPath(ePath); // path is casted into Variable at some point
		} catch (RuntimeException ex) { // for effects that should've been defined in the same module def or type
			ToolError.reportError(ErrorMessage.EFFECT_NOT_FOUND, this, e.getName(), "this"); 
		}
	}

	@Override
	public void doPrettyPrint(Appendable dest, String indent) throws IOException {
		dest.append(indent).append("effect ").append(getName()).append(" = ");
		if (effectSet != null)
			dest.append(effectSet.toString());
		dest.append('\n');
	}
	
	
	@Override
	public Set<String> getFreeVariables() {
		// TODO Auto-generated method stub
		return new HashSet<String>(); // this should either be an empty HashSet, or the entire effectSet...
//		throw new RuntimeException("getFreeVars");
	}
}