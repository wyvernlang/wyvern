package wyvern.tools.typedAST.core.declarations;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashSet;

import java.util.regex.Pattern;

import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.EffectDeclType;
import wyvern.target.corewyvernIL.expression.Effect;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TopLevelContext;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.EvaluationEnvironment;

public class EffectDeclaration extends Declaration {
	Variable path;
	String name;
	HashSet<Effect> effectSet;
	FileLocation loc;
	
	public EffectDeclaration(String name, String effects, FileLocation fileLocation) { // decltype declarations
		this.name = name;
		loc = fileLocation;
		path = new Variable(name); // not sure
		
		if (effects==null) { // no definition for identifier "name" -> only possible in effectDeclType (enforced by WyvernParser.jj)
			effectSet = null;
		} else if (effects=="") { // explicitly defined to be empty list of effects
			effectSet = new HashSet<Effect>();
		} else if (Pattern.compile("[^a-zA-Z,.]").matcher(effects).find()) { // found any non-effect-related chars --> probably an actual DSL block
			throw new RuntimeException("Invalid effects--is this a DSL block instead?"); // need to change to tool error later
		} else {
			effectSet = new HashSet<Effect>();
			for (String s : name.split(", *")) {
				String[] pathAndID = s.split("\\.");
				effectSet.add(new Effect(new Variable(pathAndID[0]), pathAndID[1], null, loc));
			}
		}
	}
	
	public EffectDeclaration(String name, String effects, FileLocation loc, boolean isDeclType) {
		this(name, effects, loc);
		if (effectSet==null && !isDeclType) { // not in the type signature but nothing defined for effect set 
			new RuntimeException("Unspecified effect set outside of type signature.");
		}
	}
	
	public void genTopLevel(TopLevelContext tlc) {
		
	}
	
	@Override
	public FileLocation getLocation() {
		return loc;
	}
	@Override
	public String getName() {
		return name;
	}
	
	public HashSet<Effect> getEffectSet() {
		return effectSet;
	}
	
	public Variable getPath() {
		return path;
	}
	
	@Override
	public DeclType genILType(GenContext ctx) {
		return new EffectDeclType(getName(), getEffectSet(), getLocation());
	}
	
	@Override
	public wyvern.target.corewyvernIL.decl.Declaration generateDecl(GenContext ctx, GenContext thisContext) {
		return new wyvern.target.corewyvernIL.decl.EffectDeclaration(new Effect(getPath(), getName(), getEffectSet(), getLocation()));
//		throw new RuntimeException("generateDecl not implemented");
	}
	@Override
	public wyvern.target.corewyvernIL.decl.Declaration topLevelGen(GenContext ctx, List<TypedModuleSpec> dependencies) {
		// TODO Auto-generated method stub
		throw new RuntimeException("topLevelGen not implemented");
//		return null;
	}
	
	@Override
	public void addModuleDecl(TopLevelContext tlc) {
//		throw new RuntimeException("addModuleDecl");
	}
	
	
	/**** Secondary or obsolete (due to use of Environment) methods. ***/
	@Override
	public Environment extendType(Environment env, Environment against) {
		// TODO Auto-generated method stub
		throw new RuntimeException("extendType not implemented");
//		return null;
	}
	@Override
	public Environment extendName(Environment env, Environment against) {
		// TODO Auto-generated method stub
		throw new RuntimeException("extendName not implemented");
//		return null;
	}
	@Override
	public Type getType() { // effects have no parsed "type" like variables/values do
		return null;
	}
	@Override
	public Map<String, TypedAST> getChildren() {
		// TODO Auto-generated method stub
		throw new RuntimeException("getChildren not implemented");
//		return null;
	}
	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		// TODO Auto-generated method stub
		throw new RuntimeException("getChildren not implemented");
//		return null;
	}

	@Override
	protected Type doTypecheck(Environment env) { 
		throw new RuntimeException("doTypecheck not implemented");
//		return null;
	}
	@Override
	protected Environment doExtend(Environment old, Environment against) {
		// TODO Auto-generated method stub
		throw new RuntimeException("doExtend not implemented");
//		return null;
	}
	@Override
	public EvaluationEnvironment extendWithValue(EvaluationEnvironment old) {
		// TODO Auto-generated method stub
		throw new RuntimeException("extendWithValue not implemented");
//		return null;
	}
	@Override
	public void evalDecl(EvaluationEnvironment evalEnv, EvaluationEnvironment declEnv) {
		// TODO Auto-generated method stub
		throw new RuntimeException("evalDecl not implemented");
	}
	
}