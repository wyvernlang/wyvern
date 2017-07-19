package wyvern.target.corewyvernIL.expression;

import java.util.HashSet;

import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.EffectDeclType;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;

public class Effect {
	Variable path;
	String name;
	HashSet<Effect> effectSet;
	FileLocation loc;
	
	public Effect(Variable p, String n, HashSet<Effect> e, FileLocation l) {
		path = (Variable) p;
		name = n;
		effectSet = e;
		loc = l;
	}
	
	public void effectCheck(TypeContext ctx, TypeContext thisCtx) { // can probably just remove thisCtx
		for (Effect e : effectSet) {
			ValueType vt = ctx.lookupTypeOf(e.getName());
			if (vt == null){
				throw new RuntimeException("Path not found.");
			} else {
				if (!(vt.findDecl(e.getName(), ctx).equals(e.getDeclType()))) {
					throw new RuntimeException("Effect name not found in path.");
				}
			}
		}
	}

	public Variable getPath() {
		return path;
	}
	
	public String getName() {
		return name;
	}
	
	public HashSet<Effect> getEffectSet() {
		return effectSet;
	}
	
	public FileLocation getLocation() {
		return loc;
	}
	
	public DeclType getDeclType() {
		return new EffectDeclType(getName(), getEffectSet(), getLocation());
	}
	
}