/**
 * @author vzhao
 */
package wyvern.target.corewyvernIL.effects;

import java.util.HashSet;
import java.util.Set;

import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.EffectDeclType;
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
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

	public Variable getPath() {
		return (Variable) path;
	}
	
	public void setPath(Path p) { // for effects defined in the same signature (whose paths are null until typechecked)
		path = p;
	}
	
	public String getName() {
		return name;
	}
	
	public FileLocation getLocation() {
		return loc;
	}
	
	public DeclType getDeclType(Set<Effect> effects) {
		return new EffectDeclType(getName(), effects, getLocation());
	}
	
	@Override
	public String toString() {
		return (path==null? "" : getPath().getName() + ".") + getName(); 
	}
	
	public Path adapt(View v) {
		return getPath().adapt(v);
	}	
	
	@Override
	public boolean equals(Object obj) {
		if (obj==null) return false;
		if (this == obj) return true;
	
		if (!(obj instanceof Effect)) return false;	
		
		Effect eObj = (Effect) obj;
		if (eObj.getName().equals(getName()) &&
				eObj.getPath().equals(getPath())) return true;
		return false;
	}
}