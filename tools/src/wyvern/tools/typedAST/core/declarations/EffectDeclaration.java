package wyvern.tools.typedAST.core.declarations;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import java.util.regex.Pattern;

import wyvern.target.corewyvernIL.decltype.AbstractTypeMember;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.EffectDeclType;
import wyvern.target.corewyvernIL.decltype.ValDeclType;
import wyvern.target.corewyvernIL.decltype.VarDeclType;
import wyvern.target.corewyvernIL.expression.Effect;
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TopLevelContext;
import wyvern.target.corewyvernIL.support.Util;
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
	private Path path;
	private String name;
	private Set<Effect> effectSet;
	private FileLocation loc;
	
	public EffectDeclaration(String name, String effects, FileLocation fileLocation) { // decltype declarations
		this.name = name;
		loc = fileLocation;
		path = new Variable(name); // not sure
		
		if (effects==null) { // no definition for identifier "name" -> only possible in effectDeclType (enforced by WyvernParser.jj)
			effectSet = null;
		} else if (effects=="") { // explicitly defined to be empty list of effects
			effectSet = new HashSet<Effect>();
		} else if (Pattern.compile("[^a-zA-Z,.]").matcher(effects).find()) { // found any non-effect-related chars --> probably an actual DSL block
			ToolError.reportError(ErrorMessage.MISTAKEN_DSL, this, name+" = {"+effects+"}");
		} else {
			effectSet = new HashSet<Effect>();
			for (String s : name.split(", *")) {
				String[] pathAndID = s.split("\\.");
				effectSet.add(new Effect(new Variable(pathAndID[0]), pathAndID[1], loc));
			}
		}
	}
	
	public EffectDeclaration(String name, String effects, FileLocation loc, boolean isDeclType) {
		this(name, effects, loc);
		
		/* Dead code (but potentially more desirable): Effects undefined in module def are currently being taken care of by the parser */
		if (effectSet==null && !isDeclType) { // not in the type signature but nothing defined for effect set 
			ToolError.reportError(ErrorMessage.UNDEFINED_EFFECT, this, name);
		}
	}
	
	@Override
	public void genTopLevel(TopLevelContext tlc) { // type abbrev, (see ValDeclaration for Let), this.E, Type/EffectGenContext for 
		tlc.addLet(getName(), Util.unitType(), Util.unitValue(), false);
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
	
	public Variable getPath() {
		return (Variable) path;
	}
	
	@Override
	public DeclType genILType(GenContext ctx) {
//		return new EffectDeclType(getName(), getEffectSet(), getLocation());
		return new ValDeclType(name, Util.unitType());
	}
	
	@Override
	public wyvern.target.corewyvernIL.decl.Declaration generateDecl(GenContext ctx, GenContext thisContext) {
		return new wyvern.target.corewyvernIL.decl.ValDeclaration(name, Util.unitType(), Util.unitValue(), loc); // stub
//		return new wyvern.target.corewyvernIL.decl.EffectDeclaration(new Effect(getPath(), getName(), getEffectSet(), getLocation()));
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
		wyvern.target.corewyvernIL.decl.Declaration decl =
				new wyvern.target.corewyvernIL.decl.ValDeclaration(getName(),
						Util.unitType(),
						new wyvern.target.corewyvernIL.expression.Variable(getName()), getLocation());
			DeclType dt = genILType(tlc.getContext());
			tlc.addModuleDecl(decl,dt);
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
		throw new RuntimeException("extendName not implemented");
//		return null;
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