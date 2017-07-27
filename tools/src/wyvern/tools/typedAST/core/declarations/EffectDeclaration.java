package wyvern.tools.typedAST.core.declarations;

import static wyvern.tools.errors.ToolError.reportError;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import java.util.regex.Pattern;

import wyvern.target.corewyvernIL.decl.TypeDeclaration;
import wyvern.target.corewyvernIL.decltype.AbstractTypeMember;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.EffectDeclType;
import wyvern.target.corewyvernIL.decltype.ValDeclType;
import wyvern.target.corewyvernIL.decltype.VarDeclType;
import wyvern.target.corewyvernIL.expression.Effect;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TopLevelContext;
import wyvern.target.corewyvernIL.support.TypeOrEffectGenContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.EvaluationEnvironment;

public class EffectDeclaration extends Declaration {
	private Path path;
	private String name;
	private Set<Effect> effectSet;
	private FileLocation loc;
	
	public EffectDeclaration(String name, String effects, FileLocation fileLocation, boolean optional) { // decltype declarations
	
		if (effects==null) { // undefined (allowed by parser implementation to occur in type and any method annotations)
			if (!optional) // i.e. undefined in module def; if it's part of a method annotation in the type but not module def, should be caught later
				ToolError.reportError(ErrorMessage.UNDEFINED_EFFECT, this, name);
			effectSet = null;
		} else if (effects=="") { // explicitly defined to be empty list of effects
			effectSet = new HashSet<Effect>();
		} else if (Pattern.compile("[^a-zA-Z,.]").matcher(effects).find()) { // found any non-effect-related chars --> probably an actual DSL block
			ToolError.reportError(ErrorMessage.MISTAKEN_DSL, this, name+" = {"+effects+"}");
		} else {
			effectSet = new HashSet<Effect>();
			for (String e : effects.split(", *")) {
				// need something to account for references to effects in the same module def (ie. no period)
				if (e.contains(".")) {
					String[] pathAndID = e.split("\\.");
					effectSet.add(new Effect(new Variable(pathAndID[0]), pathAndID[1], loc));
				} else {
					effectSet.add(new Effect(new Variable("this"), e, loc)); // need to rethink
				}
			}
		}
		
		this.name = name;
		loc = fileLocation;
		
		// send = name, path = "effect" (the module def doesn't seem to be represented in the context?)
		path = new Variable("effect"); // since it's a keyword that shouldn't be used for variable/type names...
	}
	
	public Effect getEffect() {
		return new Effect(getPath(), getName(), getLocation());
	}
	
	public void doPrettyPrint(Appendable dest, String indent) throws IOException {
		dest.append(indent).append("effect ").append(getName()).append(" = ");
		if (effectSet != null)
			dest.append(effectSet.toString());
		dest.append('\n');
	}
	
	@Override
	public void genTopLevel(TopLevelContext tlc) { // type abbrev, (see ValDeclaration for Let), this.E, Type/EffectGenContext for 
//		tlc.addLet(getName(), Util.unitType(), Util.unitValue(), false); // topLevelGen == generateILType is just to get set of effects (not an actual ValueType)
//		tlc.addLet(getName(), getPath().typeCheck(tlc.getContext()), getPath(), false); // probably shouldn't be getPath().typeCheck()
		// or at least, where to extend the context?? In the constructor (for each e in the set)??
		GenContext ctx = tlc.getContext();
//		path = ctx.getContainerForTypeAbbrev(this.getName());// it's a bit odd to place it here...
		ctx = ctx.extend(getName(), new Variable("what"), getPath().getExprType()); 
				//(Variable) ctx.getContainerForTypeAbbrev(getName()), getPath().getExprType()); // so getExprType or typeCheck? Or cast ctx to TypeOrEffect... Or make Effect a ValueType subclass
		tlc.updateContext(ctx); // would be a good sign if addLet does this somewhere
		
//		@Override // Sequence
//		public void genTopLevel(TopLevelContext tlc) {
//			for (TypedAST ast : exps) {
//				ast.genTopLevel(tlc);
//				if (ast instanceof Declaration) {
//					((Declaration)ast).addModuleDecl(tlc);
//				}
//			}
//		}
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
		return new EffectDeclType(getName(), getEffectSet(), getLocation());
//		return new ValDeclType(name, Util.unitType());
	}
	
	@Override
	public wyvern.target.corewyvernIL.decl.Declaration generateDecl(GenContext ctx, GenContext thisContext) {
//		return new wyvern.target.corewyvernIL.decl.ValDeclaration(name, Util.unitType(), Util.unitValue(), loc); // stub
		return new wyvern.target.corewyvernIL.decl.EffectDeclaration(getName(), getEffectSet(), getLocation());
	}
	@Override
	public wyvern.target.corewyvernIL.decl.Declaration topLevelGen(GenContext ctx, List<TypedModuleSpec> dependencies) {
		for (Effect e : getEffectSet()) {
			e.getPath().typeCheck(ctx).checkWellFormed(ctx);
//			((TypeOrEffectGenContext) ctx).getContainerForTypeAbbrev(e.getPath().typeCheck(ctx));
		}
		return new wyvern.target.corewyvernIL.decl.EffectDeclaration(getName(), getEffectSet(), getLocation());
		// ex. fio.read --> fio = Path/Variable which should theoretically be the obj name that corresponds to a type
		// that .checkWellFormed(ctx) can be called on
	}
	
//	@Override
//	public wyvern.target.corewyvernIL.decl.Declaration topLevelGen(
//			GenContext ctx, List<TypedModuleSpec> dependencies) {
//		if (reference == null)
//			reportError(ErrorMessage.NO_ABSTRACT_TYPES_IN_OBJECTS, this);
//		ValueType referenceILType = reference.getILType(ctx); // check each effect in the set for their type
//		referenceILType.checkWellFormed(ctx); // just call it on each effect?
//
//		IExpr metadataExp = null; // ignore
//		if (metadata != null)
//			metadataExp = ((ExpressionAST)metadata).generateIL(ctx, null, null);
//
//			return new TypeDeclaration(getName(), referenceILType, metadataExp, getLocation());
//		}
	
	@Override
	public void addModuleDecl(TopLevelContext tlc) {
		wyvern.target.corewyvernIL.decl.Declaration decl = topLevelGen(tlc.getContext(), null);
		DeclType dt = genILType(tlc.getContext()); // tlc.getContext() isn't actually being used here...
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