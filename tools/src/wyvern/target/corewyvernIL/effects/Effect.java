/**
 * @author vzhao
 */
package wyvern.target.corewyvernIL.effects;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import wyvern.target.corewyvernIL.decltype.ConcreteTypeMember;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.EffectDeclType;
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.GenContext;
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

	public Effect(Variable p, String n, FileLocation l) {
		this.path = p;
		this.name = n;
		this.loc = l;
	}

	public Variable getPath() {
		return (Variable) path;
	}
	
	/** For effects defined in the same signature (whose paths are null until typechecked) */
	public void setPath(Path p) { 
		path = p;
	}
	
	/** Add path to the effect if it doesn't already have one (i.e. if it's defined in the same type or module def). **/
	public void addPath(GenContext ctx) {
		/* ignore if path not found in context (i.e. null) -- this sometimes occurs in a valid setting, 
		 * such as sometimes for obj definitions in typedAST.DefDeclaration.generateDecl(), which 
		 * is made up for later in the compiling process; otherwise the effect is invalid and will be
		 * caught by effectCheck() later. */
		if (getPath()==null) {
			Path ePath = ctx.getContainerForTypeAbbrev(getName());
			setPath(ePath); // may be null
		}
	}
	
	public String getName() {
		return name;
	}
	
	public FileLocation getLocation() {
		return loc;
	}
	
//	public DeclType getDeclType(EffectSet effectSet) {
//		return new EffectDeclType(getName(), effectSet, getLocation());
//	}
	
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
	
	@Override
	public int hashCode() {
		final int prime = 67;
		int result = 1;
		result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
		result = prime * result + ((getPath() == null) ? 0 : getPath().hashCode());
		return result;
	}
	
	/** Check that an effect exists in the context, returning its corresponding effect set at the end. */
	public EffectSet effectCheck(TypeContext ctx) {	
		return findEffectDeclType(ctx).getEffectSet();
	}
	
    /** Find this effect's (effect)DeclType; report error if not found, else return effectDeclType. */
    public EffectDeclType findEffectDeclType(TypeContext ctx) {
        ValueType vt = null;
        
        // Without try/catch, this could result in a runtime exception due to EmptyGenContext 
        // (which doesn't have FileLocation or HasLocation to call ToolError.reportError())
        try {  
            // if path is null (due to failure of addPath() before) or typeCheck() fails
            if (getPath() == null) {
                // try to do an addPath
                if (ctx instanceof GenContext) {
                    addPath((GenContext)ctx);
                }
                if (getPath() == null) {
                    ToolError.reportError(ErrorMessage.EFFECT_NOT_IN_SCOPE, getLocation(), toString());
                }
            }
                
            vt = getPath().typeCheck(ctx, null); 
        } catch (RuntimeException ex) { 
            ToolError.reportError(ErrorMessage.EFFECT_NOT_IN_SCOPE, getLocation(), toString());
        }

        DeclType eDT = vt.findDecl(getName(), ctx); // the effect definition as appeared in the type (ex. "effect receive = ")
        if ((eDT==null) || (!(eDT instanceof EffectDeclType))){
            ToolError.reportError(ErrorMessage.EFFECT_NOT_IN_SCOPE, getLocation(), toString());
        }
        
        return (EffectDeclType) eDT;
    }

	/** Find this effect's (effect)DeclType in ValueType vt; report error if not found, else return effectDeclType. */
	public EffectDeclType findEffectDeclType(TypeContext ctx, ValueType vt) {
		DeclType eDT = vt.findDecl(getName(), ctx); // the effect definition as appeared in the type (ex. "effect receive = ")
		if ((eDT==null) || (!(eDT instanceof EffectDeclType))){
			ToolError.reportError(ErrorMessage.EFFECT_NOT_IN_SCOPE, getLocation(), toString());
		}
		
		return (EffectDeclType) eDT;
	}

    public Set<Effect> doAvoid(String varName, TypeContext ctx, int count) {
        if (path.getFreeVariables().contains(varName)) {
            EffectDeclType dt = this.findEffectDeclType(ctx);
            if (dt.getEffectSet() != null) {
                if (dt.getEffectSet().getEffects().size() == 1
                        && dt.getEffectSet().getEffects().iterator().next().equals(this)) {
                    // avoid infinite loops, just in case
                    // TODO: make this more principled
                    Set<Effect> s = new HashSet<Effect>();
                    s.add(this);
                    return s;
                }
                // different effects, so call recursively
                Set<Effect> s = new HashSet<Effect>();
                for (Effect e : dt.getEffectSet().getEffects()) {
                    s.addAll(e.doAvoid(varName, ctx, count+1));
                }
                return s;
            }
        }
        
        // was best effort anyway
        // TODO: be more principled
        Set<Effect> s = new HashSet<Effect>();
        s.add(this);
        return s;
    }
}