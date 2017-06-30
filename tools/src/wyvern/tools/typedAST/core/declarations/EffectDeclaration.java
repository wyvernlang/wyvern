/* Copied from wyvern.tools.typedAST.core.declarations.TypeDeclaration */

package wyvern.tools.typedAST.core.declarations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.evaluation.ValueBinding;
import wyvern.tools.typedAST.core.binding.objects.TypeDeclBinding;
import wyvern.tools.typedAST.core.binding.typechecking.LateNameBinding;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.core.expressions.New;
import wyvern.tools.typedAST.core.expressions.TaggedInfo;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.TypeType;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.Reference;


public class EffectDeclaration extends AbstractTypeDeclaration implements CoreAST {
	ArrayList<String> effectsList;
	public EffectDeclaration(String name, String effects) {
		if (effects=="") { 
			effectsList = new ArrayList(); // empty list of effects
		//} else if (Character.isWhitespace(effects.charAt(0))) { // <DSLLINE>?
		} else if (Pattern.compile("[^a-zA-Z,.]").matcher(effects).find()) { // found any non-effect-related chars --> actual DSL block
			  throw new RuntimeException("Probably parsing effects--should not be a DSL block here"); // need to change error type later
		} else {
			effectsList = new ArrayList<String>(Arrays.asList(name.split(", *")));
		}
		
		for (String s:effectsList) {
			System.out.print(s+"|");
		}
	}
	@Override
	public Type getType() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Map<String, TypedAST> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public FileLocation getLocation() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Environment extendType(Environment env, Environment against) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Environment extendName(Environment env, Environment against) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	protected Type doTypecheck(Environment env) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	protected Environment doExtend(Environment old, Environment against) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public EvaluationEnvironment extendWithValue(EvaluationEnvironment old) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void evalDecl(EvaluationEnvironment evalEnv, EvaluationEnvironment declEnv) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public DeclType genILType(GenContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public wyvern.target.corewyvernIL.decl.Declaration generateDecl(GenContext ctx, GenContext thisContext) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public wyvern.target.corewyvernIL.decl.Declaration topLevelGen(GenContext ctx, List<TypedModuleSpec> dependencies) {
		// TODO Auto-generated method stub
		return null;
	}
}