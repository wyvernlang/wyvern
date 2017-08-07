/**
 * @author vzhao
 */
package wyvern.target.corewyvernIL.effects;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.EffectDeclType;
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;

public class Effect {
	private Path path;
	private String name;
	private FileLocation loc;
	
	public static Set<Effect> parseEffects(String name, String effects, FileLocation fileLocation) {
		Set<Effect> effectSet = null; 
		
		if (effects==null) { // undefined (allowed by parser implementation to occur in type and any method annotations)
//			if (!declType) // i.e. undefined in module def -- the parser doesn't allow this so this is actually dead code I believe
//				ToolError.reportError(ErrorMessage.UNDEFINED_EFFECT, fileLocation, name);
		} else if (effects=="") { // empty list of effects
			effectSet = new HashSet<Effect>();
		} else if (Pattern.compile("[^a-zA-Z,. ]").matcher(effects).find()) { // found any non-effect-related chars --> probably an actual DSL block
			ToolError.reportError(ErrorMessage.MISTAKEN_DSL, fileLocation, name, effects);
		} else {
			effectSet = new HashSet<Effect>();
			for (String e : effects.split(", *")) {
				if (e.contains(".")) { // effect from another object
					String[] pathAndID = e.split("\\.");
					effectSet.add(new Effect(new Variable(pathAndID[0]), pathAndID[1], fileLocation));
				} else { // effect defined in the same type or module def
					effectSet.add(new Effect(null, e, fileLocation));
				}
			}
		}
		
		return effectSet;
	}
	
	public Effect(Variable p, String n, FileLocation l) {
		path = p;
		name = n;
		loc = l;
	}

	public Variable getPath() {
		return (Variable) path;
	}
	
	/** For effects defined in the same signature 
	 * (whose paths are null until typechecked) 
	 */
	public void setPath(Path p) { 
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
	
	/** Check that an effect exists in the context. */
	public void effectsCheck(TypeContext ctx) {	
		ValueType vt = null;
		
		// Without try/catch, this could result in a runtime exception due to EmptyGenContext 
		// (which doesn't have FileLocation or HasLocation to call ToolError.reportError())
		try {  
			// due to addPath() in generateDecl() in typedAST, getPath() will never be null 
			// when effectCheck() is called in IL's EffectDeclaration
			vt = getPath().typeCheck(ctx); 
		} catch (RuntimeException ex) { 
			// also for a recursive effect declaration (ex. effect process = process), variable name would be "var_##"
			// (could use regex to distinguish the two? May mistake a variable that is really named var_## though)
			ToolError.reportError(ErrorMessage.VARIABLE_NOT_DECLARED, getLocation(), getPath().getName()); 
		}
		
		DeclType eDT = vt.findDecl(getName(), ctx); // the effect definition as appeared in the type (ex. "effect receive = ")
		if ((eDT==null) || (!(eDT instanceof EffectDeclType))){
			ToolError.reportError(ErrorMessage.EFFECT_OF_VAR_NOT_FOUND, getLocation(), getName(), getPath().getName());
		}
	}
}