/**
 * Typed AST of an effect; parses the definition of the effect 
 * from a String into a set of effects.
 * 
 * @author vzhao
 */

package wyvern.tools.typedAST.core.declarations;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.EffectDeclType;
import wyvern.target.corewyvernIL.effects.Effect;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TopLevelContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.EvaluationEnvironment;

public class EffectDeclaration extends Declaration {
	private String name;
	private Set<Effect> effectSet;
	private FileLocation loc;
	
	public EffectDeclaration(String name, String effects, FileLocation fileLocation) {	
		this.name = name;
		this.loc = fileLocation;
		
		/* Parses the String effects into a set. If it was not defined: 
		 * effectSet==null if in type signature, else error is reported */
		this.effectSet = Effect.parseEffects(name, effects, fileLocation);
	}
	
	public Effect getEffect() {
		return new Effect(null, getName(), getLocation()); 
	}
	
	public void doPrettyPrint(Appendable dest, String indent) throws IOException {
		dest.append(indent).append("effect ").append(getName()).append(" = ");
		if (effectSet != null) { effectSet.toString().replace("[", "{").replace("]", "}"); }
		dest.append('\n');
	}
	
	@Override
	public FileLocation getLocation() {
		return loc;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public Set<Effect> getEffectSet() {
		return effectSet;
	}
	
	@Override
	public DeclType genILType(GenContext ctx) {
		if (effectSet != null) {
			ValueType vt = null;
			try {
				vt = ctx.lookupTypeOf("this");
			} catch (RuntimeException ex) {
				// don't check effect annotations of methods this time
				// (bad programming practice, but ctx.lookupTypeOf() throws exception instead of returning a value)
			}
			
			if (vt != null) {
				for (Effect e : effectSet) {
					DeclType dt = vt.findDecl(e.getName(), ctx); // not sure about what to do w/ obj ones yet...
					if ((dt == null) || !(dt instanceof EffectDeclType)) {
						ToolError.reportError(ErrorMessage.EFFECT_NOT_IN_SCOPE, getLocation(), e.toString());
					}
				}
			}
		}
		
		return new EffectDeclType(getName(), getEffectSet(), getLocation());
	}
	
	@Override
	public wyvern.target.corewyvernIL.decl.Declaration generateDecl(GenContext ctx, GenContext thisContext) {
		if (effectSet != null) {effectSet.stream().forEach(e -> e.addPath(ctx)); }
		return new wyvern.target.corewyvernIL.decl.EffectDeclaration(getName(), getEffectSet(), getLocation());
	}
	
	@Override
	public wyvern.target.corewyvernIL.decl.Declaration topLevelGen(GenContext ctx, List<TypedModuleSpec> dependencies) {
		return generateDecl(ctx, ctx); // like in DefDeclaration
	}
	
	@Override
	public void addModuleDecl(TopLevelContext tlc) {
		wyvern.target.corewyvernIL.decl.Declaration decl = topLevelGen(tlc.getContext(), null);
		DeclType dt = genILType(tlc.getContext()); // doesn't actually uses the context
		tlc.addModuleDecl(decl,dt);
	}
	
	
	/**** Secondary or obsolete (due to use of Environment) methods. ***/
	@Override
	public Environment extendType(Environment env, Environment against) {
		// TODO Auto-generated method stub
		throw new RuntimeException("extendType not implemented");
	}
	@Override
	public Environment extendName(Environment env, Environment against) {
		// TODO Auto-generated method stub
		throw new RuntimeException("extendName not implemented");
	}
	@Override
	public Type getType() { // effects have no parsed "type" like variables/values do
		throw new RuntimeException("extendName not implemented");
	}
	@Override
	public Map<String, TypedAST> getChildren() {
		// TODO Auto-generated method stub
		throw new RuntimeException("getChildren not implemented");
	}
	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		// TODO Auto-generated method stub
		throw new RuntimeException("getChildren not implemented");
	}

	@Override
	protected Type doTypecheck(Environment env) { 
		throw new RuntimeException("doTypecheck not implemented");
	}
	@Override
	protected Environment doExtend(Environment old, Environment against) {
		// TODO Auto-generated method stub
		throw new RuntimeException("doExtend not implemented");
	}
	@Override
	public EvaluationEnvironment extendWithValue(EvaluationEnvironment old) {
		// TODO Auto-generated method stub
		throw new RuntimeException("extendWithValue not implemented");
	}
	@Override
	public void evalDecl(EvaluationEnvironment evalEnv, EvaluationEnvironment declEnv) {
		// TODO Auto-generated method stub
		throw new RuntimeException("evalDecl not implemented");
	}
	
}