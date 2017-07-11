package wyvern.tools.typedAST.core.declarations;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import java.util.regex.Pattern;

import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.EffectDeclType;
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
	String name;
	ArrayList<String> effectsList;
	FileLocation loc;
	public EffectDeclaration(String name, String effects, FileLocation fileLocation) { // decltype declarations
		this.name = name;
		loc = fileLocation;
		
		if (effects==null) { // no declared effect for identifier "name"
			effectsList = null;
		} else if (effects=="") { // explicitly specified to be empty list of effects; null effects is only possible in effectDeclType (enforced by WyvernParser.jj)
			effectsList = new ArrayList<String>(); // may need a flag instead to indicate that items will be added to it in module def for null effects
	//} else if (Character.isWhitespace(effects.charAt(0))) { // <DSLLINE>?
		} else if (Pattern.compile("[^a-zA-Z,.]").matcher(effects).find()) { // found any non-effect-related chars --> actual DSL block
			throw new RuntimeException("Invalid effects--is this a DSL block instead?"); // need to change error type later
		} else {
			effectsList = new ArrayList<String>(Arrays.asList(name.split(", *")));
		}
	}
	
	public void genTopLevel(TopLevelContext tlc) {
		
	}
	
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
		// TODO Auto-generated method stub
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
	public FileLocation getLocation() {
		// TODO Auto-generated method stub
		return loc;
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}
	@Override
	/* Would this be the method for making sure an effect is in scope? */
	protected Type doTypecheck(Environment env) { // if effects have no types then not applicable?
//		if ((effectsList != null) && !effectsList.isEmpty()) {
//			env.lookup(getName());
//		}
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
	@Override
	public DeclType genILType(GenContext ctx) {
		return new EffectDeclType(getName());
	}
	
	@Override
	public wyvern.target.corewyvernIL.decl.Declaration generateDecl(GenContext ctx, GenContext thisContext) {
		// TODO Auto-generated method stub
		throw new RuntimeException("generateDecl not implemented");
//		return null;
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
	
	
}